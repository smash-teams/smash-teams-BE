package smash.teams.be.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import smash.teams.be.model.user.User;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class UserRequest {

    @Getter
    @Setter
    public static class UpdateInDto {

        private String curPassword;
        private String newPassword;
        private String phoneNumber;
        private String startWork;
        private String profileImage;

        public User toEntity( ) {
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
