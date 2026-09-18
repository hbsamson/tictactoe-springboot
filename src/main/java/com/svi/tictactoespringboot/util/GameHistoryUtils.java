package com.svi.tictactoespringboot.util;

import com.svi.tictactoespringboot.dto.response.GameResponse;
import com.svi.tictactoespringboot.entity.GameReferenceKey;
import com.svi.tictactoespringboot.mapper.GameMapper;
import com.svi.tictactoespringboot.repository.GameRepository;
import java.util.List;
import java.util.stream.Stream;

public final class GameHistoryUtils {
    private GameHistoryUtils() {}

    public static List<GameResponse> toResponses(
            Stream<GameReferenceKey> references,
            GameRepository games,
            GameMapper gameMapper) {
        return references
                .map(GameReferenceKey::getGameId)
                .map(games::findById)
                .flatMap(java.util.Optional::stream)
                .map(gameMapper::toResponse)
                .toList();
    }
}
