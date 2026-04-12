package cz.arena.coding_arena.repository;

import cz.arena.coding_arena.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Finds a random task in the requested tier that the team has NOT completed yet
    @Query(value = "SELECT * FROM tasks t WHERE t.points_tier = :tier " +
            "AND t.id NOT IN (SELECT ta.task_id FROM task_assignments ta WHERE ta.team_id = :teamId AND ta.status = 'COMPLETED') " +
            "ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Task> findRandomUnsolvedTaskByTier(Integer tier, UUID teamId);
}