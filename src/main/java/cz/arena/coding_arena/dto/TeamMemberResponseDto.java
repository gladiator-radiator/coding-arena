package cz.arena.coding_arena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TeamMemberResponseDto {
    private Long id;
    private UUID teamId;
    private Long userId;
    private String username;
    private String email;
    private Boolean isCaptain;
    // password and passwordHash are NOT included for security
}

