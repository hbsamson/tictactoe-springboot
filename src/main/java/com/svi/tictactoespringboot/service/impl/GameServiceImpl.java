package com.svi.tictactoespringboot.service.impl;

import com.svi.tictactoespringboot.dto.request.MakeMoveRequest;
import com.svi.tictactoespringboot.dto.response.BoardResponse;
import com.svi.tictactoespringboot.dto.response.GameResponse;
import com.svi.tictactoespringboot.dto.response.GameStatusResponse;
import com.svi.tictactoespringboot.dto.response.MoveResponse;
import com.svi.tictactoespringboot.entity.Game;
import com.svi.tictactoespringboot.entity.GameReferenceKey;
import com.svi.tictactoespringboot.entity.Leaderboard;
import com.svi.tictactoespringboot.entity.Move;
import com.svi.tictactoespringboot.entity.MoveKey;
import com.svi.tictactoespringboot.entity.PlayerGame;
import com.svi.tictactoespringboot.entity.RoomGame;
import com.svi.tictactoespringboot.enums.GameStatus;
import com.svi.tictactoespringboot.enums.PlayerSymbol;
import com.svi.tictactoespringboot.exception.ApiException;
import com.svi.tictactoespringboot.mapper.GameMapper;
import com.svi.tictactoespringboot.mapper.MoveMapper;
import com.svi.tictactoespringboot.repository.GameRepository;
import com.svi.tictactoespringboot.repository.LeaderboardRepository;
import com.svi.tictactoespringboot.repository.MoveRepository;
import com.svi.tictactoespringboot.repository.PlayerGameRepository;
import com.svi.tictactoespringboot.repository.PlayerRepository;
import com.svi.tictactoespringboot.repository.RoomGameRepository;
import com.svi.tictactoespringboot.repository.RoomRepository;
import com.svi.tictactoespringboot.service.GameService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

import static com.svi.tictactoespringboot.constants.ResponseMessage.*;

@Service
public class GameServiceImpl implements GameService {
    private static final List<String> EMPTY_BOARD =
            List.of("", "", "", "", "", "", "", "", "");

    private static final int[][] WINNING_LINES = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // horizontal
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // vertical
            {0, 4, 8}, {2, 4, 6} // diagonal
    };

    private final GameRepository games;
    private final RoomRepository rooms;
    private final PlayerRepository players;
    private final MoveRepository moves;
    private final PlayerGameRepository playerGames;
    private final RoomGameRepository roomGames;
    private final LeaderboardRepository leaderboard;
    private final GameMapper gameMapper;
    private final MoveMapper moveMapper;

    public GameServiceImpl(
            GameRepository games,
            RoomRepository rooms,
            PlayerRepository players,
            MoveRepository moves,
            PlayerGameRepository playerGames,
            RoomGameRepository roomGames,
            LeaderboardRepository leaderboard,
            GameMapper gameMapper,
            MoveMapper moveMapper) {
        this.games = games;
        this.rooms = rooms;
        this.players = players;
        this.moves = moves;
        this.playerGames = playerGames;
        this.roomGames = roomGames;
        this.leaderboard = leaderboard;
        this.gameMapper = gameMapper;
        this.moveMapper = moveMapper;
    }

    @Override
    public GameResponse createGame(UUID roomId) {
        return gameMapper.toResponse(create(roomId));
    }

    @Override
    public GameResponse getGame(UUID gameId) {
        return gameMapper.toResponse(find(gameId));
    }

    @Override
    public BoardResponse getBoard(UUID gameId) {
        Game game = find(gameId);
        return new BoardResponse(gameId, List.copyOf(game.getBoard()));
    }

    @Override
    public GameStatusResponse getGameStatus(UUID gameId) {
        Game game = find(gameId);
        return new GameStatusResponse(
                gameId, game.getStatus(), game.getCurrentPlayerId(), game.getWinnerId());
    }

    @Override
    public List<MoveResponse> getGameMoves(UUID gameId) {
        find(gameId);
        return moves.findByKeyGameId(gameId).stream().map(moveMapper::toResponse).toList();
    }

    @Override
    public GameResponse makeMove(UUID gameId, MakeMoveRequest request) {
        Game game = find(gameId);
        validateMove(game, request);

        int cell = request.y() * 3 + request.x();
        List<String> board = new ArrayList<>(game.getBoard());
        if (!board.get(cell).isEmpty()) {
            throw ApiException.conflict(CELL_OCCUPIED);
        }

        PlayerSymbol playedSymbol = game.getCurrentSymbol();
        board.set(cell, playedSymbol.name());
        game.setBoard(board);
        game.setMoveCount(game.getMoveCount() + 1);
        Instant playedAt = Instant.now();
        game.setUpdatedAt(playedAt);
        advanceGame(game, request.playerId(), playedSymbol, playedAt);

        Game saved = games.save(game);
        moves.save(new Move(
                    new MoveKey(gameId, saved.getMoveCount()),
                    request.playerId(),
                    playedSymbol,
                    request.x(),
                    request.y(),
                    playedAt)
        );

        if (saved.getStatus() == GameStatus.WON || saved.getStatus() == GameStatus.DRAW) {
            recordResult(saved);
        }
        return gameMapper.toResponse(saved);
    }

    @Override
    public GameResponse createRematch(UUID gameId) {
        Game previous = find(gameId);
        if (previous.getStatus() == GameStatus.IN_PROGRESS) {
            Instant abandonedAt = Instant.now();
            previous.setStatus(GameStatus.ABANDONED);
            previous.setCurrentPlayerId(null);
            previous.setCurrentSymbol(null);
            previous.setCompletedAt(abandonedAt);
            previous.setUpdatedAt(abandonedAt);
            games.save(previous);
        }
        return gameMapper.toResponse(create(previous.getRoomId()));
    }

    private Game create(UUID roomId) {
        var room = rooms.findById(roomId).orElseThrow(() -> ApiException.notFound(ROOM_NOT_FOUND, roomId));
        if (room.getGuestPlayerId() == null) {
            throw ApiException.conflict(ROOM_NOT_READY);
        }

        UUID playerX = room.getHostPlayerId();
        UUID playerO = room.getGuestPlayerId();
        Instant createdAt = Instant.now();

        Game game = new Game();
        game.setGameId(UUID.randomUUID());
        game.setRoomId(roomId);
        game.setPlayerXId(playerX);
        game.setPlayerOId(playerO);
        game.setCurrentPlayerId(playerX);
        game.setCurrentSymbol(PlayerSymbol.X);
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setBoard(new ArrayList<>(EMPTY_BOARD));
        game.setCreatedAt(createdAt);
        game.setUpdatedAt(createdAt);
        Game saved = games.save(game);

        roomGames.save(
                new RoomGame(new GameReferenceKey(roomId, createdAt, saved.getGameId())));
        playerGames.save(
                new PlayerGame(new GameReferenceKey(playerX, createdAt, saved.getGameId())));
        playerGames.save(
                new PlayerGame(new GameReferenceKey(playerO, createdAt, saved.getGameId())));
        return saved;
    }

    private void validateMove(Game game, MakeMoveRequest request) {
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw ApiException.conflict(GAME_NOT_ACTIVE);
        }
        boolean member =
                request.playerId().equals(game.getPlayerXId())
                        || request.playerId().equals(game.getPlayerOId());
        if (!member) {
            throw ApiException.conflict(PLAYER_NOT_IN_GAME);
        }
        if (!request.playerId().equals(game.getCurrentPlayerId())) {
            throw ApiException.conflict(OUT_OF_TURN);
        }
    }

    private void advanceGame(Game game, UUID playerId, PlayerSymbol playedSymbol, Instant playedAt) {
        if (hasWon(game.getBoard(), playedSymbol)) {
            game.setStatus(GameStatus.WON);
            game.setWinnerId(playerId);
            game.setCurrentPlayerId(null);
            game.setCurrentSymbol(null);
            game.setCompletedAt(playedAt);
        } else if (game.getMoveCount() == 9) {
            game.setStatus(GameStatus.DRAW);
            game.setCurrentPlayerId(null);
            game.setCurrentSymbol(null);
            game.setCompletedAt(playedAt);
        } else {
            boolean wasXMove = playedSymbol == PlayerSymbol.X;
            game.setCurrentSymbol(wasXMove ? PlayerSymbol.O : PlayerSymbol.X);
            game.setCurrentPlayerId(wasXMove ? game.getPlayerOId() : game.getPlayerXId());
        }
    }

    private void recordResult(Game game) {
        Leaderboard playerX = leaderboardEntry(game.getPlayerXId());
        Leaderboard playerO = leaderboardEntry(game.getPlayerOId());
        if (game.getStatus() == GameStatus.DRAW) {
            playerX.setDraws(playerX.getDraws() + 1);
            playerO.setDraws(playerO.getDraws() + 1);
        } else {
            Leaderboard winner = game.getWinnerId().equals(playerX.getPlayerId()) ? playerX : playerO;
            Leaderboard loser = winner == playerX ? playerO : playerX;
            winner.setWins(winner.getWins() + 1);
            loser.setLosses(loser.getLosses() + 1);
        }
        leaderboard.save(playerX);
        leaderboard.save(playerO);
    }

    private Leaderboard leaderboardEntry(UUID playerId) {
        return leaderboard
                .findById(playerId)
                .orElseGet(
                        () -> {
                            var player = players.findById(playerId)
                                                .orElseThrow(
                                                    () -> ApiException.notFound(PLAYER_NOT_FOUND, playerId));
                            return new Leaderboard(playerId, player.getName());
                        });
    }

    private Game find(UUID gameId) {
        return games.findById(gameId).orElseThrow(() -> ApiException.notFound(GAME_NOT_FOUND, gameId));
    }

    private boolean hasWon(List<String> board, PlayerSymbol symbol) {
        String value = symbol.name();
        for (int[] line : WINNING_LINES) {
            if (value.equals(board.get(line[0]))
                    && value.equals(board.get(line[1]))
                    && value.equals(board.get(line[2]))) {
                return true;
            }
        }
        return false;
    }
}
