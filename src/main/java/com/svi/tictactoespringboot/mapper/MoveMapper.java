package com.svi.tictactoespringboot.mapper;

import com.svi.tictactoespringboot.dto.response.MoveResponse;
import com.svi.tictactoespringboot.entity.Move;
import org.springframework.stereotype.Component;

@Component 
public class MoveMapper { 
    public MoveResponse toResponse(Move move) { 
        return new MoveResponse(
                move.getKey().getGameId(),
                move.getKey().getMoveNumber(),
                move.getPlayerId(),
                move.getSymbol(),
                move.getX(),
                move.getY(),
                move.getPlayedAt()
        ); 
    } 
}
