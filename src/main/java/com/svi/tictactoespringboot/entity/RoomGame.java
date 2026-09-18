package com.svi.tictactoespringboot.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("games_by_room")
public class RoomGame {
    @PrimaryKey
    private GameReferenceKey key;

    public RoomGame() {}

    public RoomGame(GameReferenceKey key) {
        this.key = key;
    }

    public GameReferenceKey getKey() {
        return key;
    }

    public void setKey(GameReferenceKey key) {
        this.key = key;
    }
}
