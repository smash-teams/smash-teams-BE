package smash.teams.be.dto.user;

import lombok.Getter;
import lombok.Setter;
import smash.teams.be.model.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
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

        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+])[A-Za-z\\d~!@#$%^&*()_+]{8,20}$",
                message = "영문 대소문자, 숫자, 특수문자만 사용하여 8~20자 이내로 작성해주세요.")
        @NotEmpty
        private String password;
    }

    @Getter
    @Setter
    public static class UpdateInDTO {
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+])[A-Za-z\\d~!@#$%^&*()_+]{8,20}$",
                message = "영문 대소문자, 숫자, 특수문자만 사용하여 8~20자 이내로 작성해주세요.")
        @NotEmpty
        private String curPassword;

        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+])[A-Za-z\\d~!@#$%^&*()_+]{8,20}$",
                message = "영문 대소문자, 숫자, 특수문자만 사용하여 8~20자 이내로 작성해주세요.")
        @NotEmpty
        private String newPassword;

        @Pattern(regexp = "^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})$",
                message = "휴대폰 번호(010-1234-5678)의 형태로 작성해주세요.")
        @NotEmpty
        private String phoneNumber;

        @Pattern(regexp = "^(?:(?:19|20)\\d{2})-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[12][0-9]|3[01])$",
                message = "입사일(2023-05-10)의 형태로 작성해주세요.")
        @NotEmpty
        private String startWork;

        @NotEmpty
        private String profileImage;

        public User toEntity() {
            LocalDate startDate = LocalDate.parse(startWork, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);

            return User.builder()
                    .password(newPassword)
                    .phoneNumber(phoneNumber)
                    .startWork(startDateTime)
                    .profileImage(profileImage)
                    .build();
        }
    }
}