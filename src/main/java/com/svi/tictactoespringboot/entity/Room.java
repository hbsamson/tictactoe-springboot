package com.svi.tictactoespringboot.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Table("rooms")
public class Room {
    @PrimaryKey private UUID roomId;
    private String joinCode;
    private UUID hostPlayerId;
    private UUID guestPlayerId;
    private int gameCount;
    private Instant createdAt;

    public Room() {}
    
    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID v) { roomId = v; }
    public String getJoinCode() { return joinCode; }
    public void setJoinCode(String v) { joinCode = v; }
    public UUID getHostPlayerId() { return hostPlayerId; }
    public void setHostPlayerId(UUID v) { hostPlayerId = v; }
    public UUID getGuestPlayerId() { return guestPlayerId; }
    public void setGuestPlayerId(UUID v) { guestPlayerId = v; }
    public int getGameCount() { return gameCount; }
    public void setGameCount(int v) { gameCount = v; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant v) { createdAt = v; }
}
