package smash.teams.be.model.schedule;

import lombok.*;
import smash.teams.be.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter // DTO 만들면 삭제해야됨
@Getter
@Table(name = "schedule_tb")
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User User;

    private String start;
    private String end;
    private String type;
    private String status;
    private String reason;
    private String master;
    private LocalDateTime finishedAt;
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
    public Schedule(Long id, smash.teams.be.model.user.User user, String start, String end, String type, String status, String reason, String master, LocalDateTime finishedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        User = user;
        this.start = start;
        this.end = end;
        this.type = type;
        this.status = status;
        this.reason = reason;
        this.master = master;
        this.finishedAt = finishedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
