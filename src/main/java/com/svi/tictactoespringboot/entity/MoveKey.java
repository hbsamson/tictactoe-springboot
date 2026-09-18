package com.svi.tictactoespringboot.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@PrimaryKeyClass
public class MoveKey implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @PrimaryKeyColumn(type = PARTITIONED, ordinal = 0)
    private UUID gameId;

    @PrimaryKeyColumn(type = CLUSTERED, ordinal = 1, ordering = Ordering.ASCENDING)
    private int moveNumber;

    public MoveKey() {}

    public MoveKey(UUID gameId, int moveNumber) {
        this.gameId = gameId;
        this.moveNumber = moveNumber;
    }

    public UUID getGameId() {
        return gameId;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MoveKey key
                && moveNumber == key.moveNumber
                && Objects.equals(gameId, key.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, moveNumber);
    }
}
