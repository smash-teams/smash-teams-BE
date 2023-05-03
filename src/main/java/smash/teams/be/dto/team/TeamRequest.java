package smash.teams.be.dto.team;

import lombok.Getter;
import lombok.Setter;
import smash.teams.be.model.team.Team;

public class TeamRequest {
    @Getter
    @Setter
    public static class AddDTO {
        private String teamName;

        public Team toEntity() {
            return Team.builder()
                    .teamName(teamName)
                    .build();
        }
    }
}
