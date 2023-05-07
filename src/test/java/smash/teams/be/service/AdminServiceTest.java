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
import smash.teams.be.dto.admin.AdminRequest;
import smash.teams.be.dto.admin.AdminResponse;
import smash.teams.be.model.errorLog.ErrorLogRepository;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AdminServiceTest extends DummyEntity {

    @InjectMocks
    private AdminService adminService;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ErrorLogRepository errorLogRepository;

    @Test
    public void add_test() {
        // given
        AdminRequest.AddInDTO addInDTO = new AdminRequest.AddInDTO();
        addInDTO.setTeamName("회계팀");

        // stub 1
        Mockito.when(teamRepository.findByTeamName(any())).thenReturn(Optional.empty());

        // stub 2
        Mockito.when(teamRepository.save(any())).thenReturn(newMockTeam(4L, "회계팀"));

        // when
        AdminResponse.AddOutDTO addOutDTO = adminService.add(addInDTO);

        // then
        Assertions.assertThat(addOutDTO.getTeamId()).isEqualTo(4L);
        Assertions.assertThat(addOutDTO.getTeamName()).isEqualTo("회계팀");
        Assertions.assertThat(addOutDTO.getTeamCount()).isEqualTo(0);
    }
}
