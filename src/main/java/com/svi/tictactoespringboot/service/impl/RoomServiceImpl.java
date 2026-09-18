package com.svi.tictactoespringboot.service.impl;
import com.svi.tictactoespringboot.dto.request.*;
import com.svi.tictactoespringboot.dto.response.*;
import com.svi.tictactoespringboot.entity.Room;
import com.svi.tictactoespringboot.entity.RoomGame;
import com.svi.tictactoespringboot.exception.ApiException;
import com.svi.tictactoespringboot.mapper.*;
import com.svi.tictactoespringboot.repository.*;
import com.svi.tictactoespringboot.service.RoomService;
import com.svi.tictactoespringboot.util.GameHistoryUtils;
import org.springframework.stereotype.Service;
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

    public RoomServiceImpl(
            RoomRepository rooms,
            PlayerRepository players,
            RoomGameRepository histories,
            GameRepository games,
            RoomMapper roomMapper,
            GameMapper gameMapper) {
        this.rooms = rooms;
        this.players = players;
        this.histories = histories;
        this.games = games;
        this.roomMapper = roomMapper;
        this.gameMapper = gameMapper;
    }

    public RoomResponse createRoom(CreateRoomRequest request) {
        requirePlayer(request.hostPlayerId());
        var room = new Room();
        room.setRoomId(UUID.randomUUID());
        room.setHostPlayerId(request.hostPlayerId());
        room.setJoinCode(room.getRoomId().toString().substring(0,6).toUpperCase(Locale.ROOT));
        room.setCreatedAt(Instant.now());
        return toResponse(rooms.save(room));
    }

    public List<RoomResponse> listRooms() {
        var result=new ArrayList<RoomResponse>();
        rooms.findAll().forEach(room -> result.add(toResponse(room)));
        result.sort(Comparator.comparing(RoomResponse::createdAt).reversed());
        return result;
    }

    public RoomResponse getRoom(UUID roomId) {
        return toResponse(find(roomId));
    }

    public RoomResponse joinRoom(UUID roomId, JoinRoomRequest request) {
        var room=find(roomId);
        requirePlayer(request.playerId());
        if (request.playerId().equals(room.getHostPlayerId())) throw ApiException.conflict(HOST_CANNOT_JOIN);
        if (room.getGuestPlayerId()!=null && !room.getGuestPlayerId().equals(request.playerId())) throw ApiException.conflict(ROOM_FULL);
        room.setGuestPlayerId(request.playerId()); return toResponse(rooms.save(room));
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

    private RoomResponse toResponse(Room room) {
        return roomMapper.toResponse(room);
    }

    private void requirePlayer(UUID id) {
        if (!players.existsById(id)) throw ApiException.notFound(PLAYER_NOT_FOUND,id);
    }
}
