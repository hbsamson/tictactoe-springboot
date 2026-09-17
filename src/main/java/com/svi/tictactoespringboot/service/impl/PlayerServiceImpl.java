package com.svi.tictactoespringboot.service.impl;
import com.svi.tictactoespringboot.dto.request.CreatePlayerRequest;
import com.svi.tictactoespringboot.dto.response.*;
import com.svi.tictactoespringboot.entity.Player;
import com.svi.tictactoespringboot.exception.ApiException;
import com.svi.tictactoespringboot.mapper.*;
import com.svi.tictactoespringboot.repository.*;
import com.svi.tictactoespringboot.service.PlayerService;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
public class PlayerServiceImpl implements PlayerService {
 private final PlayerRepository players; private final PlayerGameRepository histories; private final GameRepository games; private final PlayerMapper playerMapper; private final GameMapper gameMapper;
 public PlayerServiceImpl(PlayerRepository p, PlayerGameRepository h, GameRepository g, PlayerMapper pm, GameMapper gm) { players=p; histories=h; games=g; playerMapper=pm; gameMapper=gm; }
 public PlayerResponse create(CreatePlayerRequest r) { return playerMapper.toResponse(players.save(new Player(UUID.randomUUID(), r.name().trim(), Instant.now()))); }
 public List<PlayerResponse> list() { var result=new ArrayList<PlayerResponse>(); players.findAll().forEach(p->result.add(playerMapper.toResponse(p))); result.sort(Comparator.comparing(PlayerResponse::name,String.CASE_INSENSITIVE_ORDER)); return result; }
 public PlayerResponse get(UUID id) { return playerMapper.toResponse(find(id)); }
 public List<GameResponse> games(UUID id) { find(id); return histories.findByKeyOwnerId(id).stream().map(x->games.findById(x.getKey().getGameId()).orElse(null)).filter(Objects::nonNull).map(gameMapper::toResponse).toList(); }
 private Player find(UUID id) { return players.findById(id).orElseThrow(()->ApiException.notFound("player",id)); }
 private String normalize(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
