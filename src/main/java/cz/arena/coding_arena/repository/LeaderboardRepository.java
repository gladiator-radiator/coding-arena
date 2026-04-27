package cz.arena.coding_arena.repository;

import cz.arena.coding_arena.dto.LeaderboardEntryDto;
import cz.arena.coding_arena.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardRepository extends JpaRepository<Submission, Long> {
    
    @Query("""
            SELECT new cz.arena.coding_arena.dto.LeaderboardEntryDto(
                s.author.id,
                COALESCE(SUM(
                    CASE 
                        WHEN s.verdict = 'COMPLETED' 
                        THEN s.taskAssignment.task.pointsTier 
                        ELSE -50 
                    END
                ), 0),
                MAX(s.submittedAt)
            )
            FROM Submission s
            WHERE s.taskAssignment.task.contest.id = :contestId
            GROUP BY s.author.id
            ORDER BY 2 DESC, 3 ASC
            """)
            List<LeaderboardEntryDto> getLeaderboardByContestWithPointAggregation(@Param("contestId") Long contestId);
}