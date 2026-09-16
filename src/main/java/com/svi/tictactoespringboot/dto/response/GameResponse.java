package com.svi.tictactoespringboot.dto.response;
import com.svi.tictactoespringboot.enums.GameStatus;
import com.svi.tictactoespringboot.enums.PlayerSymbol;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GameResponse(UUID gameId, UUID roomId, UUID playerXId, UUID playerOId, UUID currentPlayerId,
 PlayerSymbol currentSymbol, GameStatus status, UUID winnerId, List<String> board, int moveCount,
 Instant createdAt, Instant updatedAt, Instant completedAt) {}
