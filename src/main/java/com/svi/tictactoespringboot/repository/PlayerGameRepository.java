package com.svi.tictactoespringboot.repository;
import com.svi.tictactoespringboot.entity.GameReferenceKey;
import com.svi.tictactoespringboot.entity.PlayerGame;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository 
public interface PlayerGameRepository extends CassandraRepository<PlayerGame, GameReferenceKey> { 
    List<PlayerGame> findByKeyOwnerId(UUID ownerId);
}
