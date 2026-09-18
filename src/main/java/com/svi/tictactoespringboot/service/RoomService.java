package com.svi.tictactoespringboot.service;

import com.svi.tictactoespringboot.dto.request.CreateRoomRequest;
import com.svi.tictactoespringboot.dto.request.JoinRoomRequest;
import com.svi.tictactoespringboot.dto.response.GameResponse;
import com.svi.tictactoespringboot.dto.response.RoomResponse;
import java.util.List;
import java.util.UUID;

public interface RoomService {
    RoomResponse createRoom(CreateRoomRequest request);
    List<RoomResponse> listRooms();
    RoomResponse getRoom(UUID roomId);
    RoomResponse joinRoom(UUID roomId, JoinRoomRequest request);
    List<GameResponse> getRoomGames(UUID roomId);
}
