package cz.arena.coding_arena.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter @Setter @NoArgsConstructor
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_code", columnDefinition = "TEXT", nullable = false)
    private String sourceCode;

    @Column(name = "language_id", nullable = false)
    private Integer languageId;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(nullable = false)
    private String verdict;

    @ManyToOne
    @JoinColumn(name = "task_assignment_id", nullable = false)
    private TaskAssignment taskAssignment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;
}