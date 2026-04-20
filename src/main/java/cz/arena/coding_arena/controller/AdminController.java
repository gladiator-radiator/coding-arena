package cz.arena.coding_arena.controller;

import cz.arena.coding_arena.context.UserContext;
import cz.arena.coding_arena.model.Task;
import cz.arena.coding_arena.model.TestCase;
import cz.arena.coding_arena.model.User;
import cz.arena.coding_arena.repository.TaskRepository;
import cz.arena.coding_arena.repository.TestCaseRepository;
import cz.arena.coding_arena.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TaskRepository taskRepository;
    private final TestCaseRepository testCaseRepository;
    private final UserRepository userRepository;

    private void verifyAdmin() {
        Long userId = UserContext.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Admin role required.");
        }
    }

    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        verifyAdmin();
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @PostMapping("/tasks/{taskId}/testcases")
    public ResponseEntity<TestCase> createTestCase(@PathVariable Long taskId, @RequestBody TestCase testCase) {
        verifyAdmin();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task not found"));

        testCase.setTask(task);
        TestCase savedTestCase = testCaseRepository.save(testCase);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTestCase);
    }
}

