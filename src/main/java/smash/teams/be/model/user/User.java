package smash.teams.be.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private double remain;

    @Column(nullable = false)
    private LocalDateTime startWork;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateTeamName(Team team) { // 소속팀 변경
        this.team = team;
    }

    public void updateRole(String role) { // 권한 변경
        this.role = role;
    }

    public void updatePassword(String password) { // 비밀번호 변경
        this.password = password;
    }

    public void updateProfileImage(String profileImage) { // 프로필 변경
        this.profileImage = profileImage;
    }

    public void updatePhoneNumber(String phoneNumber) { // 폰번호 변경
        this.phoneNumber = phoneNumber;
    }

    public void changeRemain(double remain) {
        this.remain = remain;
    }

    public void updateStartWork(LocalDateTime startWork) { // 입사날 변경(고정 또는 null)
        this.startWork = startWork;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public User(Long id, Team team, String name, String password, String email, String phoneNumber, String profileImage, String role, String status, double remain, LocalDateTime startWork, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    public void uploadImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void changeStatus(String status){
        this.status = Status.INACTIVE.getStatus();
    }
}
