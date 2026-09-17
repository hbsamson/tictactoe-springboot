package com.svi.tictactoespringboot.mapper;

import com.svi.tictactoespringboot.dto.response.PlayerResponse;
import com.svi.tictactoespringboot.entity.Player;
import org.springframework.stereotype.Component;

@Component 
public class PlayerMapper { 
    public PlayerResponse toResponse(Player player) { 
        return new PlayerResponse(
                player.getPlayerId(),
                player.getName(), 
                player.getAvatarUrl(),
                player.getCreatedAt()); 
    } 
}
