package com.svi.tictactoespringboot.dto.response;
import java.util.List;
import java.util.UUID;

public record BoardResponse(UUID gameId, List<String> cells) {}
