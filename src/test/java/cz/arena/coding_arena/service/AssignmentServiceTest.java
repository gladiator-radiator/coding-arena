package cz.arena.coding_arena.service;

import cz.arena.coding_arena.context.UserContext;
import cz.arena.coding_arena.model.*;
import cz.arena.coding_arena.repository.TaskAssignmentRepository;
import cz.arena.coding_arena.repository.TaskRepository;
import cz.arena.coding_arena.repository.TeamMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskAssignmentRepository assignmentRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private ContestService contestService;

    @InjectMocks
    private AssignmentService assignmentService;

    private Team testTeam;
    private UUID teamId;

    @BeforeEach
    void setUp() {
        // Setup mock UserContext
        UserContext.setUserId(1L);

        // Setup mock data
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test_user");

        teamId = UUID.randomUUID();
        testTeam = new Team();
        testTeam.setId(teamId);
        testTeam.setName("Test Team");

        TeamMember teamMember = new TeamMember();
        teamMember.setUser(testUser);
        teamMember.setTeam(testTeam);

        // Mock getting the team for the user
        when(teamMemberRepository.findAll()).thenReturn(List.of(teamMember));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void requestTask_Success() {
        // Arrange
        Integer requestedTier = 100;
        Long contestId = 1L;
        Task mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setPointsTier(100);

        // No active locked task
        when(assignmentRepository.findByTeamIdAndStatus(teamId, "LOCKED")).thenReturn(Optional.empty());
        // Found an unsolved task
        when(taskRepository.findRandomUnsolvedTaskByTier(requestedTier, teamId)).thenReturn(Optional.of(mockTask));
        // Mock save to return the passed argument
        when(assignmentRepository.save(any(TaskAssignment.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        TaskAssignment result = assignmentService.requestTask(requestedTier, contestId);

        // Assert
        assertNotNull(result);
        assertEquals("LOCKED", result.getStatus());
        assertEquals(0, result.getPenaltyApplied());
        assertEquals(mockTask, result.getTask());
        assertEquals(testTeam, result.getTeam());

        // Verify dependencies were called
        verify(contestService).validateActionInContest(contestId);
        verify(contestService).ensureTeamParticipation(contestId, testTeam);
        verify(assignmentRepository).save(any(TaskAssignment.class));
    }

    @Test
    void requestTask_FailsWhenAlreadyLocked() {
        // Arrange
        when(assignmentRepository.findByTeamIdAndStatus(teamId, "LOCKED"))
                .thenReturn(Optional.of(new TaskAssignment()));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> assignmentService.requestTask(100, 1L));

        assertEquals("Your team already has an active locked task!", exception.getMessage());

        // Verify task search and save were NEVER called
        verify(taskRepository, never()).findRandomUnsolvedTaskByTier(anyInt(), any(UUID.class));
        verify(assignmentRepository, never()).save(any(TaskAssignment.class));
    }

    @Test
    void requestTask_FailsWhenNoTasksLeft() {
        // Arrange
        when(assignmentRepository.findByTeamIdAndStatus(teamId, "LOCKED")).thenReturn(Optional.empty());
        // Return empty optional meaning no tasks left in this tier
        when(taskRepository.findRandomUnsolvedTaskByTier(100, teamId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> assignmentService.requestTask(100, 1L));

        assertEquals("No tasks left in this tier for your team!", exception.getMessage());
    }

    @Test
    void surrenderTask_Success() {
        // Arrange
        TaskAssignment lockedAssignment = new TaskAssignment();
        lockedAssignment.setStatus("LOCKED");
        lockedAssignment.setTeam(testTeam);

        when(assignmentRepository.findByTeamIdAndStatus(teamId, "LOCKED"))
                .thenReturn(Optional.of(lockedAssignment));

        // Act
        assignmentService.surrenderTask();

        // Assert
        assertEquals("SURRENDERED", lockedAssignment.getStatus());
        assertEquals(50, lockedAssignment.getPenaltyApplied());
        verify(assignmentRepository).save(lockedAssignment);
    }

    @Test
    void surrenderTask_FailsWhenNoLockedTask() {
        // Arrange
        when(assignmentRepository.findByTeamIdAndStatus(teamId, "LOCKED"))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> assignmentService.surrenderTask());

        assertEquals("No active task to surrender.", exception.getMessage());
        verify(assignmentRepository, never()).save(any());
    }
}
