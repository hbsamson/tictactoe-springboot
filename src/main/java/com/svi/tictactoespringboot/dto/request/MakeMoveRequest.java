package com.svi.tictactoespringboot.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MakeMoveRequest(@NotNull UUID playerId, @Min(0) @Max(2) int x, @Min(0) @Max(2) int y) {}
