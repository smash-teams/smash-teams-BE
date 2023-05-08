package smash.teams.be.dto.schedule;

import lombok.Getter;
import lombok.Setter;
import smash.teams.be.model.schedule.Schedule;
import smash.teams.be.model.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class ScheduleRequest {
    @Getter
    @Setter
    public static class MakeScheduleRequestInDTO {
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", message = "다음과 같은 형식으로 요청해주세요.(2023-03-03T09:00:00)")
        @NotEmpty
        private String startDate;

        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", message = "다음과 같은 형식으로 요청해주세요.(2023-03-03T09:00:00)")
        @NotEmpty
        private String endDate;

        @Pattern(regexp = "^(DAYOFF|HALFOFF|SHIFT)$", message = "DAYOFF, HALFOFF, SHIFT 중 하나의 형태로 요청해주세요.")
        @NotEmpty
        private String type;

        @Size(min = 0, max = 50, message = "0~50자 이내로 작성해주세요.")
        private String reason;

        public Schedule toEntity(User user, String status) {
            return Schedule.builder()
                    .user(user)
                    .startDate(LocalDateTime.parse(startDate))
                    .endDate(LocalDateTime.parse(endDate))
                    .type(type)
                    .status(status)
                    .reason(reason)
                    .build();
        }
    }
}
