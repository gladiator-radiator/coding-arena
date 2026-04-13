package cz.arena.coding_arena.dto;

import lombok.Data;

@Data
public class SubmissionRequest {
    private String sourceCode;
    private Integer languageId;
    private Long contestId;
}