package smash.teams.be.dto.admin;

import lombok.Getter;
import lombok.Setter;
import smash.teams.be.model.team.Team;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class AdminRequest {
    @Getter
    @Setter
    public static class AddInDTO {
        @Pattern(regexp = "^[가-힣]{2,19}팀$", message = "한글 2~20자 이내로 작성해주세요.")
        @NotEmpty
        private String teamName;

        public Team toEntity() {
            return Team.builder()
                    .teamName(teamName)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class UpdateAuthAndTeamInDTO {
        @NotNull
        private Long userId;

        @Pattern(regexp = "^[가-힣]{2,19}팀$", message = "한글 2~20자 이내로 작성해주세요.")
        @NotEmpty
        private String teamName;

        @Pattern(regexp = "^(USER|CEO|MANAGER|ADMIN)$", message = "USER, CEO, MANAGER, ADMIN 중 하나의 형태로 요청해주세요.")
        @NotEmpty
        private String role;
    }
}
