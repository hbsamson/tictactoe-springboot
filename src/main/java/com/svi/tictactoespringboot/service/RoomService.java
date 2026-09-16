package com.svi.tictactoespringboot.service;
import com.svi.tictactoespringboot.dto.request.CreateRoomRequest;
import com.svi.tictactoespringboot.dto.request.JoinRoomRequest;
import com.svi.tictactoespringboot.dto.response.GameResponse;
import com.svi.tictactoespringboot.dto.response.RoomResponse;
import java.util.List;
import java.util.UUID;
public interface RoomService { RoomResponse create(CreateRoomRequest request); List<RoomResponse> list(); RoomResponse get(UUID id); RoomResponse join(UUID id, JoinRoomRequest request); List<GameResponse> games(UUID id); }
