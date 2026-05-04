package cz.arena.coding_arena.controller;

import cz.arena.coding_arena.context.UserContext;
import cz.arena.coding_arena.dto.TeamMemberResponseDto;
import cz.arena.coding_arena.model.Team;
import cz.arena.coding_arena.model.TeamMember;
import cz.arena.coding_arena.model.User;
import cz.arena.coding_arena.repository.TeamMemberRepository;
import cz.arena.coding_arena.repository.TeamRepository;
import cz.arena.coding_arena.repository.UserRepository;
import cz.arena.coding_arena.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final ContestService contestService;

    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        Long userId = UserContext.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Team savedTeam = teamRepository.save(team);

        TeamMember member = new TeamMember();
        member.setTeam(savedTeam);
        member.setUser(user);
        member.setIsCaptain(true);
        teamMemberRepository.save(member);

        contestService.enrollTeamInLadderContests(savedTeam);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedTeam);
    }

    @PostMapping("/{teamId}/join")
    public ResponseEntity<TeamMemberResponseDto> joinTeam(@PathVariable UUID teamId) {
        Long userId = UserContext.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));

        // Check if user is already a member of this team
        if (teamMemberRepository.findByTeamIdAndUserId(teamId, userId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "User is already a member of this team");
        }

        TeamMember member = new TeamMember();
        member.setTeam(team);
        member.setUser(user);
        member.setIsCaptain(false);
        TeamMember savedMember = teamMemberRepository.save(member);

        // Convert to DTO without password
        TeamMemberResponseDto response = new TeamMemberResponseDto(
            savedMember.getId(),
            savedMember.getTeam().getId(),
            savedMember.getUser().getId(),
            savedMember.getUser().getUsername(),
            savedMember.getUser().getEmail(),
            savedMember.getIsCaptain()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMemberResponseDto>> getTeamMembers(@PathVariable UUID teamId) {
        // Verify team exists
        teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));

        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);

        // Convert to DTOs without passwords
        List<TeamMemberResponseDto> response = members.stream()
            .map(member -> new TeamMemberResponseDto(
                member.getId(),
                member.getTeam().getId(),
                member.getUser().getId(),
                member.getUser().getUsername(),
                member.getUser().getEmail(),
                member.getIsCaptain()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}

