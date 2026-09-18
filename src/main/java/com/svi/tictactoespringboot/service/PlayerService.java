package com.svi.tictactoespringboot.service;

import com.svi.tictactoespringboot.dto.request.CreatePlayerRequest;
import com.svi.tictactoespringboot.dto.response.GameResponse;
import com.svi.tictactoespringboot.dto.response.PlayerResponse;
import java.util.List;
import java.util.UUID;

public interface PlayerService {
    PlayerResponse createPlayer(CreatePlayerRequest request);
    List<PlayerResponse> listPlayers();
    PlayerResponse getPlayer(UUID playerId);
    List<GameResponse> getPlayerGames(UUID playerId);
}
