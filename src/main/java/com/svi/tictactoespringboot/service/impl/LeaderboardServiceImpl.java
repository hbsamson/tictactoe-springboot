package com.svi.tictactoespringboot.service.impl;

import com.svi.tictactoespringboot.dto.response.LeaderboardResponse;
import com.svi.tictactoespringboot.entity.Leaderboard;
import com.svi.tictactoespringboot.repository.LeaderboardRepository;
import com.svi.tictactoespringboot.service.LeaderboardService;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {
     private final LeaderboardRepository repository;
     public LeaderboardServiceImpl(LeaderboardRepository repository) {
          this.repository=repository;
     }

     public LeaderboardResponse get() {
           var rows = new ArrayList<Leaderboard>();
           repository.findAll().forEach(rows::add);
           rows.sort(Comparator.comparingInt(
                   Leaderboard::getPoints).reversed().thenComparing(Comparator.comparingInt(Leaderboard::getWins).reversed()).thenComparingInt(Leaderboard::getLosses).thenComparing(Leaderboard::getPlayerName, String.CASE_INSENSITIVE_ORDER));

           var entries = new ArrayList<LeaderboardResponse.Entry>();
           for(int i = 0; i < rows.size(); i++) {
               var x = rows.get(i);
               entries.add(
                       new LeaderboardResponse.Entry(
                               i+1,
                               x.getPlayerId(),
                               x.getPlayerName(),
                               x.getWins(),
                               x.getDraws(),
                               x.getLosses(),
                               x.getPoints()
                       )
               );
           }
           return new LeaderboardResponse(entries);
     }
}
