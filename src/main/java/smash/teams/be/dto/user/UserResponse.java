package smash.teams.be.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import smash.teams.be.model.user.User;

public class UserResponse {
    @Getter
    @Setter
    public static class LoginOutDTO {
        private LoginInfoOutDTO loginInfoOutDTO;
        private String jwt;

        public LoginOutDTO(LoginInfoOutDTO loginInfoOutDTO, String jwt) {
            this.loginInfoOutDTO = loginInfoOutDTO;
            this.jwt = jwt;
        }
    }

    @Setter
    @Getter
    public static class JoinOutDTO {
        private Long id;
        private String name;
        private String email;

        public JoinOutDTO(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
        }
    }

    @Getter
    @Setter
    public static class LoginInfoOutDTO {
        private Long id;
        private String name;
        private String email;
        private String phoneNumber;
        private String profileImage;
        private String startWork;
        private double remain;
        private String teamName;
        private String role;

        public LoginInfoOutDTO(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.phoneNumber = user.getPhoneNumber();
            this.profileImage = user.getProfileImage();
            this.startWork = user.getStartWork().toLocalDate().toString();
            this.remain = user.getRemain();
            this.teamName = user.getTeam().getTeamName();
            this.role = user.getRole();
        }
    }

    @Getter
    @Setter
    public static class FindMyInfoOutDTO {
        private Long id;
        private String teamName;
        private String name;
        private String email;
        private String phoneNumber;
        private Double remain;
        private String startWork;
        private String profileImage;
        private String role;

        public FindMyInfoOutDTO(User user, String prefix) {
            this.id = user.getId();
            this.teamName = user.getTeam().getTeamName();
            this.name = user.getName();
            this.email = user.getEmail();
            this.phoneNumber = user.getPhoneNumber();
            this.remain = user.getRemain();
            this.startWork = user.getStartWork().toLocalDate().toString();
            this.profileImage = prefix + user.getProfileImage();
            this.role = user.getRole();
        }
    }

    @Getter
    @Setter
    public static class UpdateOutDTO {
        private String phoneNumber;
        private String startWork;
        private String profileImage;

        public UpdateOutDTO(User user) {
            this.phoneNumber = user.getPhoneNumber();
            this.startWork = user.getStartWork().toLocalDate().toString();
            this.profileImage = user.getProfileImage();
        }
    }

    @Getter @Setter
    public static class CheckOutDTO {
        private boolean checkEmail;

        public CheckOutDTO(boolean check) {
            this.checkEmail = check;
        }
    }

    @Getter
    @Setter
    public static class UpdateImageOutDTO {
        private String profileImage;

        public UpdateImageOutDTO(User user) {
            this.profileImage = user.getProfileImage();
        }
    }
}
