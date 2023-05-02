package smash.teams.be.model.team;

import lombok.*;
import smash.teams.be.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter // DTO 만들면 삭제해야됨
@Getter
@Table(name = "schedule_tb")
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String team;
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
    public Team(Long id, String team, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.team = team;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
