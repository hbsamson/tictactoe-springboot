# Tic-Tac-Toe Spring Boot API

A REST API for a two-player Tic-Tac-Toe game. It manages players, reusable rooms, durable games and moves, rematches, and a leaderboard. The project demonstrates layered Spring architecture and query-driven Cassandra modeling.

## Contents

- [Technology and dependencies](#technology-and-dependencies)
- [Architecture](#architecture)
- [Game lifecycle](#game-lifecycle)
- [Local setup and running](#local-setup-and-running)
- [Configuration](#configuration)
- [Postman setup](#postman-setup)
- [API reference](#api-reference)
- [Cassandra schema](#cassandra-schema)
- [Annotations](#annotations)
- [Key design decisions](#key-design-decisions)
- [Build and troubleshooting](#build-and-troubleshooting)

## Technology and dependencies

| Technology | Version | Purpose |
|---|---:|---|
| Java | 25 | Language and application runtime |
| Spring Boot | 4.1.1 | Bootstrap and dependency management |
| Maven | Wrapper included | Build, run, and package |
| Cassandra | Local setup uses 3.11.14 | Durable query-oriented storage |
| Postman | Collection schema 2.1 | End-to-end API tests |

The Maven dependencies are intentionally small:

| Dependency | Responsibility |
|---|---|
| `spring-boot-starter-webmvc` | REST routing, JSON serialization, and HTTP responses |
| `spring-boot-starter-data-cassandra` | Cassandra mapping and generated repository implementations |
| `spring-boot-starter-validation` | Jakarta request validation |
| `spring-boot-starter-actuator` | Operational and health infrastructure |
| `spring-boot-maven-plugin` | Runs and packages the application |

Constructor injection is used throughout, making dependencies explicit and components easier to maintain.

## Architecture

### Request flow

```text
HTTP client / Postman
        |
        v
Controllers  -- routing, deserialization, validation, HTTP status
        |
        v
Services     -- room/game rules and use-case coordination
        |
        +--> Mappers --> response DTOs
        |
        v
Repositories -- Spring Data Cassandra operations
        |
        v
Cassandra    -- source tables, history indexes, leaderboard read model

Exceptions --> GlobalExceptionHandler --> consistent JSON error
```

### Package responsibilities

| Package | Responsibility |
|---|---|
| `controller` | Defines `/api/v1` routes; contains no game logic |
| `service` / `service.impl` | Enforces rules, advances turns, detects results, and coordinates writes |
| `repository` | Declares Cassandra access through `CassandraRepository` |
| `entity` | Maps Java objects and compound keys to tables |
| `dto.request` | Defines accepted JSON and field constraints |
| `dto.response` | Defines the public API without exposing persistence entities |
| `mapper` | Converts entities into immutable response records |
| `exception` | Represents expected failures and centralizes error responses |
| `config` | Configures API CORS behavior |
| `util` | Resolves history reference rows to games |

### Domain/data relationship

```text
Player X ----\
              >---- Room ---- Game ---- ordered Moves
Player O ----/          |        |
                        |        +---- games_by_player
                        +------------- games_by_room

Completed Game ---------------------> Leaderboard entries
```

A room is a reusable pairing. Its host is X and starts; its guest is O. A rematch uses the same two players but creates a new game with an empty board and no moves. The previous game's final board and move history remain stored under the previous game ID.

## Game lifecycle

1. Create two players.
2. One player creates a room and becomes its host.
3. The server returns a six-character room key. The room is `WAITING` for the configured period (10 minutes by default).
4. A different player joins with that key before it expires. The room becomes `ACTIVE`, meaning both player slots are assigned and it is ready to start.
5. The host starts the game with the room key and their player ID. The room becomes `IN_GAME`; the host is `X`, the guest `O`, and X moves first.
6. Submit moves using the acting player's UUID and zero-based `x`/`y` coordinates.
7. The service rejects non-members, out-of-turn moves, occupied cells, invalid coordinates, and moves after completion.
8. After each move it checks eight winning lines, declares a draw after nine moves, or changes the turn.
9. A win or draw updates both leaderboard entries. A rematch creates a fresh game; if the previous game is active, it is marked `ABANDONED`.

`ACTIVE` records lobby membership; it does not claim that a persistent browser or network connection is open. Because Postman has no disconnect event, an unfilled `WAITING` room becomes `EXPIRED` based on server time. Expiration is checked whenever the room is read, joined, or used to start a game. Starting twice through the room-key endpoint is rejected; later matches use the rematch endpoint.

Coordinates map to the flat board with `index = y * 3 + x`:

```text
             x=0  x=1  x=2
y=0           0    1    2
y=1           3    4    5
y=2           6    7    8
```

Clients never submit a symbol. The server derives `X` or `O` from membership and turn state, preventing clients from forging marks.

## Local setup and running

### Prerequisites

- JDK 25 for Spring Boot.
- Cassandra reachable on port `9042` (the documented installation is Cassandra 3.11.14).
- For this legacy Windows Cassandra distribution, JDK 8 to launch Cassandra and Python 2.7 for bundled `cqlsh`.
- Postman Desktop or CLI to run the collection.

The paths below are examples; substitute your installation paths. Keep Cassandra's Java 8 and the application's Java 25 in separate terminals.

### 1. Clone the project

```powershell
git clone <repository-url>
cd tictactoe-springboot
```

### 2. Start Cassandra

Open Command Prompt and leave it running:

```bat
set "JAVA_HOME=C:\Users\user\Documents\jdk8u502-b07"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd C:\Users\user\Documents\apache-cassandra-3.11.14\bin
cassandra -R
```

Wait until Cassandra is listening for CQL clients. Test readiness with `cqlsh localhost`, then enter `exit`.

### 3. Initialize the schema

From the repository root in a second Command Prompt:

```bat
set "PATH=C:\Python27;%PATH%"
python --version
C:\Users\user\Documents\apache-cassandra-3.11.14\bin\cqlsh localhost -f src\main\resources\schema.cql
```

Optional verification:

```cql
DESCRIBE KEYSPACES;
USE tictactoe_springboot;
DESCRIBE TABLES;
DESCRIBE TABLE games;
```

### 4. Start the API

Open a third terminal at the repository root:

```bat
set "JAVA_HOME=C:\Program Files\Java\jdk-25.0.2"
set "PATH=%JAVA_HOME%\bin;%PATH%"
java -version
mvnw.cmd spring-boot:run
```

An installed Maven can use `mvn spring-boot:run`. On macOS/Linux, use `./mvnw spring-boot:run` with a compatible Cassandra installation.

The API is available at `http://localhost:8080/api/v1`. Stop Spring with `Ctrl+C`; Cassandra must remain running while the API is used.

## Configuration

Defaults are in `src/main/resources/application.properties`:

| Environment variable | Default | Meaning |
|---|---|---|
| `CASSANDRA_CONTACT_POINTS` | `localhost:9042` | Cassandra host(s) and port |
| `CASSANDRA_LOCAL_DATACENTER` | `datacenter1` | Driver local datacenter |
| `CASSANDRA_KEYSPACE` | `tictactoe_springboot` | Existing keyspace |
| `CASSANDRA_SCHEMA_ACTION` | `CREATE_IF_NOT_EXISTS` | Spring Data table schema action |
| `CASSANDRA_REQUEST_TIMEOUT` | `10s` | Cassandra request timeout |
| `FRONTEND_URLS` | `http://localhost:5500,http://127.0.0.1:5500` | Comma-separated allowed CORS origins |
| `ROOM_WAITING_TIMEOUT` | `10m` | How long a room accepts its second player |

PowerShell example:

```powershell
$env:CASSANDRA_CONTACT_POINTS = "localhost:9042"
$env:FRONTEND_URLS = "http://localhost:3000,http://localhost:5173"
$env:ROOM_WAITING_TIMEOUT = "10m"
.\mvnw.cmd spring-boot:run
```

`CREATE_IF_NOT_EXISTS` does not run `schema.cql` and cannot create the keyspace Spring must connect to. Run the script first. It also does not add columns to existing tables; schema changes require an explicit migration or a deliberate local reset.

If the keyspace was created before room-key joining and expiration were added, run this one-time migration:

```bat
C:\Users\user\Documents\apache-cassandra-3.11.14\bin\cqlsh localhost -f src\main\resources\migrate-room-lobby.cql
```

Do not run that migration twice; Cassandra 3.11 will reject columns or tables that already exist. Fresh installations need only `schema.cql`.

## Postman setup

Import [`postman/tictactoe-springboot.postman_collection.json`](postman/tictactoe-springboot.postman_collection.json). A separate Postman environment is unnecessary because the collection owns its variables and test scripts.

### Import and configure

1. Start Cassandra and Spring Boot.
2. In Postman choose **Import**, then select the JSON file in `postman/`.
3. Open the imported collection and select **Variables**.
4. Confirm `baseUrl` is `http://localhost:8080/api/v1`; change it if your server differs.
5. Leave generated ID values empty. Test scripts populate them from responses.

| Variable | Usage |
|---|---|
| `baseUrl` | Prefix for all endpoints |
| `playerXId`, `playerOId` | Captured when the two players are created |
| `roomId` | Captured for internal room reads and history |
| `roomKey` | Captured for guest join and initial game start |
| `gameId` | Captured when the first game is created |
| `rematchGameId` | Captured when the rematch is created |
| `missingId` | Stable UUID for `404` tests |
| `outsiderId` | Stable UUID for membership rejection |

### Run the scenario

Use the collection runner and preserve request order because later folders consume earlier IDs:

1. **Create two players** — creates X/O and verifies validation, malformed JSON, missing player, and invalid UUID errors.
2. **Room Setup** — tests invalid setup, creates a room, verifies it is not ready, and joins O.
3. **Play game - X wins** — tests illegal moves and plays five legal moves ending in an X win.
4. **Verify all read paths** — verifies game, board, status, moves, histories, leaderboard, and a missing game.
5. **Rematch** — creates a fresh game and verifies its empty board.

Requests include assertions for status and important fields. Scripts use `pm.collectionVariables.set(...)`; running a later request alone before setup leaves its placeholders empty.

Cassandra persists between runs. The collection's newly generated resources remain distinct, but its leaderboard assertions assume those new players have no prior score row. For a strictly repeatable run, use a clean development keyspace. Never reset a keyspace containing needed data.

## API reference

All request/response bodies are JSON and all routes use `/api/v1`.

| Method | Endpoint | Purpose | Success |
|---|---|---|---:|
| `POST` | `/players` | Create a player | `201` |
| `GET` | `/players` | List players alphabetically | `200` |
| `GET` | `/players/{playerId}` | Get a player | `200` |
| `GET` | `/players/{playerId}/games` | Player history, newest first | `200` |
| `POST` | `/rooms` | Create a hosted room | `201` |
| `GET` | `/rooms` | List rooms, newest first | `200` |
| `GET` | `/rooms/{roomId}` | Get a room | `200` |
| `POST` | `/rooms/{roomKey}/join` | Join/re-submit the guest before expiration | `200` |
| `POST` | `/rooms/{roomKey}/games` | Host starts an active room's game | `201` |
| `GET` | `/rooms/{roomId}/games` | Room history, newest first | `200` |
| `GET` | `/games/{gameId}` | Complete game snapshot | `200` |
| `GET` | `/games/{gameId}/board` | Nine board cells | `200` |
| `GET` | `/games/{gameId}/status` | Status/current player/winner | `200` |
| `GET` | `/games/{gameId}/moves` | Ordered move history | `200` |
| `POST` | `/games/{gameId}/moves` | Make a move | `200` |
| `POST` | `/games/{gameId}/rematches` | Create a new game in the room | `201` |
| `GET` | `/leaderboard` | Ranked score entries | `200` |

### Real-time STOMP updates

Connect a STOMP client to the native WebSocket endpoint `ws://localhost:8080/ws`, then
subscribe to the destinations needed by the current screen:

| Destination | Payload | Published when |
|---|---|---|
| `/topic/lobby` | JSON array of `RoomResponse` objects | A room is created, joined, or its first game starts |
| `/topic/games/{gameId}/board` | Complete `GameResponse` snapshot | A game starts, a move is accepted, or a rematch changes the old/new game |
| `/topic/leaderboard` | `LeaderboardResponse` | A game ends in a win or draw |

The REST endpoints remain the source for commands and initial state. Subscribe first,
then fetch the corresponding REST resource so an update cannot be missed while the
screen is loading. Browser origins for both REST and WebSocket handshakes use the
`FRONTEND_URLS` setting.

Example request bodies:

```json
{ "name": "Hannah" }
```

```json
{ "hostPlayerId": "<player-uuid>" }
```

```json
{ "playerId": "<guest-player-uuid>" }
```

```json
{ "playerId": "<host-player-uuid>" }
```

The preceding bodies are used for `POST /rooms/{roomKey}/join` and `POST /rooms/{roomKey}/games`, respectively.

```json
{ "playerId": "<current-player-uuid>", "x": 0, "y": 0 }
```

Game statuses are `IN_PROGRESS`, `WON`, `DRAW`, and `ABANDONED`; symbols are `X` and `O`. At completion, `currentPlayerId` and `currentSymbol` are `null`. `winnerId` is populated only for `WON`.

### Error contract

Expected failures share one response shape:

```json
{
  "timestamp": "2026-09-18T10:00:00Z",
  "status": 409,
  "code": "OUT_OF_TURN",
  "message": "It is not this player's turn.",
  "path": "/api/v1/games/<game-id>/moves",
  "fieldErrors": []
}
```

- `400`: invalid fields, malformed JSON, or invalid path UUID.
- `404`: player, room, or game not found.
- `409`: room/game rule conflict or optimistic-lock conflict.
- `500`: unexpected server failure.

## Cassandra schema

`src/main/resources/schema.cql` creates eight tables:

| Table | Primary key | Purpose/access pattern |
|---|---|---|
| `players` | `player_id` | Player record fetched by UUID |
| `rooms` | `room_id` | Authoritative membership, status, key, and waiting expiration |
| `rooms_by_key` | `room_key` | Resolves a human-friendly key to the internal room UUID |
| `games` | `game_id` | Authoritative current snapshot |
| `moves` | `(game_id, move_number)` | One game partition, oldest move first |
| `games_by_player` | `(owner_id, created_at, game_id)` | Player history, newest first |
| `games_by_room` | `(owner_id, created_at, game_id)` | Room history, newest first |
| `leaderboard` | `player_id` | Precomputed wins/draws/losses |

Cassandra is modeled from queries, not relational normalization. `rooms_by_key` exists because `room_key` is not the partition key of `rooms`. Room creation reserves its key with Cassandra `IF NOT EXISTS`; a collision generates another candidate, so a key always resolves to one room. `createdAt` and `expiresAt` determine room age and validity, but cannot disambiguate duplicate keys because a joining player supplies only the key.

Querying `games` by non-key player or room columns would likewise require a scan or `ALLOW FILTERING`, so game creation writes lightweight references to `games_by_player` for both participants and to `games_by_room`. `owner_id` selects one partition; `created_at DESC` supplies history order; `game_id` prevents timestamp collisions.

Likewise, `moves` partitions by game and clusters by ascending move number. The `games` row separately stores board, status, current player, winner, and count, so a normal read need not replay every move.

The leaderboard is a materialized read model. It currently loads rows and sorts by wins descending, draws descending, losses ascending, then case-insensitive name. This suits demonstration-scale data; a large deployment would need bucketed ranking tables or a dedicated analytics read service.

## Annotations

### Web, validation, and components

| Annotation/interface | Effect |
|---|---|
| `@SpringBootApplication` | Enables auto-configuration, component scanning, and startup |
| `@RestController` | Registers handlers and serializes returns as JSON |
| `@RequestMapping`, `@GetMapping`, `@PostMapping` | Define route prefixes and operations |
| `@RequestBody` | Deserializes JSON to a request record |
| `@PathVariable` | Converts route values; invalid UUIDs become structured `400`s |
| `@Service`, `@Repository`, `@Component` | Register services, persistence boundaries, and mappers |
| `@Configuration`, `@Value` | Register CORS config and inject allowed origins |
| `@Valid` | Runs Jakarta validation before the service is called |
| `@NotBlank`, `@Size(max=80)` | Validate player names |
| `@NotNull` | Require player UUIDs in commands |
| `@Min(0)`, `@Max(2)` | Restrict coordinates to the board |
| `@RestControllerAdvice`, `@ExceptionHandler` | Map exceptions to stable API errors globally |

Annotation validation checks request shape. Services handle persisted-state rules that annotations cannot express: existence, room readiness, membership, turn order, occupied cells, and active status.

### Cassandra mapping

| Annotation | Effect |
|---|---|
| `@Table` | Maps an entity to a Cassandra table |
| `@PrimaryKey` | Identifies a simple or embedded compound key |
| `@Column` | Maps Java names such as `gameId` to `game_id` |
| `@PrimaryKeyClass` | Declares a serializable compound key |
| `@PrimaryKeyColumn` | Defines partition/clustering position and ordering |
| `@Version` | Enables optimistic locking on `games.version` |

Methods such as `findByKeyGameId` and `findByKeyOwnerId` are derived queries: Spring Data parses paths through embedded keys and generates CQL.

## Key design decisions

### Layered API and DTO boundary

Controllers handle HTTP, services make decisions, repositories persist, and mappers expose DTOs. Persistence details such as versions and compound keys do not leak into the API. Java records keep request/response messages concise and immutable.

### Snapshot plus history

The game row is a fast current snapshot; move rows are audit/replay history. The duplication makes game reads constant-work while preserving all accepted moves. The service maintains both representations.

### Application-managed denormalization

Cassandra has no joins or foreign keys, so services validate references and explicitly write source and lookup rows. This provides predictable partition-key reads at the cost of multiple writes. These are not a cross-table ACID transaction; production hardening could add suitable logged batches, idempotent commands, an outbox, and reconciliation.

### Optimistic concurrency

`Game.version` uses `@Version`. If two clients update the same turn, one game save succeeds and the stale update becomes `409 GAME_STATE_CHANGED` instead of overwriting the board. Subsequent move and leaderboard writes remain separate operations—an explicit project-scope trade-off.

### Server-authoritative state

The server calculates symbols, board indexes, move numbers, next player, result, and timestamps. Clients submit identity and intent only, keeping rules consistent across callers.

### Reusable rooms and stable errors

A room preserves a player pairing while each rematch receives a new game ID. The global handler gives clients stable error codes rather than requiring message parsing.

### Postman-compatible presence and expiration

There is no reliable disconnect signal in a stateless REST/Postman workflow. Instead, a new room is `WAITING` until `expiresAt`. A timely guest join moves it to `ACTIVE`; a late join or start changes it to `EXPIRED` and returns `ROOM_EXPIRED`. The initial game can be started only once and only by the recorded host, after which the room is `IN_GAME`. Supplying a player UUID demonstrates authorization rules but is not authentication, because this project has no login/token system.

### Current scope

- No authentication: `playerId` is caller-supplied identity.
- STOMP uses Spring's in-memory simple broker; multi-instance deployment requires a shared broker relay.
- No pagination; list/leaderboard reads target demonstration-scale data.
- Short room keys are reserved atomically, while UUIDs remain the internal permanent identifiers.
- No migration tool; `schema.cql` is the clean/current installation schema.

## Build and troubleshooting

Build with the wrapper:

```powershell
.\mvnw.cmd package
java -jar target/tictactoe-springboot-0.0.1-SNAPSHOT.jar
```

Functional verification is the complete Postman run with Cassandra connected.

Common issues:

- **Cannot connect:** verify Cassandra is listening on `9042`, then check contact points and datacenter. In `cqlsh`, run `SELECT data_center FROM system.local;`.
- **Keyspace missing:** run `schema.cql` before Spring; table auto-creation cannot create the target keyspace.
- **Wrong Java/Python:** use separate terminals -> Java 8/Python 2.7 for the documented Cassandra tooling and Java 25 for Spring.
- **Empty Postman variables:** run from the first folder in order.
- **Unexpected persisted scores:** Cassandra survives restarts. Intentionally reset only a disposable development keyspace, recreate it, then rerun.
