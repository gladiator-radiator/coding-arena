package cz.arena.coding_arena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LeaderboardEntryDto {
    private Long userId;
    private Long score;
    private LocalDateTime lastSubmissionAt;
}