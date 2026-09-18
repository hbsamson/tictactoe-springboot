package com.svi.tictactoespringboot.entity;

import com.svi.tictactoespringboot.enums.RoomStatus;
import org.springframework.data.annotation.Version;
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

    private RoomStatus status;

    @Column("expires_at")
    private Instant expiresAt;

    @Version
    private Long version;

    public Room() {}

    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID roomId) { this.roomId = roomId; }
    public String getRoomKey() { return roomKey; }
    public void setRoomKey(String roomKey) { this.roomKey = roomKey; }
    public UUID getHostPlayerId() { return hostPlayerId; }
    public void setHostPlayerId(UUID hostPlayerId) { this.hostPlayerId = hostPlayerId; }
    public UUID getGuestPlayerId() { return guestPlayerId; }
    public void setGuestPlayerId(UUID guestPlayerId) { this.guestPlayerId = guestPlayerId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
