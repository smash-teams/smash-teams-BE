package smash.teams.be.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.dto.team.TeamRequest;
import smash.teams.be.dto.team.TeamResponse;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TeamServiceTest extends DummyEntity {

    @InjectMocks
    private TeamService teamService;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    public void add_test() {
        // given
        TeamRequest.AddInDTO addInDTO = new TeamRequest.AddInDTO();
        addInDTO.setTeamName("회계팀");

        // stub 1
        Mockito.when(teamRepository.findByTeamName(any())).thenReturn(Optional.empty());

        // stub 2
        Mockito.when(teamRepository.save(any())).thenReturn(newMockTeam(4L, "회계팀"));

        // when
        TeamResponse.AddOutDTO addOutDTO = teamService.add(addInDTO);

        // then
        Assertions.assertThat(addOutDTO.getTeamId()).isEqualTo(4L);
        Assertions.assertThat(addOutDTO.getTeamName()).isEqualTo("회계팀");
        Assertions.assertThat(addOutDTO.getTeamCount()).isEqualTo(0);
    }
}