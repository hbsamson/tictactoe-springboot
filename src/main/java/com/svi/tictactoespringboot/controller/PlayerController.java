package com.svi.tictactoespringboot.controller;

import com.svi.tictactoespringboot.dto.request.CreatePlayerRequest;
import com.svi.tictactoespringboot.dto.response.GameResponse;
import com.svi.tictactoespringboot.dto.response.PlayerResponse;
import com.svi.tictactoespringboot.service.PlayerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> create(@Valid @RequestBody CreatePlayerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.create(request));
    }

    @GetMapping
    public List<PlayerResponse> list() {
        return playerService.list();
    }

    @GetMapping("/{playerId}")
    public PlayerResponse get(@PathVariable UUID playerId) {
        return playerService.get(playerId);
    }

    @GetMapping("/{playerId}/games")
    public List<GameResponse> games(@PathVariable UUID playerId) {
        return playerService.games(playerId);
    }
}
