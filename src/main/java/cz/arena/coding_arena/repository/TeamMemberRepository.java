package cz.arena.coding_arena.repository;
import cz.arena.coding_arena.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByTeamId(UUID teamId);
    Optional<TeamMember> findByTeamIdAndUserId(UUID teamId, Long userId);
}
