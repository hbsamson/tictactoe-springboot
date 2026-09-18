package com.svi.tictactoespringboot.service.impl;
import com.svi.tictactoespringboot.dto.request.*;
import com.svi.tictactoespringboot.dto.response.*;
import com.svi.tictactoespringboot.entity.Room;
import com.svi.tictactoespringboot.entity.RoomGame;
import com.svi.tictactoespringboot.entity.RoomKey;
import com.svi.tictactoespringboot.enums.RoomStatus;
import com.svi.tictactoespringboot.exception.ApiException;
import com.svi.tictactoespringboot.mapper.*;
import com.svi.tictactoespringboot.repository.*;
import com.svi.tictactoespringboot.service.RoomService;
import com.svi.tictactoespringboot.util.GameHistoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.InsertOptions;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import static com.svi.tictactoespringboot.constants.ResponseMessage.*;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository rooms;
    private final PlayerRepository players;
    private final RoomGameRepository histories;
    private final GameRepository games;
    private final RoomMapper roomMapper;
    private final GameMapper gameMapper;
    private final RoomKeyRepository roomKeys;
    private final Duration waitingTimeout;
    private final CassandraOperations cassandra;

    public RoomServiceImpl(
            RoomRepository rooms,
            PlayerRepository players,
            RoomGameRepository histories,
            GameRepository games,
            RoomMapper roomMapper,
            GameMapper gameMapper,
            RoomKeyRepository roomKeys,
            CassandraOperations cassandra,
            @Value("${app.room.waiting-timeout}") Duration waitingTimeout) {
        this.rooms = rooms;
        this.players = players;
        this.histories = histories;
        this.games = games;
        this.roomMapper = roomMapper;
        this.gameMapper = gameMapper;
        this.roomKeys = roomKeys;
        this.cassandra = cassandra;
        this.waitingTimeout = waitingTimeout;
    }

    public RoomResponse createRoom(CreateRoomRequest request) {
        requirePlayer(request.hostPlayerId());
        var room = new Room();
        room.setRoomId(UUID.randomUUID());
        room.setHostPlayerId(request.hostPlayerId());
        Instant createdAt = Instant.now();
        room.setCreatedAt(createdAt);
        room.setExpiresAt(createdAt.plus(waitingTimeout));
        room.setStatus(RoomStatus.WAITING);
        room.setRoomKey(reserveRoomKey(room.getRoomId(), room.getExpiresAt()));
        Room saved = rooms.save(room);
        return toResponse(saved);
    }

    public List<RoomResponse> listRooms() {
        var result=new ArrayList<RoomResponse>();
        rooms.findAll().forEach(room -> result.add(toResponse(expireIfNecessary(room))));
        result.sort(Comparator.comparing(RoomResponse::createdAt).reversed());
        return result;
    }

    public RoomResponse getRoom(UUID roomId) {
        return toResponse(expireIfNecessary(find(roomId)));
    }

    public RoomResponse joinRoom(String roomKey, JoinRoomRequest request) {
        var room = findByRoomKey(roomKey);
        expireIfNecessary(room);
        if (room.getStatus() == RoomStatus.EXPIRED) throw ApiException.conflict(ROOM_EXPIRED);
        requirePlayer(request.playerId());
        if (request.playerId().equals(room.getHostPlayerId())) throw ApiException.conflict(HOST_CANNOT_JOIN);
        if (room.getGuestPlayerId()!=null && !room.getGuestPlayerId().equals(request.playerId())) throw ApiException.conflict(ROOM_FULL);
        room.setGuestPlayerId(request.playerId());
        room.setStatus(RoomStatus.ACTIVE);
        return toResponse(rooms.save(room));
    }

    public UUID requireActiveRoom(String roomKey, UUID hostPlayerId) {
        var room = findByRoomKey(roomKey);
        expireIfNecessary(room);
        if (room.getStatus() == RoomStatus.EXPIRED) throw ApiException.conflict(ROOM_EXPIRED);
        if (!room.getHostPlayerId().equals(hostPlayerId)) throw ApiException.conflict(ONLY_HOST_CAN_START);
        if (room.getStatus() == RoomStatus.IN_GAME) throw ApiException.conflict(ROOM_ALREADY_STARTED);
        if (room.getStatus() != RoomStatus.ACTIVE || room.getGuestPlayerId() == null) {
            throw ApiException.conflict(ROOM_NOT_READY);
        }
        room.setStatus(RoomStatus.IN_GAME);
        rooms.save(room);
        return room.getRoomId();
    }

    public List<GameResponse> getRoomGames(UUID roomId) {
        find(roomId);
        return GameHistoryUtils.toResponses(
                histories.findByKeyOwnerId(roomId).stream().map(RoomGame::getKey),
                games,
                gameMapper);
    }

    private Room find(UUID id) {
        return rooms.findById(id).orElseThrow(()->ApiException.notFound(ROOM_NOT_FOUND,id));
    }

    private Room findByRoomKey(String value) {
        String roomKey = value.trim().toUpperCase(Locale.ROOT);
        RoomKey reference = roomKeys.findById(roomKey)
                .orElseThrow(() -> ApiException.notFound(ROOM_KEY_NOT_FOUND, roomKey));
        return find(reference.getRoomId());
    }

    private Room expireIfNecessary(Room room) {
        if (room.getStatus() == RoomStatus.WAITING
                && room.getExpiresAt() != null
                && !Instant.now().isBefore(room.getExpiresAt())) {
            room.setStatus(RoomStatus.EXPIRED);
            return rooms.save(room);
        }
        return room;
    }

    private String reserveRoomKey(UUID roomId, Instant expiresAt) {
        InsertOptions onlyIfUnused = InsertOptions.builder().withIfNotExists().build();
        for (int attempt = 0; attempt < 20; attempt++) {
            String candidate = UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 6)
                    .toUpperCase(Locale.ROOT);
            if (cassandra.insert(new RoomKey(candidate, roomId, expiresAt), onlyIfUnused).wasApplied()) {
                return candidate;
            }
        }
        throw new IllegalStateException("Unable to allocate a unique room key");
    }

    private RoomResponse toResponse(Room room) {
        return roomMapper.toResponse(room);
    }

    private void requirePlayer(UUID id) {
        if (!players.existsById(id)) throw ApiException.notFound(PLAYER_NOT_FOUND,id);
    }
}
