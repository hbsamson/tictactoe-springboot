package com.svi.tictactoespringboot.controller;

import com.svi.tictactoespringboot.dto.response.LeaderboardResponse;
import com.svi.tictactoespringboot.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/leaderboard")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public LeaderboardResponse getLeaderboard() {
        return leaderboardService.getLeaderboard();
    }
}
