package cz.arena.coding_arena.service;

import cz.arena.coding_arena.model.Contest;
import cz.arena.coding_arena.model.ContestParticipant;
import cz.arena.coding_arena.model.Team;
import cz.arena.coding_arena.repository.ContestParticipantRepository;
import cz.arena.coding_arena.repository.ContestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContestServiceTest {

    @Mock
    private ContestRepository contestRepository;

    @Mock
    private ContestParticipantRepository contestParticipantRepository;

    @InjectMocks
    private ContestService contestService;

    @BeforeEach
    void setUp() {
        // MockitoExtension handles mock initialization
    }

    @Test
    void validateActionInContest_LadderMode_AlwaysPasses() {
        Contest ladderContest = new Contest();
        ladderContest.setId(1L);
        ladderContest.setMode("LADDER");

        when(contestRepository.findById(1L)).thenReturn(Optional.of(ladderContest));

        // Should not throw an exception, LADDER mode is always active
        assertDoesNotThrow(() -> contestService.validateActionInContest(1L));
    }

    @Test
    void validateActionInContest_LiveModeActive_Passes() {
        Contest liveContest = new Contest();
        liveContest.setId(2L);
        liveContest.setMode("LIVE");
        // Active time window (started 1 hour ago, ends in 1 hour)
        liveContest.setStartTime(LocalDateTime.now().minusHours(1));
        liveContest.setEndTime(LocalDateTime.now().plusHours(1));

        when(contestRepository.findById(2L)).thenReturn(Optional.of(liveContest));

        // Should pass cleanly
        assertDoesNotThrow(() -> contestService.validateActionInContest(2L));
    }

    @Test
    void validateActionInContest_LiveModeExpired_ThrowsException() {
        Contest expiredContest = new Contest();
        expiredContest.setId(3L);
        expiredContest.setMode("LIVE");
        // The window expired yesterday
        expiredContest.setStartTime(LocalDateTime.now().minusDays(2));
        expiredContest.setEndTime(LocalDateTime.now().minusDays(1));

        when(contestRepository.findById(3L)).thenReturn(Optional.of(expiredContest));

        // Must throw an exception due to expired time
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> contestService.validateActionInContest(3L));

        assertEquals("Action rejected: Contest is not currently active!", exception.getMessage());
    }

    @Test
    void ensureTeamParticipation_CreatesParticipantOnlyOnce() {
        Team team = new Team();
        UUID teamId = UUID.randomUUID();
        team.setId(teamId);

        Contest contest = new Contest();
        contest.setId(1L);

        when(contestParticipantRepository.findByContest_IdAndTeam_Id(1L, teamId)).thenReturn(Optional.empty());
        when(contestRepository.findById(1L)).thenReturn(Optional.of(contest));
        when(contestParticipantRepository.save(any(ContestParticipant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> contestService.ensureTeamParticipation(1L, team));

        verify(contestParticipantRepository).save(any(ContestParticipant.class));
        verify(contestParticipantRepository).findByContest_IdAndTeam_Id(1L, teamId);
    }

    @Test
    void enrollTeamInLadderContests_EnrollsTeamInAllLadderContests() {
        Team team = new Team();
        UUID teamId = UUID.randomUUID();
        team.setId(teamId);

        Contest ladderContest = new Contest();
        ladderContest.setId(10L);
        ladderContest.setMode("LADDER");

        Contest liveContest = new Contest();
        liveContest.setId(11L);
        liveContest.setMode("LIVE");

        when(contestRepository.findAll()).thenReturn(java.util.List.of(ladderContest, liveContest));
        when(contestParticipantRepository.findByContest_IdAndTeam_Id(10L, teamId)).thenReturn(Optional.empty());
        when(contestParticipantRepository.save(any(ContestParticipant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> contestService.enrollTeamInLadderContests(team));

        verify(contestParticipantRepository).save(any(ContestParticipant.class));
        verify(contestParticipantRepository).findByContest_IdAndTeam_Id(10L, teamId);
        verify(contestParticipantRepository, never()).findByContest_IdAndTeam_Id(11L, teamId);
    }
}
