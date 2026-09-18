package com.svi.tictactoespringboot.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StartGameRequest(@NotNull UUID playerId) {}
