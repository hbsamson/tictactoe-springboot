package com.svi.tictactoespringboot.controller;

import com.svi.tictactoespringboot.dto.request.MakeMoveRequest;
import com.svi.tictactoespringboot.dto.request.StartGameRequest;
import com.svi.tictactoespringboot.dto.response.BoardResponse;
import com.svi.tictactoespringboot.dto.response.GameResponse;
import com.svi.tictactoespringboot.dto.response.GameStatusResponse;
import com.svi.tictactoespringboot.dto.response.MoveResponse;
import com.svi.tictactoespringboot.service.GameService;
import com.svi.tictactoespringboot.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class GameController {
    private final GameService gameService;
    private final RoomService roomService;

    public GameController(GameService gameService, RoomService roomService) {
        this.gameService = gameService;
        this.roomService = roomService;
    }

    @PostMapping("/rooms/{roomKey}/games")
    public ResponseEntity<GameResponse> createGame(
            @PathVariable String roomKey, @Valid @RequestBody StartGameRequest request) {
        UUID roomId = roomService.requireActiveRoom(roomKey, request.playerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.createGame(roomId));
    }

    @GetMapping("/games/{gameId}")
    public GameResponse getGame(@PathVariable UUID gameId) {
        return gameService.getGame(gameId);
    }

    @GetMapping("/games/{gameId}/board")
    public BoardResponse getBoard(@PathVariable UUID gameId) {
        return gameService.getBoard(gameId);
    }

    @GetMapping("/games/{gameId}/status")
    public GameStatusResponse getGameStatus(@PathVariable UUID gameId) {
        return gameService.getGameStatus(gameId);
    }

    @GetMapping("/games/{gameId}/moves")
    public List<MoveResponse> getGameMoves(@PathVariable UUID gameId) {
        return gameService.getGameMoves(gameId);
    }

    @PostMapping("/games/{gameId}/moves")
    public GameResponse makeMove(@PathVariable UUID gameId, @Valid @RequestBody MakeMoveRequest request) {
        return gameService.makeMove(gameId, request);
    }

    @PostMapping("/games/{gameId}/rematches")
    public ResponseEntity<GameResponse> createRematch(@PathVariable UUID gameId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.createRematch(gameId));
    }
}
