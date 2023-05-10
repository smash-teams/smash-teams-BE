package smash.teams.be.core.dummy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import smash.teams.be.dto.admin.AdminResponse;
import smash.teams.be.model.schedule.Schedule;
import smash.teams.be.model.schedule.Type;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.Status;
import smash.teams.be.model.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DummyEntity {
    private final int SIZE = 12; // 한 페이지 당 사용자 정보 최대 갯수

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
                .password(passwordEncoder.encode("seungmin1234"))
                .email(name + "@gmail.com")
                .phoneNumber("010-1234-5678")
                .remain(20)
                .role(Role.USER.getRole())
                .status(Status.ACTIVE.getStatus())
                .startWork(LocalDateTime.now())
                .build();
    }

    public User newManagerWithTeam(String name, Team team) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .team(team)
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

    public User newMockUserWithTeam(Long id, String name, Team team) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .team(team)
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

    public Schedule newScheduleForTest(Long scheduleId, Long userId, String userRole, String userName, Long teamId, String teamName, String scheduleStatus, String scheduleReason) {
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
                        .email(userName + "@gmail.com")
                        .phoneNumber("010-" + userId + userId + userId + userId + "-" + userId + userId + userId + userId)
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

    public Schedule newScheduleForRepoTest(User user, String type, String status, String reason) {
        return Schedule.builder()
                .user(user)
                .startDate(LocalDateTime.parse("2022-01-01T09:00:00"))
                .endDate(LocalDateTime.parse("2022-01-01T09:00:00"))
                .type(type)
                .status(status)
                .reason(reason)
                .createdAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .updatedAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .finishedAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .build();
    }

    public User newUserForRepoTest(Team team, String userName, String role) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .team(team)
                .name(userName)
                .password(passwordEncoder.encode("1234"))
                .email(userName + "@gmail.com")
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

    public Team newTeamForRepoTest(String teamName) {
        return Team.builder()
                .teamName(teamName)
                .createdAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .updatedAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .build();
    }

    public List<User> newMockUserList(String teamName) {
        return new ArrayList<>() {{
            add((newMockUser(1L, "이승민")));
            add((newMockUser(2L, "이윤경")));
            add((newMockUser(3L, "이한울")));
        }};
    }

    public List<AdminResponse.UserListDTO> newMockUserListByDTO(String teamName) {
        return new ArrayList<>() {{
            add(new AdminResponse.UserListDTO(
                    newMockUser(1L, "이승민"), LocalDateTime.now().toLocalDate().toString(), teamName));
            add(new AdminResponse.UserListDTO(
                    newMockUser(2L, "이윤경"), LocalDateTime.now().toLocalDate().toString(), teamName));
            add(new AdminResponse.UserListDTO(
                    newMockUser(3L, "이한울"), LocalDateTime.now().toLocalDate().toString(), teamName));
        }};
    }

    public List<AdminResponse.TeamListDTO> newMockTeamList() {
        return new ArrayList<>() {{
            add(new AdminResponse.TeamListDTO(
                    newMockTeam(1L, "개발팀"), 3));
            add(new AdminResponse.TeamListDTO(
                    newMockTeam(2L, "회계팀"), 1));
            add(new AdminResponse.TeamListDTO(
                    newMockTeam(3L, "마케팅팀"), 1));
        }};
    }

    public Page<User> newMockUserPage(String teamName) {
        return new PageImpl<>(
                newMockUserList(teamName),
                PageRequest.of(0, SIZE),
                3
        );
    }

    public Schedule newMockSchedule(User user) {
        return Schedule.builder()
                .id(1L)
                .user(user)
                .startDate(LocalDateTime.parse("2023-03-03T09:00:00"))
                .endDate(LocalDateTime.parse("2023-03-03T12:00:00"))
                .type(Type.HALFOFF.getType())
                .status(smash.teams.be.model.schedule.Status.LAST.getStatus())
                .reason("병원 예약")
                .createdAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .updatedAt(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .build();
    }

    public Schedule newMockScheduleWithUserWithTeam(Long scheduleId, Long userId, Long teamId, String userName, String teamName) {
        return Schedule.builder()
                .id(scheduleId)
                .user(User.builder()
                        .id(userId)
                        .team(Team.builder()
                                .id(teamId)
                                .teamName(teamName)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build())
                        .name(userName)
                        .email(userName + "@gmail.com")
                        .phoneNumber("010-1111-1111")
                        .role("USER")
                        .status("ACTIVATE")
                        .remain(20)
                        .startWork(LocalDate.parse("2020-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .profileImage(null)
                        .build())
                .startDate(LocalDateTime.parse("2022-01-01T09:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                .endDate(LocalDateTime.parse("2022-01-01T09:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                .type("DAYOFF")
                .status("APPROVED")
                .reason("여행")
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public User newMockUserUpdate(Long id, String name) {
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
                .profileImage("사진 2")
                .startWork(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public User newUserForIntergratingTest(String name, Team team, String role, String email){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder().name(name).team(team).role(role)
                .remain(20).email(email+"@gmail.com").password(passwordEncoder.encode("1234"))
                .phoneNumber("010-1111-1111").status(Status.ACTIVE.getStatus()).startWork(LocalDateTime.now())
                .profileImage(null).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
    }

    public Schedule newScheduleForIntergratingTest(User user, String type, String status){
        return Schedule.builder().user(user).type(type).status(status)
                .reason("쉬고싶음").startDate(LocalDateTime.now()).endDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).finishedAt(LocalDateTime.now()).build();
    }

    public Team newTeamForIntergratingTest(String teamName){
        return Team.builder().teamName(teamName)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
    }
}
