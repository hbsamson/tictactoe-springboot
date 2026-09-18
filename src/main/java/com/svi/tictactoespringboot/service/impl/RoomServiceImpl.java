package com.svi.tictactoespringboot.service.impl;
import com.svi.tictactoespringboot.dto.request.*;
import com.svi.tictactoespringboot.dto.response.*;
import com.svi.tictactoespringboot.entity.Room;
import com.svi.tictactoespringboot.exception.ApiException;
import com.svi.tictactoespringboot.mapper.*;
import com.svi.tictactoespringboot.repository.*;
import com.svi.tictactoespringboot.service.RoomService;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository rooms;
    private final PlayerRepository players;
    private final RoomGameRepository histories;
    private final GameRepository games;
    private final RoomMapper roomMapper;
    private final GameMapper gameMapper;

    public RoomServiceImpl(RoomRepository r, PlayerRepository p, RoomGameRepository h, GameRepository g, RoomMapper rm, GameMapper gm) {
        rooms=r;
        players=p;
        histories=h;
        games=g;
        roomMapper=rm;
        gameMapper=gm;
    }
    public RoomResponse create(CreateRoomRequest r) {
        requirePlayer(r.hostPlayerId());
        var room = new Room();
        room.setRoomId(UUID.randomUUID());
        room.setHostPlayerId(r.hostPlayerId());
        room.setJoinCode(room.getRoomId().toString().substring(0,6).toUpperCase(Locale.ROOT));
        room.setCreatedAt(Instant.now());
        return toResponse(rooms.save(room));
    }

    public List<RoomResponse> list() {
        var result=new ArrayList<RoomResponse>();
        rooms.findAll().forEach(x->result.add(toResponse(x)));
        result.sort(Comparator.comparing(RoomResponse::createdAt).reversed());
        return result;
    }

    public RoomResponse get(UUID id) {
        return toResponse(find(id));
    }

    public RoomResponse join(UUID id, JoinRoomRequest r) {
        var room=find(id);
        requirePlayer(r.playerId());
        if (r.playerId().equals(room.getHostPlayerId())) throw ApiException.conflict("HOST_CANNOT_JOIN","Host is already in the room");
        if (room.getGuestPlayerId()!=null && !room.getGuestPlayerId().equals(r.playerId())) throw ApiException.conflict("ROOM_FULL","Room already has two players");
        room.setGuestPlayerId(r.playerId()); return toResponse(rooms.save(room));
    }

    public List<GameResponse> games(UUID id) {
        find(id);
        return histories.findByKeyOwnerId(id).stream().map(x->games.findById(x.getKey().getGameId()).orElse(null)).filter(Objects::nonNull).map(gameMapper::toResponse).toList();
    }

    private Room find(UUID id) {
        return rooms.findById(id).orElseThrow(()->ApiException.notFound("room",id));
    }

    private RoomResponse toResponse(Room room) {
        return roomMapper.toResponse(room);
    }

    private void requirePlayer(UUID id) {
        if (!players.existsById(id)) throw ApiException.notFound("player",id);
    }
}
