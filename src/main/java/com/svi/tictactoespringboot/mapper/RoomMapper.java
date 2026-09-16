package com.svi.tictactoespringboot.mapper;
import com.svi.tictactoespringboot.dto.response.RoomResponse;
import com.svi.tictactoespringboot.entity.Room;
import org.springframework.stereotype.Component;
@Component public class RoomMapper { public RoomResponse toResponse(Room r) { return new RoomResponse(r.getRoomId(), r.getJoinCode(), r.getHostPlayerId(), r.getGuestPlayerId(), r.getGameCount(), r.getCreatedAt()); } }
