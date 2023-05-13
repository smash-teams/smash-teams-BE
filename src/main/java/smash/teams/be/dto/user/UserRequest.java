package smash.teams.be.dto.user;

import lombok.Getter;
import lombok.Setter;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.Status;
import smash.teams.be.model.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class UserRequest {
    @Getter
    @Setter
    public static class LoginInDTO {
        @NotEmpty
        @Pattern(regexp = "^(?=.{1,50}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "50자가 넘지 않도록 이메일 형식에 맞춰 작성해주세요.")
        private String email;

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,20}$",
                message = "영문, 숫자, 특수문자를 각각 1개 이상 사용하여 8~20자 이내로 작성해주세요.")
        @NotEmpty
        private String password;
    }

    @Setter
    @Getter
    public static class JoinInDTO {
        @Pattern(regexp = "^[A-Za-z가-힣]{2,20}$", message = "영문/한글 2~20자 이내로 작성해주세요")
        @NotEmpty
        private String name;

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,20}$",
                message = "영문, 숫자, 특수문자를 각각 1개 이상 사용하여 8~20자 이내로 작성해주세요.")
        @NotEmpty
        @Size(min = 4, max = 20)
        private String password;

        @NotEmpty
        @Pattern(regexp = "^(?=.{1,50}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "50자가 넘지 않도록 이메일 형식에 맞춰 작성해주세요.")
        private String email;

        @Pattern(regexp = "^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})$",
                message = "휴대폰 번호(010-1234-5678)의 형태로 작성해주세요.")
        @NotEmpty
        private String phoneNumber;

        @Pattern(regexp = "^(?:(?:19|20)\\d{2})-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[12][0-9]|3[01])$",
                message = "입사일(2023-05-10)의 형태로 작성해주세요.")
        @NotEmpty
        private String startWork;

        @NotEmpty
        private String teamName;

        public User toEntity(Team team) {
            return User.builder()
                    .name(name)
                    .password(password)
                    .email(email)
                    .role(Role.USER.getRole())
                    .status(Status.ACTIVE.getStatus())
                    .phoneNumber(phoneNumber)
                    .startWork(LocalDate.parse(startWork,DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay())
                    .team(team)
                    .remain(20)
                    .build();
        }
    }


    @Getter
    @Setter
    public static class UpdateInDTO {
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,20}$",
                message = "영문, 숫자, 특수문자를 각각 1개 이상 사용하여 8~20자 이내로 작성해주세요.")
        @NotEmpty
        private String curPassword;

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,20}$",
                message = "영문, 숫자, 특수문자를 각각 1개 이상 사용하여 8~20자 이내로 작성해주세요..")
        @NotEmpty
        private String newPassword;

        @Pattern(regexp = "^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})$",
                message = "휴대폰 번호(010-1234-5678)의 형태로 작성해주세요.")
        @NotEmpty
        private String phoneNumber;

        @Pattern(regexp = "^(?:(?:19|20)\\d{2})-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[12][0-9]|3[01])$",
                message = "입사일(2023-05-10)의 형태로 작성해주세요.")
        @NotEmpty
        private String startWork;

        public User toEntity() {
            LocalDate startDate = LocalDate.parse(startWork, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);

            return User.builder()
                    .password(newPassword)
                    .phoneNumber(phoneNumber)
                    .startWork(startDateTime)
                    .build();
        }
    }

    @Getter @Setter
    public static class CheckInDTO {
        private String email;
    }

    @Getter @Setter
    public static class WithdrawInDTO {
        private String email;
        private String password;
    }

}