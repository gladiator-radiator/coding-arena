package cz.arena.coding_arena.repository;

import cz.arena.coding_arena.dto.LeaderboardEntryDto;
import cz.arena.coding_arena.model.ContestParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardRepository extends JpaRepository<ContestParticipant, Long> {

    @Query("""
            SELECT new cz.arena.coding_arena.dto.LeaderboardEntryDto(
                t.id,
                t.name,
                (COALESCE(SUM(CASE WHEN ta.status = 'COMPLETED' THEN CAST(ta.task.pointsTier AS long) ELSE 0L END), 0L) 
                 - COALESCE(SUM(CAST(ta.penaltyApplied AS long)), 0L)),
                (SELECT MAX(s.submittedAt) FROM Submission s WHERE s.taskAssignment.team.id = t.id AND s.verdict = 'Accepted')
            )
            FROM ContestParticipant cp
            JOIN cp.team t
            LEFT JOIN TaskAssignment ta ON ta.team.id = t.id
            WHERE cp.contest.id = :contestId
            GROUP BY t.id, t.name
            ORDER BY 3 DESC, 4 ASC
            """)
    List<LeaderboardEntryDto> getLeaderboardByContestWithPointAggregation(@Param("contestId") Long contestId);
}