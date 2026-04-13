package cz.arena.coding_arena.service;

import cz.arena.coding_arena.model.Contest;
import cz.arena.coding_arena.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContestService {

    private final ContestRepository contestRepository;

    public void validateActionInContest(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));

        // Ladder Mode is always active
        if ("LADDER".equalsIgnoreCase(contest.getMode())) {
            return;
        }

        // Live Mode requires checking start and end times
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(contest.getStartTime()) || now.isAfter(contest.getEndTime())) {
            throw new RuntimeException("Action rejected: Contest is not currently active!");
        }
    }
}