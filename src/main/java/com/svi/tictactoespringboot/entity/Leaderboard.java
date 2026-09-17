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
    private int points;

    public Leaderboard() {}
    public Leaderboard(UUID id, String name) { playerId = id; playerName = name; }

    public UUID getPlayerId() { return playerId; }
    public void setPlayerId(UUID v) { playerId = v; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String v) { playerName = v; }
    public int getWins() { return wins; }
    public void setWins(int v) { wins = v; }
    public int getDraws() { return draws; }
    public void setDraws(int v) { draws = v; }
    public int getLosses() { return losses; }
    public void setLosses(int v) { losses = v; }
    public int getPoints() { return points; }
    public void setPoints(int v) { points = v; }
}
