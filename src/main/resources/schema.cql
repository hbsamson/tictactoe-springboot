CREATE KEYSPACE IF NOT EXISTS tictactoe_springboot WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

USE tictactoe_springboot;

CREATE TABLE IF NOT EXISTS players (
    player_id           uuid        PRIMARY KEY,
    name                text,
    created_at          timestamp
);

CREATE TABLE IF NOT EXISTS rooms (
    room_id             uuid        PRIMARY KEY,
    room_key            text,
    host_player_id      uuid,
    guest_player_id     uuid,
    created_at          timestamp
);

CREATE TABLE IF NOT EXISTS games (
    game_id             uuid       PRIMARY KEY,
    room_id             uuid,
    player_x_id         uuid,
    player_o_id         uuid,
    current_player_id   uuid,
    current_symbol      text,
    status              text,
    winner_id           uuid,
    board               list<text>,
    move_count          int,
    created_at          timestamp,
    updated_at          timestamp,
    completed_at        timestamp,
    version             bigint
);

CREATE TABLE IF NOT EXISTS moves (
    game_id             uuid,
    move_number         int,
    player_id           uuid,
    symbol              text,
    x                   int,
    y                   int,
    played_at           timestamp,
    PRIMARY KEY (game_id, move_number)
) WITH CLUSTERING ORDER BY (move_number ASC);

CREATE TABLE IF NOT EXISTS games_by_player (
    owner_id            uuid,
    created_at          timestamp,
    game_id             uuid,
    PRIMARY KEY (owner_id, created_at, game_id)
) WITH CLUSTERING ORDER BY (created_at DESC);

CREATE TABLE IF NOT EXISTS games_by_room (
    owner_id            uuid,
    created_at          timestamp,
    game_id             uuid,
    PRIMARY KEY (owner_id, created_at, game_id)
) WITH CLUSTERING ORDER BY (created_at DESC);

CREATE TABLE IF NOT EXISTS leaderboard (
    player_id           uuid        PRIMARY KEY,
    player_name         text,
    wins                int,
    draws               int,
    losses              int
);
