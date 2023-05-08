package smash.teams.be.dto.schedule;

import lombok.Getter;
import lombok.Setter;
import smash.teams.be.core.util.DateUtil;
import smash.teams.be.model.schedule.Schedule;
import smash.teams.be.model.user.User;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
        private UserOutDTOWithScheduleOutDTO user;

        public ScheduleOutDTO(Schedule schedule, UserOutDTOWithScheduleOutDTO user) {
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
    public static class UserOutDTOWithScheduleOutDTO{
        private Long userId;
        private String name;
        private String email;
        private String phoneNumber;
        private String startWork;
        private String role;
        private String teamName;
        private String profileImage;

        public UserOutDTOWithScheduleOutDTO(User user) {
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

    @Getter
    @Setter
    public static class ListOutDto {
        private List<ScheduleDto> scheduleList;

        public ListOutDto(List<Schedule> scheduleList) { // scheduleListPS
            this.scheduleList = scheduleList.stream()
                    .map(schedule -> new ScheduleDto(schedule, new ScheduleDto.UserDto(schedule.getUser())))
                    .collect(Collectors.toList());
        }

        @Getter
        @Setter
        public static class ScheduleDto {
            private Long scheduleId;
            private UserDto user;
            private String type;
            private String reason;
            private String startDate;
            private String endDate;

            public ScheduleDto(Schedule schedule, UserDto user) {
                this.scheduleId = schedule.getId();
                this.user = user;
                this.type = schedule.getType();
                this.reason = schedule.getReason();
                this.startDate = DateUtil.toStringFormat(schedule.getStartDate());
                this.endDate = DateUtil.toStringFormat(schedule.getEndDate());
            }

            @Getter
            @Setter
            public static class UserDto {
                private Long userId;
                private String name;
                private String email;
                private String teamName;
                private String role;
                private String profileImage;

                public UserDto(User user) { // 1) User -> UserDto
                    this.userId = user.getId();
                    this.name = user.getName();
                    this.email = user.getEmail();
                    this.teamName = user.getTeam().getTeamName();
                    this.role = user.getRole();
                    this.profileImage = user.getProfileImage();
                }
            }
        }
    }
}