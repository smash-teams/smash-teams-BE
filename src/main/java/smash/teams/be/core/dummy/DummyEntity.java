package smash.teams.be.core.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.Status;
import smash.teams.be.model.user.User;

import java.time.LocalDateTime;

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
                .startWork(LocalDateTime.now())
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
}
