package smash.teams.be.dto.admin;

import lombok.Getter;
import lombok.Setter;
import smash.teams.be.model.team.Team;

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
}
