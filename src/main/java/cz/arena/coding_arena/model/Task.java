package cz.arena.coding_arena.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tasks")
@Getter @Setter @NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT") // For longer texts we use TEXT instead of the classic VARCHAR(255)
    private String description;

    @Column(name = "points_tier", nullable = false)
    private Integer pointsTier;

    @Column(name = "time_limit_ms")
    private Integer timeLimitMs;

    @Column(name = "memory_limit_kb")
    private Integer memoryLimitKb;
    @ManyToOne
    @JoinColumn(name = "contest_id")
    private Contest contest;
}