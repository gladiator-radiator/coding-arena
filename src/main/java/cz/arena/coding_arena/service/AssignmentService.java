package cz.arena.coding_arena.service;

import cz.arena.coding_arena.context.UserContext;
import cz.arena.coding_arena.model.*;
import cz.arena.coding_arena.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository assignmentRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ContestService contestService;

    @Transactional
    public TaskAssignment requestTask(Integer tier, Long contestId) {
        // 1. Verify the contest is currently active
        contestService.validateActionInContest(contestId);

        Long userId = UserContext.getUserId();
        Team team = getTeamForUser(userId);

        // 2. Lock check: Prevent requesting if they already have an active task
        assignmentRepository.findByTeamIdAndStatus(team.getId(), "LOCKED")
                .ifPresent(a -> { throw new RuntimeException("Your team already has an active locked task!"); });

        // 3. Random assignment of an unsolved task
        Task task = taskRepository.findRandomUnsolvedTaskByTier(tier, team.getId())
                .orElseThrow(() -> new RuntimeException("No tasks left in this tier for your team!"));

        // 4. Save the new assignment
        TaskAssignment assignment = new TaskAssignment();
        assignment.setTeam(team);
        assignment.setTask(task);
        assignment.setStatus("LOCKED");
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setPenaltyApplied(0);

        return assignmentRepository.save(assignment);
    }

    @Transactional
    public void surrenderTask() {
        Team team = getTeamForUser(UserContext.getUserId());

        // 5. Find the locked task and apply the 50 point penalty
        TaskAssignment active = assignmentRepository.findByTeamIdAndStatus(team.getId(), "LOCKED")
                .orElseThrow(() -> new RuntimeException("No active task to surrender."));

        active.setStatus("SURRENDERED");
        active.setPenaltyApplied(50);
        assignmentRepository.save(active);
    }

    private Team getTeamForUser(Long userId) {
        return teamMemberRepository.findAll().stream()
                .filter(tm -> tm.getUser().getId().equals(userId))
                .map(TeamMember::getTeam)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User is not assigned to any team"));
    }
}