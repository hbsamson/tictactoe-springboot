package com.svi.tictactoespringboot.dto.response;

import com.svi.tictactoespringboot.enums.RoomStatus;
import java.time.Instant;
import java.util.UUID;

public record RoomResponse(
        UUID roomId,
        String roomKey,
        UUID hostPlayerId,
        UUID guestPlayerId,
        Instant createdAt,
        Instant expiresAt,
        RoomStatus status) {}
