package com.svi.tictactoespringboot.dto.response;

import java.util.List;
import java.util.UUID;

public record GetGamesResponse (List<GameSummary> games) {
    public record GameSummary(
        UUID gameId,
        UUID playerX,
        UUID playerO
    ) {}
}
