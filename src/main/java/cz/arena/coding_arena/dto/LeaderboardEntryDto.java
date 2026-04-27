package cz.arena.coding_arena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LeaderboardEntryDto {
    private UUID teamId;
    private String teamName;
    private Long totalPoints;
    private LocalDateTime lastSubmissionTime;
}