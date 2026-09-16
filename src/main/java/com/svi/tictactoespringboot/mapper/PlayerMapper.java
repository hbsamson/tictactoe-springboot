package com.svi.tictactoespringboot.mapper;
import com.svi.tictactoespringboot.dto.response.PlayerResponse;
import com.svi.tictactoespringboot.entity.Player;
import org.springframework.stereotype.Component;

@Component 
public class PlayerMapper { 
    public PlayerResponse toResponse(Player p) { 
        return new PlayerResponse(p.getPlayerId(), p.getName(), p.getAvatarUrl(), p.getCreatedAt()); 
    } 
}
