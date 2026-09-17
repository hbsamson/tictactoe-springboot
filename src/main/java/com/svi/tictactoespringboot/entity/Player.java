package com.svi.tictactoespringboot.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Table("players")
public class Player {
    @PrimaryKey
    @Column("player_id")
    private UUID playerId;

    private String name;

    @Column("created_at")
    private Instant createdAt;

    public Player() {}

    public Player(UUID id, String name, Instant createdAt) {
        this.playerId = id; 
        this.name = name;
        this.createdAt = createdAt; 
    }
    
    public UUID getPlayerId() { return playerId; }
    public void setPlayerId(UUID v) { playerId = v; }
    public String getName() { return name; }
    public void setName(String v) { name = v; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant v) { createdAt = v; }
}
