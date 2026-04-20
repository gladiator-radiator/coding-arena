package cz.arena.coding_arena.exception;

import java.time.LocalDateTime;

public record ApiError(
        String message,
        int status,
        LocalDateTime timestamp
)

{
    public ApiError(String message, int status) {
        this(message, status, LocalDateTime.now());
    }
}