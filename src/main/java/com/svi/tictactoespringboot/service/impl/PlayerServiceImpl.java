package com.svi.tictactoespringboot.service.impl;
import com.svi.tictactoespringboot.dto.request.CreatePlayerRequest;
import com.svi.tictactoespringboot.dto.response.*;
import com.svi.tictactoespringboot.entity.Player;
import com.svi.tictactoespringboot.entity.PlayerGame;
import com.svi.tictactoespringboot.exception.ApiException;
import com.svi.tictactoespringboot.mapper.*;
import com.svi.tictactoespringboot.repository.*;
import com.svi.tictactoespringboot.service.PlayerService;
import com.svi.tictactoespringboot.util.GameHistoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import static com.svi.tictactoespringboot.constants.ResponseMessage.PLAYER_NOT_FOUND;

@Service
public class PlayerServiceImpl implements PlayerService {
    private static final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);

    private final PlayerRepository players;
    private final PlayerGameRepository histories;
    private final GameRepository games;
    private final PlayerMapper playerMapper;
    private final GameMapper gameMapper;

    public PlayerServiceImpl(PlayerRepository players, PlayerGameRepository histories, GameRepository games, PlayerMapper playerMapper, GameMapper gameMapper) {
        this.players = players;
        this.histories = histories;
        this.games = games;
        this.playerMapper = playerMapper;
        this.gameMapper = gameMapper;
    }

 public PlayerResponse createPlayer(CreatePlayerRequest request) {
     Player saved = players.save(
             new Player(UUID.randomUUID(),
                     request.name().trim(),
                     Instant.now()));
     log.info("Created player: playerId={}", saved.getPlayerId());
     return playerMapper.toResponse(saved);
    }

    public List<PlayerResponse> listPlayers() {
        var result=new ArrayList<PlayerResponse>();
        players.findAll().forEach(player -> result.add(playerMapper.toResponse(player)));
        result.sort(Comparator.comparing(PlayerResponse::name,String.CASE_INSENSITIVE_ORDER));
        return result;
    }

     public PlayerResponse getPlayer(UUID playerId) {
         return playerMapper.toResponse(find(playerId));
     }

     public List<GameResponse> getPlayerGames(UUID playerId) {
         find(playerId);
         return GameHistoryUtils.toResponses(
                 histories.findByKeyOwnerId(playerId).stream().map(PlayerGame::getKey),
                 games,
                 gameMapper);
     }

     private Player find(UUID id) {
         return players.findById(id).orElseThrow(()->ApiException.notFound(PLAYER_NOT_FOUND,id));
     }

}
