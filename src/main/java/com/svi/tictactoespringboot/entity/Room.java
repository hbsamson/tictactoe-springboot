package com.svi.tictactoespringboot.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Table("rooms")
public class Room {
    @PrimaryKey
    @Column("room_id")
    private UUID roomId;

    @Column("room_key")
    private String roomKey;

    @Column("host_player_id")
    private UUID hostPlayerId;

    @Column("guest_player_id")
    private UUID guestPlayerId;

    @Column("created_at")
    private Instant createdAt;

    public Room() {}

    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID roomId) { this.roomId = roomId; }
    public String getJoinCode() { return roomKey; }
    public void setJoinCode(String joinCode) { this.roomKey = joinCode; }
    public UUID getHostPlayerId() { return hostPlayerId; }
    public void setHostPlayerId(UUID hostPlayerId) { this.hostPlayerId = hostPlayerId; }
    public UUID getGuestPlayerId() { return guestPlayerId; }
    public void setGuestPlayerId(UUID guestPlayerId) { this.guestPlayerId = guestPlayerId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
