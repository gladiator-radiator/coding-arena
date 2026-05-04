package cz.arena.coding_arena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SubmissionResponseDto {
    private Long id;
    private Integer languageId;
    private LocalDateTime submittedAt;
    private String verdict;
    private Long authorId;
    private String authorUsername;
    private Long taskAssignmentId;
    // sourceCode is NOT included (transient)
    // author password is NOT included for security
}

