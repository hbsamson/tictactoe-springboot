package com.svi.tictactoespringboot.controller;

import com.svi.tictactoespringboot.dto.request.CreateRoomRequest;
import com.svi.tictactoespringboot.dto.request.JoinRoomRequest;
import com.svi.tictactoespringboot.dto.response.GameResponse;
import com.svi.tictactoespringboot.dto.response.RoomResponse;
import com.svi.tictactoespringboot.service.RoomService;
import com.svi.tictactoespringboot.service.RealtimeUpdatePublisher;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    private final RoomService roomService;
    private final RealtimeUpdatePublisher realtimeUpdates;

    public RoomController(RoomService roomService, RealtimeUpdatePublisher realtimeUpdates) {
        this.roomService = roomService;
        this.realtimeUpdates = realtimeUpdates;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        RoomResponse room = roomService.createRoom(request);
        realtimeUpdates.publishLobby(roomService.listRooms());
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    @GetMapping
    public List<RoomResponse> listRooms() {
        return roomService.listRooms();
    }

    @GetMapping("/{roomId}")
    public RoomResponse getRoom(@PathVariable UUID roomId) {
        return roomService.getRoom(roomId);
    }

    @PostMapping("/{roomKey}/join")
    public RoomResponse joinRoom(@PathVariable String roomKey, @Valid @RequestBody JoinRoomRequest request) {
        RoomResponse room = roomService.joinRoom(roomKey, request);
        realtimeUpdates.publishLobby(roomService.listRooms());
        return room;
    }

    @GetMapping("/{roomId}/games")
    public List<GameResponse> getRoomGames(@PathVariable UUID roomId) {
        return roomService.getRoomGames(roomId);
    }
}
