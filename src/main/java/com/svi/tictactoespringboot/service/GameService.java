package com.svi.tictactoespringboot.service;
import com.svi.tictactoespringboot.dto.request.MakeMoveRequest;
import com.svi.tictactoespringboot.dto.response.*;
import java.util.List;
import java.util.UUID;

public interface GameService {
    GameResponse createGame(UUID roomId);
    GameResponse getGame(UUID gameId);
    BoardResponse getBoard(UUID gameId);
    GameStatusResponse getStatus(UUID gameId);
    List<MoveResponse> getMoves(UUID gameId);
    GameResponse makeMove(UUID gameId, MakeMoveRequest request);
    GameResponse rematch(UUID gameId);
}
