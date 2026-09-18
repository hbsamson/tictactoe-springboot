package com.svi.tictactoespringboot.service.impl;

import com.svi.tictactoespringboot.dto.response.LeaderboardResponse;
import com.svi.tictactoespringboot.entity.Leaderboard;
import com.svi.tictactoespringboot.repository.LeaderboardRepository;
import com.svi.tictactoespringboot.service.LeaderboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {
     private static final Logger log = LoggerFactory.getLogger(LeaderboardServiceImpl.class);

     private final LeaderboardRepository repository;
     public LeaderboardServiceImpl(LeaderboardRepository repository) {
          this.repository=repository;
     }

     public LeaderboardResponse getLeaderboard() {
           var rows = new ArrayList<Leaderboard>();
           repository.findAll().forEach(rows::add);
           log.debug("Loaded leaderboard: entryCount={}", rows.size());
           rows.sort(Comparator.comparingInt(Leaderboard::getWins).reversed()
                   .thenComparing(Comparator.comparingInt(Leaderboard::getDraws).reversed())
                   .thenComparingInt(Leaderboard::getLosses)
                   .thenComparing(Leaderboard::getPlayerName, String.CASE_INSENSITIVE_ORDER));

           var entries = new ArrayList<LeaderboardResponse.Entry>();
           for(int index = 0; index < rows.size(); index++) {
               var row = rows.get(index);
               entries.add(
                       new LeaderboardResponse.Entry(
                               index + 1,
                               row.getPlayerId(),
                               row.getPlayerName(),
                               row.getWins(),
                               row.getDraws(),
                               row.getLosses()
                       )
               );
           }
           return new LeaderboardResponse(entries);
     }

}
