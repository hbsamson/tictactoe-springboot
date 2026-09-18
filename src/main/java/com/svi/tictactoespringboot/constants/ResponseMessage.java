package com.svi.tictactoespringboot.constants;

public enum ResponseMessage {
    // Request validation
    VALIDATION_FAILED("Request validation failed"),
    MALFORMED_REQUEST("Request body is malformed"),
    INVALID_PATH_VALUE("Invalid value for %s"),

    // Missing resources
    PLAYER_NOT_FOUND("Player not found: %s"),
    ROOM_NOT_FOUND("Room not found: %s"),
    GAME_NOT_FOUND("Game not found: %s"),

    // Room rules
    HOST_CANNOT_JOIN("Host is already in the room"),
    ROOM_FULL("Room already has two players"),
    ROOM_NOT_READY("Room requires two players before a game can start"),

    // Game rules
    CELL_OCCUPIED("Board cell is already occupied"),
    GAME_NOT_ACTIVE("Game is no longer in progress"),
    PLAYER_NOT_IN_GAME("Player is not a member of this game"),
    OUT_OF_TURN("It is not this player's turn"),

    // Concurrency
    GAME_STATE_CHANGED("Game changed while the move was being processed"),

    // Server errors
    INTERNAL_ERROR("An unexpected error occurred");

    private final String value;

    ResponseMessage(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String format(Object... arguments) {
        return value.formatted(arguments);
    }
}

