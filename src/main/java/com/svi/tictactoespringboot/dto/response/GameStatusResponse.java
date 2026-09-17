package com.svi.tictactoespringboot.dto.response;

import com.svi.tictactoespringboot.enums.GameStatus;
import java.util.UUID;

public record GameStatusResponse(UUID gameId, GameStatus status, UUID currentPlayerId, UUID winnerId) {}
