package smash.teams.be.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import smash.teams.be.model.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class UserRequest {

    @Getter
    @Setter
    public static class UpdateInDto {
        @Size(min = 4, max = 20)
        @NotEmpty
        private String curPassword;
        @Size(min = 4, max = 20)
        @NotEmpty
        private String newPassword;
        @NotEmpty
        private String phoneNumber;
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
