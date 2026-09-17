package com.svi.tictactoespringboot.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@PrimaryKeyClass
public class MoveKey implements Serializable {
    @PrimaryKeyColumn(type = PARTITIONED, ordinal = 0) private UUID gameId;
    @PrimaryKeyColumn(type = CLUSTERED, ordinal = 1, ordering = Ordering.ASCENDING) private int moveNumber;

    public MoveKey() {}
    public MoveKey(UUID gameId, int moveNumber) {
        this.gameId = gameId;
        this.moveNumber = moveNumber;
    }

    public UUID getGameId() { return gameId; }
    public int getMoveNumber() { return moveNumber; }

    @Override
    public boolean equals(Object o) {
        return o instanceof MoveKey k && moveNumber == k.moveNumber && Objects.equals(gameId, k.gameId);
    }

    @Override public int hashCode() {
        return Objects.hash(gameId, moveNumber);
    }
}
