package smash.teams.be.dto.user;

import lombok.Getter;
import lombok.Setter;
import smash.teams.be.core.util.DateUtil;
import smash.teams.be.model.user.User;

public class UserResponse {

    @Getter
    @Setter
    public static class findMyInfoOutDTO {
        private Long id;
        private String name;
        private String email;
        private String startWork;
        private String profileImage;

        public findMyInfoOutDTO(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.startWork = DateUtil.toStringFormat(user.getStartWork());
            this.profileImage = user.getProfileImage();
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
            this.startWork = DateUtil.toStringFormat(user.getStartWork());
            this.profileImage = user.getProfileImage();
        }
    }
}
