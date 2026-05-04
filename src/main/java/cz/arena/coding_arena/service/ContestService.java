package cz.arena.coding_arena.service;

import cz.arena.coding_arena.model.Contest;
import cz.arena.coding_arena.model.ContestParticipant;
import cz.arena.coding_arena.model.Team;
import cz.arena.coding_arena.repository.ContestParticipantRepository;
import cz.arena.coding_arena.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContestService {

    private final ContestRepository contestRepository;
    private final ContestParticipantRepository contestParticipantRepository;

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

    @Transactional
    public void ensureTeamParticipation(Long contestId, Team team) {
        contestParticipantRepository.findByContest_IdAndTeam_Id(contestId, team.getId())
                .orElseGet(() -> {
                    Contest contest = contestRepository.findById(contestId)
                            .orElseThrow(() -> new RuntimeException("Contest not found"));

                    ContestParticipant participant = new ContestParticipant();
                    participant.setContest(contest);
                    participant.setTeam(team);
                    participant.setStartedAt(LocalDateTime.now());
                    return contestParticipantRepository.save(participant);
                });
    }

    @Transactional
    public void enrollTeamInLadderContests(Team team) {
        List<Contest> ladderContests = contestRepository.findAll().stream()
                .filter(contest -> "LADDER".equalsIgnoreCase(contest.getMode()))
                .toList();

        for (Contest contest : ladderContests) {
            contestParticipantRepository.findByContest_IdAndTeam_Id(contest.getId(), team.getId())
                    .orElseGet(() -> {
                        ContestParticipant participant = new ContestParticipant();
                        participant.setContest(contest);
                        participant.setTeam(team);
                        participant.setStartedAt(LocalDateTime.now());
                        return contestParticipantRepository.save(participant);
                    });
        }
    }
}