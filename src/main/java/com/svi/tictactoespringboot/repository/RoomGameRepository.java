package com.svi.tictactoespringboot.repository;
import com.svi.tictactoespringboot.entity.GameReferenceKey;
import com.svi.tictactoespringboot.entity.RoomGame;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
@Repository public interface RoomGameRepository extends CassandraRepository<RoomGame, GameReferenceKey> { List<RoomGame> findByKeyOwnerId(UUID ownerId); }
