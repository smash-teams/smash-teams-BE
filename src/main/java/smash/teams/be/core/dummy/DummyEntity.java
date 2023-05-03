package smash.teams.be.core.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.Status;
import smash.teams.be.model.user.User;

import java.time.LocalDateTime;

public class DummyEntity { // check - newMock** 메서드들은 나중에 valid 추가하면 notNull인 값들 추가로 넣어주기
    public User newUser(String name) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(name)
                .password(passwordEncoder.encode("1234"))
                .email(name + "@gmail.com")
                .phoneNumber("010-1234-5678")
                .profileImage(null)
                .remain(20)
                .role(Role.USER.getRole())
                .status(Status.ACTIVE.getStatus())
                .build();
    }

    public User newManager(String name) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(name)
                .password(passwordEncoder.encode("1234"))
                .email(name + "@gmail.com")
                .phoneNumber("010-1234-5678")
                .profileImage(null)
                .remain(20)
                .role(Role.MANAGER.getRole())
                .status(Status.ACTIVE.getStatus())
                .build();
    }

    public User newCeo(String name) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(name)
                .password(passwordEncoder.encode("1234"))
                .email(name + "@gmail.com")
                .phoneNumber("010-1234-5678")
                .profileImage(null)
                .remain(20)
                .role(Role.CEO.getRole())
                .status(Status.ACTIVE.getStatus())
                .build();
    }

    public User newAdmin(String name) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(name)
                .password(passwordEncoder.encode("1234"))
                .email(name + "@gmail.com")
                .phoneNumber("010-1234-5678")
                .profileImage(null)
                .remain(20)
                .role(Role.ADMIN.getRole())
                .status(Status.ACTIVE.getStatus())
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
                .profileImage(null)
                .remain(20)
                .role(Role.USER.getRole())
                .status(Status.ACTIVE.getStatus())
                .build();
    }

//    public User newMockUser(Long id, String name){
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        return User.builder()
//                .id(id)
//                .username(username)
//                .password(passwordEncoder.encode("1234"))
//                .fullName(fullName)
//                .email(username+"@nate.com")
//                .role("USER")
//                .status(true)
//                .createdAt(LocalDateTime.now())
//                .build();
//    }

    public User newMockAdmin(Long id, String name) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .name(name)
                .password(passwordEncoder.encode("1234"))
                .email(name + "@gmail.com")
                .phoneNumber("010-1234-5678")
                .profileImage(null)
                .remain(20)
                .role(Role.ADMIN.getRole())
                .status(Status.ACTIVE.getStatus())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Team newTeam(String teamName) {
        return Team.builder()
                .teamName(teamName)
                .build();
    }
}
