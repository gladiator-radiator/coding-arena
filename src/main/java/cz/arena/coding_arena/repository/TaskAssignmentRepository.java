package cz.arena.coding_arena.repository;
import cz.arena.coding_arena.model.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {}