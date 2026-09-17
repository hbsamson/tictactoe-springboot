package com.svi.tictactoespringboot.dto.response;

import java.time.Instant;
import java.util.UUID;

public record RoomResponse(UUID roomId, String joinCode, UUID hostPlayerId, UUID guestPlayerId, Instant createdAt) {}
