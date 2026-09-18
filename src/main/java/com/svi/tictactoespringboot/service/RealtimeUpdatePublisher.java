package com.svi.tictactoespringboot.service;

import com.svi.tictactoespringboot.dto.response.GameResponse;
import com.svi.tictactoespringboot.dto.response.LeaderboardResponse;
import com.svi.tictactoespringboot.dto.response.RoomResponse;
import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RealtimeUpdatePublisher {
    public static final String LOBBY_TOPIC = "/topic/lobby";
    public static final String LEADERBOARD_TOPIC = "/topic/leaderboard";

    private final SimpMessagingTemplate messagingTemplate;

    public RealtimeUpdatePublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishBoard(GameResponse game) {
        messagingTemplate.convertAndSend(
                "/topic/games/" + game.gameId() + "/board", game);
    }

    public void publishLobby(List<RoomResponse> rooms) {
        messagingTemplate.convertAndSend(LOBBY_TOPIC, rooms);
    }

    public void publishLeaderboard(LeaderboardResponse leaderboard) {
        messagingTemplate.convertAndSend(LEADERBOARD_TOPIC, leaderboard);
    }
}
