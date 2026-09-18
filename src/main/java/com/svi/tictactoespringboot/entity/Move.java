package com.svi.tictactoespringboot.entity;

import com.svi.tictactoespringboot.enums.PlayerSymbol;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Table("moves")
public class Move {
    @PrimaryKey
    private MoveKey key;

    @Column("player_id")
    private UUID playerId;

    private PlayerSymbol symbol;
    @Column("x")
    private int coordinateX;

    @Column("y")
    private int coordinateY;

    @Column("played_at")
    private Instant playedAt;

    public Move() {}
    public Move(MoveKey key, UUID playerId, PlayerSymbol symbol, int coordinateX, int coordinateY, Instant playedAt) {
        this.key = key;
        this.playerId = playerId;
        this.symbol = symbol;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.playedAt = playedAt;
    }

    public MoveKey getKey() { return key; }
    public void setKey(MoveKey key) { this.key = key; }
    public UUID getPlayerId() { return playerId; }
    public void setPlayerId(UUID playerId) { this.playerId = playerId; }
    public PlayerSymbol getSymbol() { return symbol; }
    public void setSymbol(PlayerSymbol symbol) { this.symbol = symbol; }
    public int getX() { return coordinateX; }
    public void setX(int coordinateX) { this.coordinateX = coordinateX; }
    public int getY() { return coordinateY; }
    public void setY(int coordinateY) { this.coordinateY = coordinateY; }
    public Instant getPlayedAt() { return playedAt; }
    public void setPlayedAt(Instant playedAt) { this.playedAt = playedAt; }
}
