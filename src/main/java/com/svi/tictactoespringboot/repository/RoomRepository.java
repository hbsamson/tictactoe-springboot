package com.svi.tictactoespringboot.repository;

import com.svi.tictactoespringboot.entity.Room;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface RoomRepository extends CassandraRepository<Room, UUID> {}
