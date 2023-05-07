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
import smash.teams.be.model.team.Team;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.UserQueryRepository;
import smash.teams.be.model.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

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
    private UserQueryRepository userQueryRepository;

    @Test
    public void add_test() {
        // given
        AdminRequest.AddInDTO addInDTO = new AdminRequest.AddInDTO();
        addInDTO.setTeamName("회계팀");

        // stub
        Mockito.when(teamRepository.findByTeamName(any())).thenReturn(Optional.empty());
        Mockito.when(teamRepository.save(any())).thenReturn(newMockTeam(4L, "회계팀"));

        // when
        AdminResponse.AddOutDTO addOutDTO = adminService.add(addInDTO);

        // then
        Assertions.assertThat(addOutDTO.getTeamId()).isEqualTo(4L);
        Assertions.assertThat(addOutDTO.getTeamName()).isEqualTo("회계팀");
        Assertions.assertThat(addOutDTO.getTeamCount()).isEqualTo(0);
    }

    @Test
    public void getAdminPage_test() {
        // given1
        String teamName = "개발팀";
        String keyword = "이";
        String page = "0";

        // stub
        Team team = newMockTeam(1L, teamName);
        Mockito.when(teamRepository.findByTeamName(any()))
                .thenReturn(Optional.of(team));
        Mockito.when(userQueryRepository.findAllByKeywordAndTeamId(anyLong(), any(), anyInt()))
                .thenReturn(newMockUserPage(teamName));
        Mockito.when(teamRepository.findAll()).thenReturn(
                new ArrayList<>() {{
                    add(newMockTeam(1L, "개발팀"));
                    add(newMockTeam(2L, "회계팀"));
                    add(newMockTeam(3L, "마케팅팀"));
                }}
        );
        Mockito.when(userRepository.calculateCountByTeamId(anyLong())).thenReturn(3);

        // when
        AdminResponse.GetAdminPageOutDTO getAdminPageOutDTO = adminService.getAdminPage(teamName, keyword, Integer.parseInt(page));

        // then
        Assertions.assertThat(getAdminPageOutDTO.getTeamList().get(0).getTeamId()).isEqualTo(1L);
        Assertions.assertThat(getAdminPageOutDTO.getTeamList().get(0).getTeamName()).isEqualTo("개발팀");
        Assertions.assertThat(getAdminPageOutDTO.getTeamList().get(0).getTeamCount()).isEqualTo(3);
        Assertions.assertThat(getAdminPageOutDTO.getTeamList().get(1).getTeamId()).isEqualTo(2L);
        Assertions.assertThat(getAdminPageOutDTO.getTeamList().get(1).getTeamName()).isEqualTo("회계팀");
        Assertions.assertThat(getAdminPageOutDTO.getTeamList().get(1).getTeamCount()).isEqualTo(3);
        Assertions.assertThat(getAdminPageOutDTO.getTeamList().get(2).getTeamId()).isEqualTo(3L);
        Assertions.assertThat(getAdminPageOutDTO.getTeamList().get(2).getTeamName()).isEqualTo("마케팅팀");
        Assertions.assertThat(getAdminPageOutDTO.getTeamList().get(2).getTeamCount()).isEqualTo(3);
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(0).getUserId()).isEqualTo(1L);
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(0).getProfileImage()).isEqualTo(null);
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(0).getName()).isEqualTo("이승민");
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(0).getEmail()).isEqualTo("이승민@gmail.com");
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(0).getPhoneNumber()).isEqualTo("010-1234-5678");
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(0).getStartWork()).isEqualTo(LocalDateTime.now().toLocalDate().toString());
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(0).getRole()).isEqualTo(Role.USER.getRole());
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(1).getUserId()).isEqualTo(2L);
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(1).getProfileImage()).isEqualTo(null);
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(1).getName()).isEqualTo("이윤경");
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(1).getEmail()).isEqualTo("이윤경@gmail.com");
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(1).getPhoneNumber()).isEqualTo("010-1234-5678");
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(1).getStartWork()).isEqualTo(LocalDateTime.now().toLocalDate().toString());
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(1).getRole()).isEqualTo(Role.USER.getRole());
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(2).getUserId()).isEqualTo(3L);
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(2).getProfileImage()).isEqualTo(null);
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(2).getName()).isEqualTo("이한울");
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(2).getEmail()).isEqualTo("이한울@gmail.com");
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(2).getPhoneNumber()).isEqualTo("010-1234-5678");
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(2).getStartWork()).isEqualTo(LocalDateTime.now().toLocalDate().toString());
        Assertions.assertThat(getAdminPageOutDTO.getUserList().get(2).getRole()).isEqualTo(Role.USER.getRole());
        Assertions.assertThat(getAdminPageOutDTO.getSize()).isEqualTo(12);
        Assertions.assertThat(getAdminPageOutDTO.getTotalElements()).isEqualTo(3L);
        Assertions.assertThat(getAdminPageOutDTO.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(getAdminPageOutDTO.getCurPage()).isEqualTo(0);
        Assertions.assertThat(getAdminPageOutDTO.getFirst()).isEqualTo(true);
        Assertions.assertThat(getAdminPageOutDTO.getLast()).isEqualTo(true);
        Assertions.assertThat(getAdminPageOutDTO.getEmpty()).isEqualTo(false);
    }

}
