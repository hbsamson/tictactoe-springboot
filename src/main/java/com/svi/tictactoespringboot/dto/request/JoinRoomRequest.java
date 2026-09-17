package com.svi.tictactoespringboot.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record JoinRoomRequest(@NotNull UUID playerId) {}
