package com.svi.tictactoespringboot.dto.response;

import java.time.Instant;
import java.util.UUID;

public record PlayerResponse(UUID playerId, String name, String avatarUrl, Instant createdAt) {}
