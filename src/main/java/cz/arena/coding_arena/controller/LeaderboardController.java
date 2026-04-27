package cz.arena.coding_arena.controller;

import cz.arena.coding_arena.dto.LeaderboardEntryDto;
import cz.arena.coding_arena.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(@RequestParam Long contestId) {
        List<LeaderboardEntryDto> leaderboard = leaderboardService.getLeaderboard(contestId);
        return ResponseEntity.ok(leaderboard);
    }
}