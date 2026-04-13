package cz.arena.coding_arena.repository;

import cz.arena.coding_arena.model.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    // Used to check if a team currently has a task "LOCKED"
    Optional<TaskAssignment> findByTeamIdAndStatus(UUID teamId, String status);
}