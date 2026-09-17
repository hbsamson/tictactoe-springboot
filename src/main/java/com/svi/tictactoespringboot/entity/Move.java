package com.svi.tictactoespringboot.entity;

import com.svi.tictactoespringboot.enums.PlayerSymbol;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Table("moves_by_game")
public class Move {
    @PrimaryKey
    private MoveKey key;

    @Column("player_id")
    private UUID playerId;

    private PlayerSymbol symbol;
    private int x;
    private int y;

    @Column("played_at")
    private Instant playedAt;

    public Move() {}
    public Move(MoveKey key, UUID playerId, PlayerSymbol symbol, int x, int y, Instant playedAt) {
        this.key = key;
        this.playerId = playerId;
        this.symbol = symbol;
        this.x = x;
        this.y = y;
        this.playedAt = playedAt;
    }

    public MoveKey getKey() { return key; }
    public void setKey(MoveKey v) { key = v; }
    public UUID getPlayerId() { return playerId; }
    public void setPlayerId(UUID v) { playerId = v; }
    public PlayerSymbol getSymbol() { return symbol; }
    public void setSymbol(PlayerSymbol v) { symbol = v; }
    public int getX() { return x; }
    public void setX(int v) { x = v; }
    public int getY() { return y; }
    public void setY(int v) { y = v; }
    public Instant getPlayedAt() { return playedAt; }
    public void setPlayedAt(Instant v) { playedAt = v; }
}
