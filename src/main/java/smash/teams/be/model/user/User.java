package smash.teams.be.model.user;

import lombok.*;
import smash.teams.be.model.team.Team;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter // DTO 만들면 삭제해야됨
@Getter
@Table(name = "user_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    private String profileImage;
    private String role;
    private String status;
    private Integer remain;
    private LocalDateTime startWork;
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
    public User(Long id, Team team, String name, String password, String email, String phoneNumber, String profileImage, String role, String status, Integer remain, LocalDateTime startWork, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.team = team;
        this.name = name;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
        this.role = role;
        this.status = status;
        this.remain = remain;
        this.startWork = startWork;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
