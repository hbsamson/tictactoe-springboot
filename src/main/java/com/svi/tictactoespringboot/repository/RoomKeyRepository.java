package com.svi.tictactoespringboot.repository;

import com.svi.tictactoespringboot.entity.RoomKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomKeyRepository extends CassandraRepository<RoomKey, String> {}
