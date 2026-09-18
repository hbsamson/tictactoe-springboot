package com.svi.tictactoespringboot.mapper;

import com.svi.tictactoespringboot.dto.response.RoomResponse;
import com.svi.tictactoespringboot.entity.Room;
import org.springframework.stereotype.Component;

@Component 
public class RoomMapper { 
    public RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getRoomId(),
                room.getRoomKey(),
                room.getHostPlayerId(),
                room.getGuestPlayerId(),
                room.getCreatedAt(),
                room.getExpiresAt(),
                room.getStatus());
    } 
}
