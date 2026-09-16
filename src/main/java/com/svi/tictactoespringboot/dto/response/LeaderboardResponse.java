package com.svi.tictactoespringboot.dto.response;
import java.util.List;
import java.util.UUID;
public record LeaderboardResponse(List<Entry> entries) {
 public record Entry(int rank, UUID playerId, String playerName, int wins, int draws, int losses, int points) {}
}
