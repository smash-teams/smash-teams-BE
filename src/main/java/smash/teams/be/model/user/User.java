package smash.teams.be.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import smash.teams.be.model.team.Team;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @Column(nullable = false, length = 20)
    private String name;

    @JsonIgnore
    @Column(nullable = false, length = 60)
    private String password;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 30)
    private String phoneNumber;

    private String profileImage;

    @Column(nullable = false, length = 10)
    private String role;

    @Column(nullable = false, length = 10)
    private String status;

    @Column(nullable = false, length = 10)
    private Integer remain;

    @Column(nullable = false)
    private LocalDateTime startWork;

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
