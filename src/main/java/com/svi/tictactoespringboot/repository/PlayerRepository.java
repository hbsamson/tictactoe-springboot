package com.svi.tictactoespringboot.repository;
import com.svi.tictactoespringboot.entity.Player;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
@Repository public interface PlayerRepository extends CassandraRepository<Player, UUID> {}
