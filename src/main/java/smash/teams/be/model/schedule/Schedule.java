package smash.teams.be.model.schedule;

import lombok.*;
import smash.teams.be.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "schedule_tb")
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false, length = 30)
    private LocalDateTime startDate;

    @Column(nullable = false, length = 30)
    private LocalDateTime endDate;

    @Column(nullable = false, length = 10)
    private String type;

    @Column(nullable = false, length = 10)
    private String status;

    @Column(length = 50)
    private String reason;

    private LocalDateTime finishedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Schedule(Long id, User user, LocalDateTime startDate, LocalDateTime endDate, String type, String status, String reason, LocalDateTime finishedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.status = status;
        this.reason = reason;
        this.finishedAt = finishedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
