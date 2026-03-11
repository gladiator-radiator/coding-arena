package cz.arena.coding_arena.dto;

import lombok.Data;

@Data
public class JudgeResponse {
    private String token;      // Unique ID
    private String stdout;     // Standard output
    private String stderr;     // Error output
    private String compile_output; // Compiler output
    private String time;       // Runtime
    private Integer memory;    // Used memory
    private Status status;     // Result

    @Data
    public static class Status {
        private Integer id;
        private String description;
    }
}