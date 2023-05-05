package smash.teams.be.core.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import smash.teams.be.model.schedule.Schedule;
import smash.teams.be.model.schedule.Type;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.Status;
import smash.teams.be.model.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DummyEntity {
    public User newUser(String name) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(name)
                .password(passwordEncoder.encode("1234"))
                .email(name + "@gmail.com")
                .phoneNumber("010-1234-5678")
                .remain(20)
                .role(Role.USER.getRole())
                .status(Status.ACTIVE.getStatus())
                .startWork(LocalDateTime.now().toLocalDate().atStartOfDay())
                .build();
    }

    public User newManager(String name) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(name)
                .password(passwordEncoder.encode("1234"))
                .email(name + "@gmail.com")
                .phoneNumber("010-1234-5678")
                .remain(20)
                .role(Role.MANAGER.getRole())
                .status(Status.ACTIVE.getStatus())
                .startWork(LocalDateTime.now())
                .build();
    }

    public User newCeo(String name) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(name)
                .password(passwordEncoder.encode("1234"))
                .email(name + "@gmail.com")
                .phoneNumber("010-1234-5678")
                .remain(20)
                .role(Role.CEO.getRole())
                .status(Status.ACTIVE.getStatus())
                .startWork(LocalDateTime.now())
                .build();
    }

    public User newAdmin(String name) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(name)
                .password(passwordEncoder.encode("1234"))
                .email(name + "@gmail.com")
                .phoneNumber("010-1234-5678")
                .remain(20)
                .role(Role.ADMIN.getRole())
                .status(Status.ACTIVE.getStatus())
                .startWork(LocalDateTime.now())
                .build();
    }

    public User newUserWithTeam(String name, Team team) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .team(team)
                .name(name)
                .password(passwordEncoder.encode("1234"))
                .email(name + "@gmail.com")
                .phoneNumber("010-1234-5678")
                .remain(20)
                .role(Role.USER.getRole())
                .status(Status.ACTIVE.getStatus())
                .startWork(LocalDateTime.now())
                .build();
    }

    public User newMockUser(Long id, String name) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .name(name)
                .password(passwordEncoder.encode("1234"))
                .email(name + "@gmail.com")
                .phoneNumber("010-1234-5678")
                .remain(20)
                .role(Role.USER.getRole())
                .status(Status.ACTIVE.getStatus())
                .startWork(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public User newMockAdmin(Long id, String name) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .name(name)
                .password(passwordEncoder.encode("1234"))
                .email(name + "@gmail.com")
                .phoneNumber("010-1234-5678")
                .remain(20)
                .role(Role.ADMIN.getRole())
                .status(Status.ACTIVE.getStatus())
                .startWork(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Team newTeam(String teamName) {
        return Team.builder()
                .teamName(teamName)
                .build();
    }

    public Team newMockTeam(Long id, String teamName) {
        return Team.builder()
                .id(id)
                .teamName(teamName)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Schedule newScheduleForTest(Long scheduleId, Long userId, String userRole, String userName, Long teamId, String teamName, String scheduleStatus, String scheduleReason){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return Schedule.builder()
                .id(scheduleId)
                .startDate(LocalDateTime.parse("2022-01-01T09:00:00"))
                .endDate(LocalDateTime.parse("2022-01-01T09:00:00"))
                .type(Type.DAYOFF.getType())
                .status(scheduleStatus)
                .reason(scheduleReason)
                .createdAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .updatedAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .finishedAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .user(User.builder()
                        .id(userId)
                        .team(Team.builder()
                                .id(teamId)
                                .teamName(teamName)
                                .createdAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                                .updatedAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                                .build())
                        .name(userName)
                        .password(passwordEncoder.encode("1234"))
                        .email(userName+"@gmail.com")
                        .phoneNumber("010-"+userId+userId+userId+userId+"-"+userId+userId+userId+userId)
                        .role(userRole)
                        .status(Status.ACTIVE.getStatus())
                        .remain(20)
                        .startWork(LocalDate.parse("2020-01-01").atStartOfDay())
                        .createdAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                        .updatedAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                        .profileImage(null)
                        .build())
                .build();
    }

    public Schedule newScheduleForRepoTest(User user){
        return Schedule.builder()
                .user(user)
                .startDate(LocalDateTime.parse("2022-01-01T09:00:00"))
                .endDate(LocalDateTime.parse("2022-01-01T09:00:00"))
                .type("DAYOFF")
                .status("APPROVED")
                .reason("여행")
                .createdAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .updatedAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .finishedAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .build();
    }

    public User newUserForRepoTest(Team team, String userName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .team(team)
                .name(userName)
                .password(passwordEncoder.encode("1234"))
                .email(userName+"@gmail.com")
                .phoneNumber("010-1111-1111")
                .role("USER")
                .status("ACTIVATE")
                .remain(20)
                .startWork(LocalDate.parse("2020-01-01").atStartOfDay())
                .createdAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .updatedAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .profileImage(null)
                .build();
    }

    public Team newTeamForRepoTest(String teamName){
        return Team.builder()
                .teamName(teamName)
                .createdAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .updatedAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .build();
    }
}
