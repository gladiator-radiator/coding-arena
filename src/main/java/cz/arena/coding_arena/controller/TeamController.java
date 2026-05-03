package cz.arena.coding_arena.controller;

import cz.arena.coding_arena.context.UserContext;
import cz.arena.coding_arena.model.Team;
import cz.arena.coding_arena.model.TeamMember;
import cz.arena.coding_arena.model.User;
import cz.arena.coding_arena.repository.TeamMemberRepository;
import cz.arena.coding_arena.repository.TeamRepository;
import cz.arena.coding_arena.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

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

        return ResponseEntity.status(HttpStatus.CREATED).body(savedTeam);
    }

    @PostMapping("/{teamId}/join")
    public ResponseEntity<TeamMember> joinTeam(@PathVariable UUID teamId) {
        Long userId = UserContext.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));

        TeamMember member = new TeamMember();
        member.setTeam(team);
        member.setUser(user);
        member.setIsCaptain(false);
        TeamMember savedMember = teamMemberRepository.save(member);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedMember);
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMember>> getTeamMembers(@PathVariable UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));

        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);
        return ResponseEntity.ok(members);
    }
}

