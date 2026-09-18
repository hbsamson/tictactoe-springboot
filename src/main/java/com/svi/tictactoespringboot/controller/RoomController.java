package com.svi.tictactoespringboot.controller;

import com.svi.tictactoespringboot.dto.request.CreateRoomRequest;
import com.svi.tictactoespringboot.dto.request.JoinRoomRequest;
import com.svi.tictactoespringboot.dto.response.GameResponse;
import com.svi.tictactoespringboot.dto.response.RoomResponse;
import com.svi.tictactoespringboot.service.RoomService;
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

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.create(request));
    }

    @GetMapping
    public List<RoomResponse> listRooms() {
        return roomService.list();
    }

    @GetMapping("/{roomId}")
    public RoomResponse getRoom(@PathVariable UUID roomId) {
        return roomService.get(roomId);
    }

    @PostMapping("/{roomId}/join")
    public RoomResponse joinRoom(@PathVariable UUID roomId, @Valid @RequestBody JoinRoomRequest request) {
        return roomService.join(roomId, request);
    }

    @GetMapping("/{roomId}/games")
    public List<GameResponse> getRoomGames(@PathVariable UUID roomId) {
        return roomService.games(roomId);
    }
}
