package smash.teams.be.dto.admin;

import lombok.Getter;
import lombok.Setter;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.user.User;

import java.util.List;

public class AdminResponse {
    @Getter
    @Setter
    public static class AddOutDTO {
        private Long teamId;
        private String teamName;
        private Integer teamCount;

        public AddOutDTO(Team team) {
            this.teamId = team.getId();
            this.teamName = team.getTeamName();
            this.teamCount = 0; // 처음에 DB에 팀을 추가하면 소속된 인원은 0명이므로
        }
    }

    @Getter
    @Setter
    public static class TeamListDTO { // GetAdminPageOutDTO에서 사용
        private Long teamId;
        private String teamName;
        private Integer teamCount;

        public TeamListDTO(Team team, Integer teamCount) {
            this.teamId = team.getId();
            this.teamName = team.getTeamName();
            this.teamCount = teamCount;
        }
    }

    @Getter
    @Setter
    public static class UserListDTO { // GetAdminPageOutDTO에서 사용
        private Long userId;
        private String profileImage;
        private String name;
        private String email;
        private String phoneNumber;
        private String startWork; // LocalDateTime -> "2010-05-10"
        private String teamName;
        private String role;

        public UserListDTO(User user, String startWork, String teamName) {
            this.userId = user.getId();
            this.profileImage = user.getProfileImage();
            this.name = user.getName();
            this.email = user.getEmail();
            this.phoneNumber = user.getPhoneNumber();
            this.startWork = startWork;
            this.teamName = teamName;
            this.role = user.getRole();
        }
    }

    @Getter
    @Setter
    public static class GetAdminPageOutDTO {
        private List<TeamListDTO> teamList;
        private List<UserListDTO> userList;
        private Integer size;
        private Long totalElements;
        private Integer totalPages;
        private Integer curPage;
        private Boolean first;
        private Boolean last;
        private Boolean empty;

        public GetAdminPageOutDTO(List<TeamListDTO> teamList, List<UserListDTO> userList, Integer size, Long totalElements, Integer totalPages, Integer curPage, Boolean first, Boolean last, Boolean empty) {
            this.teamList = teamList;
            this.userList = userList;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.curPage = curPage;
            this.first = first;
            this.last = last;
            this.empty = empty;
        }
    }
}