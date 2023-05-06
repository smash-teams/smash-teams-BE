package smash.teams.be.dto.schedule;

import lombok.Getter;
import lombok.Setter;
import smash.teams.be.model.schedule.Schedule;
import smash.teams.be.model.user.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScheduleResponse {

    @Getter @Setter
    public static class ScheduleListDTO{
        private List<ScheduleOutDTO> scheduleList;

        public ScheduleListDTO(List<ScheduleOutDTO> scheduleList) {
            this.scheduleList = scheduleList;
        }
    }

    @Getter @Setter
    public static class ScheduleOutDTO{
        private Long scheduleId;
        private String startDate;
        private String endDate;
        private String type;
        private String status;
        private String reason;
        private UserOutWithScheduleOutDTO user;

        public ScheduleOutDTO(Schedule schedule, UserOutWithScheduleOutDTO user) {
            this.scheduleId = schedule.getId();
            this.startDate = schedule.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.endDate = schedule.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.type = schedule.getType();
            this.status = schedule.getStatus();
            this.reason = schedule.getReason();
            this.user = user;
        }
    }

    @Getter @Setter
    public static class UserOutWithScheduleOutDTO{
        private Long userId;
        private String name;
        private String email;
        private String phoneNumber;
        private String startWork;
        private String role;
        private String teamName;
        private String profileImage;

        public UserOutWithScheduleOutDTO(User user) {
            this.userId = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.phoneNumber = user.getPhoneNumber();
            this.startWork = user.getStartWork().format(DateTimeFormatter.ISO_LOCAL_DATE);
            this.role = user.getRole();
            this.teamName = user.getTeam().getTeamName();
            this.profileImage = user.getProfileImage();
        }
    }
}