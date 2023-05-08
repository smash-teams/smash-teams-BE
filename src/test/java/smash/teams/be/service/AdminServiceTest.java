package smash.teams.be.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.UserQueryRepository;
import smash.teams.be.model.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static smash.teams.be.dto.admin.AdminRequest.AddInDTO;
import static smash.teams.be.dto.admin.AdminResponse.AddOutDTO;
import static smash.teams.be.dto.admin.AdminResponse.GetAdminPageOutDTO;

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
    public void getAdminPage_test() {
        // given
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
        GetAdminPageOutDTO getAdminPageOutDTO = adminService.getAdminPage(teamName, keyword, Integer.parseInt(page));

        // then
        assertThat(getAdminPageOutDTO.getTeamList().get(0).getTeamId()).isEqualTo(1L);
        assertThat(getAdminPageOutDTO.getTeamList().get(0).getTeamName()).isEqualTo("개발팀");
        assertThat(getAdminPageOutDTO.getTeamList().get(0).getTeamCount()).isEqualTo(3);
        assertThat(getAdminPageOutDTO.getTeamList().get(1).getTeamId()).isEqualTo(2L);
        assertThat(getAdminPageOutDTO.getTeamList().get(1).getTeamName()).isEqualTo("회계팀");
        assertThat(getAdminPageOutDTO.getTeamList().get(1).getTeamCount()).isEqualTo(3);
        assertThat(getAdminPageOutDTO.getTeamList().get(2).getTeamId()).isEqualTo(3L);
        assertThat(getAdminPageOutDTO.getTeamList().get(2).getTeamName()).isEqualTo("마케팅팀");
        assertThat(getAdminPageOutDTO.getTeamList().get(2).getTeamCount()).isEqualTo(3);
        assertThat(getAdminPageOutDTO.getUserList().get(0).getUserId()).isEqualTo(1L);
        assertThat(getAdminPageOutDTO.getUserList().get(0).getProfileImage()).isEqualTo(null);
        assertThat(getAdminPageOutDTO.getUserList().get(0).getName()).isEqualTo("이승민");
        assertThat(getAdminPageOutDTO.getUserList().get(0).getEmail()).isEqualTo("이승민@gmail.com");
        assertThat(getAdminPageOutDTO.getUserList().get(0).getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(getAdminPageOutDTO.getUserList().get(0).getStartWork()).isEqualTo(LocalDateTime.now().toLocalDate().toString());
        assertThat(getAdminPageOutDTO.getUserList().get(0).getRole()).isEqualTo(Role.USER.getRole());
        assertThat(getAdminPageOutDTO.getUserList().get(1).getUserId()).isEqualTo(2L);
        assertThat(getAdminPageOutDTO.getUserList().get(1).getProfileImage()).isEqualTo(null);
        assertThat(getAdminPageOutDTO.getUserList().get(1).getName()).isEqualTo("이윤경");
        assertThat(getAdminPageOutDTO.getUserList().get(1).getEmail()).isEqualTo("이윤경@gmail.com");
        assertThat(getAdminPageOutDTO.getUserList().get(1).getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(getAdminPageOutDTO.getUserList().get(1).getStartWork()).isEqualTo(LocalDateTime.now().toLocalDate().toString());
        assertThat(getAdminPageOutDTO.getUserList().get(1).getRole()).isEqualTo(Role.USER.getRole());
        assertThat(getAdminPageOutDTO.getUserList().get(2).getUserId()).isEqualTo(3L);
        assertThat(getAdminPageOutDTO.getUserList().get(2).getProfileImage()).isEqualTo(null);
        assertThat(getAdminPageOutDTO.getUserList().get(2).getName()).isEqualTo("이한울");
        assertThat(getAdminPageOutDTO.getUserList().get(2).getEmail()).isEqualTo("이한울@gmail.com");
        assertThat(getAdminPageOutDTO.getUserList().get(2).getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(getAdminPageOutDTO.getUserList().get(2).getStartWork()).isEqualTo(LocalDateTime.now().toLocalDate().toString());
        assertThat(getAdminPageOutDTO.getUserList().get(2).getRole()).isEqualTo(Role.USER.getRole());
        assertThat(getAdminPageOutDTO.getSize()).isEqualTo(12);
        assertThat(getAdminPageOutDTO.getTotalElements()).isEqualTo(3L);
        assertThat(getAdminPageOutDTO.getTotalPages()).isEqualTo(1);
        assertThat(getAdminPageOutDTO.getCurPage()).isEqualTo(0);
        assertThat(getAdminPageOutDTO.getFirst()).isEqualTo(true);
        assertThat(getAdminPageOutDTO.getLast()).isEqualTo(true);
        assertThat(getAdminPageOutDTO.getEmpty()).isEqualTo(false);
    }

//    @Test
//    public void updateAuthAndTeam_test() {
//        // given
//        Long id = 1L;
//
//        // stub
//        Team team = newMockTeam(1L, "개발팀");
//        User user = newMockUserWithTeam(1L, "이승민", team);
//
//        Team team2 = newMockTeam(2L, "회계팀");
//        UpdateAuthAndTeamInDTO updateAuthAndTeamInDTO = new UpdateAuthAndTeamInDTO();
//        updateAuthAndTeamInDTO.setUserId(id);
//        updateAuthAndTeamInDTO.setTeamName(team2.getTeamName());
//        updateAuthAndTeamInDTO.setRole(Role.MANAGER.getRole());
//        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
//        Mockito.when(teamRepository.findByTeamName(any())).thenReturn(Optional.of(team2));
//
//        // when
//        TestUpdateAuthAndTeamOutDTO testUpdateAuthAndTeamOutDTO = adminService.updateAuthAndTeam(updateAuthAndTeamInDTO);
//
//        // then
//        assertThat(testUpdateAuthAndTeamOutDTO.getUser().getTeam().getTeamName()).isEqualTo(team2.getTeamName());
//        assertThat(testUpdateAuthAndTeamOutDTO.getUser().getRole()).isEqualTo(Role.MANAGER.getRole());
//    }

    @Test
    public void add_test() {
        // given
        AddInDTO addInDTO = new AddInDTO();
        addInDTO.setTeamName("회계팀");

        // stub
        Mockito.when(teamRepository.findByTeamName(any())).thenReturn(Optional.empty());
        Mockito.when(teamRepository.save(any())).thenReturn(newMockTeam(4L, "회계팀"));

        // when
        AddOutDTO addOutDTO = adminService.add(addInDTO);

        // then
        assertThat(addOutDTO.getTeamId()).isEqualTo(4L);
        assertThat(addOutDTO.getTeamName()).isEqualTo("회계팀");
        assertThat(addOutDTO.getTeamCount()).isEqualTo(0);
    }
}
