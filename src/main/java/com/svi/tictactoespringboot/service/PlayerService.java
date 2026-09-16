package com.svi.tictactoespringboot.service;
import com.svi.tictactoespringboot.dto.request.CreatePlayerRequest;
import com.svi.tictactoespringboot.dto.response.GameResponse;
import com.svi.tictactoespringboot.dto.response.PlayerResponse;
import java.util.List;
import java.util.UUID;

public interface PlayerService { 
    PlayerResponse create(CreatePlayerRequest request); 
    List<PlayerResponse> list(); PlayerResponse get(UUID id);
    List<GameResponse> games(UUID id); 
}
