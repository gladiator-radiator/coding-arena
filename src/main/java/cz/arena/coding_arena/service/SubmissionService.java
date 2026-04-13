package cz.arena.coding_arena.service;

import cz.arena.coding_arena.context.UserContext;
import cz.arena.coding_arena.dto.JudgeResponse;
import cz.arena.coding_arena.model.*;
import cz.arena.coding_arena.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final TaskAssignmentRepository assignmentRepository;
    private final TestCaseRepository testCaseRepository;
    private final SubmissionRepository submissionRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository; // Added this to fetch the Author
    private final JudgeService judgeService;
    private final ContestService contestService;

    @Transactional
    public Submission processSubmission(String sourceCode, Integer languageId, Long contestId) {
        // 1. Validate contest window
        contestService.validateActionInContest(contestId);

        Long userId = UserContext.getUserId();

        // Fetch the author (User) from the database
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found in database."));

        Team team = getTeamForUser(userId);

        // 2. Verify the team has an active locked task
        TaskAssignment assignment = assignmentRepository.findByTeamIdAndStatus(team.getId(), "LOCKED")
                .orElseThrow(() -> new RuntimeException("Your team does not have an active locked task to submit."));

        Task task = assignment.getTask();

        // 3. Load test cases for this task from the database
        List<TestCase> testCases = testCaseRepository.findByTaskId(task.getId());
        if (testCases.isEmpty()) {
            throw new RuntimeException("This task has no test cases defined.");
        }

        // 4. Run the code against all test cases in Judge0
        boolean allPassed = true;
        String finalVerdict = "Accepted";

        for (TestCase tc : testCases) {
            JudgeResponse response = judgeService.submitCode(sourceCode, languageId, tc.getInputData(), tc.getExpectedOutput());

            // If Judge0 doesn't return "Accepted" (ID 3), the test failed
            if (response.getStatus().getId() != 3) {
                allPassed = false;
                finalVerdict = response.getStatus().getDescription();
                break; // Stop testing on the first failure
            }
        }

        // 5. Save the submission record to the database (Using your exact entity fields)
        Submission submission = new Submission();
        submission.setAuthor(author); // Replaced setTeam
        submission.setTaskAssignment(assignment); // Replaced setTask
        submission.setSourceCode(sourceCode);
        submission.setLanguageId(languageId);
        submission.setVerdict(finalVerdict);
        submission.setSubmittedAt(LocalDateTime.now());

        submissionRepository.save(submission);

        // 6. If all tests passed, mark the task assignment as COMPLETED
        if (allPassed) {
            assignment.setStatus("COMPLETED");
            // Points calculation will go here later
            assignmentRepository.save(assignment);
        }

        return submission;
    }

    private Team getTeamForUser(Long userId) {
        return teamMemberRepository.findAll().stream()
                .filter(tm -> tm.getUser().getId().equals(userId))
                .map(TeamMember::getTeam)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User is not assigned to any team."));
    }
}