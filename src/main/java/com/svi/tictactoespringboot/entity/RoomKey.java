package com.svi.tictactoespringboot.entity;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("rooms_by_key")
public class RoomKey {
    @PrimaryKey
    @Column("room_key")
    private String roomKey;

    @Column("room_id")
    private UUID roomId;

    @Column("expires_at")
    private Instant expiresAt;

    public RoomKey() {}

    public RoomKey(String roomKey, UUID roomId, Instant expiresAt) {
        this.roomKey = roomKey;
        this.roomId = roomId;
        this.expiresAt = expiresAt;
    }

    public String getRoomKey() { return roomKey; }
    public UUID getRoomId() { return roomId; }
    public Instant getExpiresAt() { return expiresAt; }
}
