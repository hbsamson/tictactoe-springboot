package com.svi.tictactoespringboot.controller;

import com.svi.tictactoespringboot.dto.request.MakeMoveRequest;
import com.svi.tictactoespringboot.dto.request.StartGameRequest;
import com.svi.tictactoespringboot.dto.response.BoardResponse;
import com.svi.tictactoespringboot.dto.response.GameResponse;
import com.svi.tictactoespringboot.dto.response.GameStatusResponse;
import com.svi.tictactoespringboot.dto.response.MoveResponse;
import com.svi.tictactoespringboot.service.GameService;
import com.svi.tictactoespringboot.service.RoomService;
import com.svi.tictactoespringboot.service.LeaderboardService;
import com.svi.tictactoespringboot.service.RealtimeUpdatePublisher;
import com.svi.tictactoespringboot.enums.GameStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class GameController {
    private final GameService gameService;
    private final RoomService roomService;
    private final LeaderboardService leaderboardService;
    private final RealtimeUpdatePublisher realtimeUpdates;

    public GameController(GameService gameService, RoomService roomService,
            LeaderboardService leaderboardService, RealtimeUpdatePublisher realtimeUpdates) {
        this.gameService = gameService;
        this.roomService = roomService;
        this.leaderboardService = leaderboardService;
        this.realtimeUpdates = realtimeUpdates;
    }

    @PostMapping("/rooms/{roomKey}/games")
    public ResponseEntity<GameResponse> createGame(
            @PathVariable String roomKey, @Valid @RequestBody StartGameRequest request) {
        UUID roomId = roomService.requireActiveRoom(roomKey, request.playerId());
        GameResponse game = gameService.createGame(roomId);
        realtimeUpdates.publishLobby(roomService.listRooms());
        realtimeUpdates.publishBoard(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }

    @GetMapping("/games/{gameId}")
    public GameResponse getGame(@PathVariable UUID gameId) {
        return gameService.getGame(gameId);
    }

    @GetMapping("/games/{gameId}/board")
    public BoardResponse getBoard(@PathVariable UUID gameId) {
        return gameService.getBoard(gameId);
    }

    @GetMapping("/games/{gameId}/status")
    public GameStatusResponse getGameStatus(@PathVariable UUID gameId) {
        return gameService.getGameStatus(gameId);
    }

    @GetMapping("/games/{gameId}/moves")
    public List<MoveResponse> getGameMoves(@PathVariable UUID gameId) {
        return gameService.getGameMoves(gameId);
    }

    @PostMapping("/games/{gameId}/moves")
    public GameResponse makeMove(@PathVariable UUID gameId, @Valid @RequestBody MakeMoveRequest request) {
        GameResponse game = gameService.makeMove(gameId, request);
        realtimeUpdates.publishBoard(game);
        if (game.status() == GameStatus.WON || game.status() == GameStatus.DRAW) {
            realtimeUpdates.publishLeaderboard(leaderboardService.getLeaderboard());
        }
        return game;
    }

    @PostMapping("/games/{gameId}/rematches")
    public ResponseEntity<GameResponse> createRematch(@PathVariable UUID gameId) {
        GameResponse game = gameService.createRematch(gameId);
        realtimeUpdates.publishBoard(gameService.getGame(gameId));
        realtimeUpdates.publishBoard(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }
}
