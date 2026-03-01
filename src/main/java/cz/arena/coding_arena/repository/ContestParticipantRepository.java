package cz.arena.coding_arena.repository;
import cz.arena.coding_arena.model.ContestParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestParticipantRepository extends JpaRepository<ContestParticipant, Long> {}