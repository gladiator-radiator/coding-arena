package cz.arena.coding_arena.controller;

import cz.arena.coding_arena.dto.SubmissionRequest;
import cz.arena.coding_arena.dto.SubmissionResponseDto;
import cz.arena.coding_arena.model.Submission;
import cz.arena.coding_arena.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponseDto> submitCode(@RequestBody SubmissionRequest request) {
        Submission result = submissionService.processSubmission(
                request.getSourceCode(),
                request.getLanguageId(),
                request.getContestId()
        );

        // Convert to DTO without exposing author password
        SubmissionResponseDto response = new SubmissionResponseDto(
            result.getId(),
            result.getLanguageId(),
            result.getSubmittedAt(),
            result.getVerdict(),
            result.getAuthor().getId(),
            result.getAuthor().getUsername(),
            result.getTaskAssignment().getId()
        );

        return ResponseEntity.ok(response);
    }
}