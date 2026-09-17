package com.svi.tictactoespringboot.mapper;

import com.svi.tictactoespringboot.dto.response.GameResponse;
import com.svi.tictactoespringboot.entity.Game;
import org.springframework.stereotype.Component;
import java.util.List;

@Component 
public class GameMapper { 
    public GameResponse toResponse(Game game) { 
        return new GameResponse(
                game.getGameId(),
                game.getRoomId(),
                game.getPlayerXId(),
                game.getPlayerOId(),
                game.getCurrentPlayerId(),
                game.getCurrentSymbol(),
                game.getStatus(),
                game.getWinnerId(),
                List.copyOf(game.getBoard()),
                game.getMoveCount(),
                game.getCreatedAt(),
                game.getUpdatedAt(),
                game.getCompletedAt()
        ); 
    } 
}
