package com.svi.tictactoespringboot.dto.response;
import com.svi.tictactoespringboot.enums.PlayerSymbol;
import java.time.Instant;
import java.util.UUID;

public record MoveResponse(UUID gameId, int moveNumber, UUID playerId, PlayerSymbol symbol, int x, int y, Instant playedAt) {}
