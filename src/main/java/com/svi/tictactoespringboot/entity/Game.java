package com.svi.tictactoespringboot.entity;

import com.svi.tictactoespringboot.enums.GameStatus;
import com.svi.tictactoespringboot.enums.PlayerSymbol;
import org.springframework.data.annotation.Version;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table("games")
public class Game {
    @PrimaryKey
    @Column("game_id")
    private UUID gameId;

    @Column("room_id")
    private UUID roomId;

    @Column("player_x_id")
    private UUID playerXId;

    @Column("player_o_id")
    private UUID playerOId;

    @Column("current_player_id")
    private UUID currentPlayerId;

    @Column("current_symbol")
    private PlayerSymbol currentSymbol;

    private GameStatus status;

    @Column("winner_id")
    private UUID winnerId;

    private List<String> board;

    @Column("move_count")
    private int moveCount;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Column("completed_at")
    private Instant completedAt;

    @Version
    private Long version;

    public Game() {}
    
    public UUID getGameId() { return gameId; }
    public void setGameId(UUID v) { gameId = v; }
    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID v) { roomId = v; }
    public UUID getPlayerXId() { return playerXId; }
    public void setPlayerXId(UUID v) { playerXId = v; }
    public UUID getPlayerOId() { return playerOId; }
    public void setPlayerOId(UUID v) { playerOId = v; }
    public UUID getCurrentPlayerId() { return currentPlayerId; }
    public void setCurrentPlayerId(UUID v) { currentPlayerId = v; }
    public PlayerSymbol getCurrentSymbol() { return currentSymbol; }
    public void setCurrentSymbol(PlayerSymbol v) { currentSymbol = v; }
    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus v) { status = v; }
    public UUID getWinnerId() { return winnerId; }
    public void setWinnerId(UUID v) { winnerId = v; }
    public List<String> getBoard() { return board == null ? new ArrayList<>() : board; }
    public void setBoard(List<String> v) { board = v; }
    public int getMoveCount() { return moveCount; }
    public void setMoveCount(int v) { moveCount = v; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant v) { createdAt = v; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant v) { updatedAt = v; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant v) { completedAt = v; }
    public Long getVersion() { return version; }
    public void setVersion(Long v) { version = v; }
}
