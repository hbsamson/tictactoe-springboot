package com.svi.tictactoespringboot.entity;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("games_by_player")
public class PlayerGame {
    @PrimaryKey private GameReferenceKey key;

    public PlayerGame() {}

    public PlayerGame(GameReferenceKey key) {
        this.key = key;
    }

    public GameReferenceKey getKey() { return key; }
    public void setKey(GameReferenceKey v) { key = v; }
}
