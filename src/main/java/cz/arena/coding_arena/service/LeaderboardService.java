package cz.arena.coding_arena.service;

import cz.arena.coding_arena.dto.LeaderboardEntryDto;
import cz.arena.coding_arena.repository.LeaderboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    public List<LeaderboardEntryDto> getLeaderboard(Long contestId) {
        if (contestId == null || contestId <= 0) {
            throw new IllegalArgumentException("Invalid contestId: must be a positive number");
        }

        return leaderboardRepository.getLeaderboardByContestWithPointAggregation(contestId);
    }
}