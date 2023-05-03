package smash.teams.be.dto.team;

import lombok.Getter;
import lombok.Setter;
import smash.teams.be.model.team.Team;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class TeamRequest {
    @Getter
    @Setter
    public static class AddInDTO {
        @Pattern(regexp = "^[가-힣]{1,19}팀$") // 오직 한글, 마지막 글자가 "팀"으로 끝나면서 글자 수가 최대 20자
        @NotEmpty
        private String teamName;

        public Team toEntity() {
            return Team.builder()
                    .teamName(teamName)
                    .build();
        }
    }
}
