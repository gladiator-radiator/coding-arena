package cz.arena.coding_arena.service;

import cz.arena.coding_arena.model.Contest;
import cz.arena.coding_arena.repository.ContestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ContestServiceTest {

    @Mock
    private ContestRepository contestRepository;

    @InjectMocks
    private ContestService contestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
}
