package com.svi.tictactoespringboot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePlayerRequest(@NotBlank @Size(max=80) String name) {}
