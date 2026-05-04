package cz.arena.coding_arena.repository;
import cz.arena.coding_arena.model.ContestParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContestParticipantRepository extends JpaRepository<ContestParticipant, Long> {
	Optional<ContestParticipant> findByContest_IdAndTeam_Id(Long contestId, UUID teamId);
}
