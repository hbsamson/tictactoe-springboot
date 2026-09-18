package com.svi.tictactoespringboot.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.util.UUID;

@Table("leaderboard")
public class Leaderboard {
    @PrimaryKey
    @Column("player_id")
    private UUID playerId;

    @Column("player_name")
    private String playerName;
    private int wins;
    private int draws;
    private int losses;

    public Leaderboard() {}
    public Leaderboard(UUID id, String name) { playerId = id; playerName = name; }

    public UUID getPlayerId() { return playerId; }
    public void setPlayerId(UUID playerId) { this.playerId = playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }
    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }
    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }
}
