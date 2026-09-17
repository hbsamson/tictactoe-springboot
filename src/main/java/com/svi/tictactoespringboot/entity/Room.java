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
    public void setRoomId(UUID v) { roomId = v; }
    public String getJoinCode() { return roomKey; }
    public void setJoinCode(String v) { roomKey = v; }
    public UUID getHostPlayerId() { return hostPlayerId; }
    public void setHostPlayerId(UUID v) { hostPlayerId = v; }
    public UUID getGuestPlayerId() { return guestPlayerId; }
    public void setGuestPlayerId(UUID v) { guestPlayerId = v; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant v) { createdAt = v; }
}
