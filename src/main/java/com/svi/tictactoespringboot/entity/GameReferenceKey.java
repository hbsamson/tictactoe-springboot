package com.svi.tictactoespringboot.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@PrimaryKeyClass
public class GameReferenceKey implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @PrimaryKeyColumn(type = PARTITIONED, ordinal = 0)
    private UUID ownerId;

    @PrimaryKeyColumn(type = CLUSTERED, ordinal = 1, ordering = Ordering.DESCENDING)
    private Instant createdAt;

    @PrimaryKeyColumn(type = CLUSTERED, ordinal = 2)
    private UUID gameId;

    public GameReferenceKey() {}

    public GameReferenceKey(UUID ownerId, Instant createdAt, UUID gameId) {
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.gameId = gameId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UUID getGameId() {
        return gameId;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof GameReferenceKey key
                && Objects.equals(ownerId, key.ownerId)
                && Objects.equals(createdAt, key.createdAt)
                && Objects.equals(gameId, key.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, createdAt, gameId);
    }
}
