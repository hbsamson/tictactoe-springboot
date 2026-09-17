package com.svi.tictactoespringboot.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateRoomRequest(@NotNull UUID hostPlayerId) {}
