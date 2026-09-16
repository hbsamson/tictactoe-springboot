package com.svi.tictactoespringboot.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@PrimaryKeyClass
public class GameReferenceKey implements Serializable {
    @PrimaryKeyColumn(type = PARTITIONED, ordinal = 0) private UUID ownerId;
    @PrimaryKeyColumn(type = CLUSTERED, ordinal = 1, ordering = Ordering.DESCENDING) private Instant createdAt;
    @PrimaryKeyColumn(type = CLUSTERED, ordinal = 2) private UUID gameId;
    public GameReferenceKey() {}
    public GameReferenceKey(UUID ownerId, Instant createdAt, UUID gameId) { this.ownerId = ownerId; this.createdAt = createdAt; this.gameId = gameId; }
    public UUID getOwnerId() { return ownerId; }
    public Instant getCreatedAt() { return createdAt; }
    public UUID getGameId() { return gameId; }
    @Override public boolean equals(Object o) { return o instanceof GameReferenceKey k && Objects.equals(ownerId,k.ownerId) && Objects.equals(createdAt,k.createdAt) && Objects.equals(gameId,k.gameId); }
    @Override public int hashCode() { return Objects.hash(ownerId, createdAt, gameId); }
}
