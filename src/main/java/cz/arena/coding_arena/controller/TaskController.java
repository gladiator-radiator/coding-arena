package cz.arena.coding_arena.controller;

import cz.arena.coding_arena.model.TaskAssignment;
import cz.arena.coding_arena.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final AssignmentService assignmentService;

    @PostMapping("/request")
    public ResponseEntity<TaskAssignment> request(@RequestParam Integer tier, @RequestParam Long contestId) {
        return ResponseEntity.ok(assignmentService.requestTask(tier, contestId));
    }

    @PostMapping("/surrender")
    public ResponseEntity<String> surrender() {
        assignmentService.surrenderTask();
        return ResponseEntity.ok("Task surrendered successfully. A penalty of 50 points has been applied.");
    }
}