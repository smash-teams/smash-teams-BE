package smash.teams.be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import smash.teams.be.core.WithMockAdmin;
import smash.teams.be.core.advice.LogAdvice;
import smash.teams.be.core.advice.ValidAdvice;
import smash.teams.be.core.config.FilterRegisterConfig;
import smash.teams.be.core.config.SecurityConfig;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.dto.admin.AdminRequest;
import smash.teams.be.dto.admin.AdminResponse;
import smash.teams.be.model.errorLog.ErrorLogRepository;
import smash.teams.be.model.user.Role;
import smash.teams.be.service.AdminService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @WebMvcTest는 웹 계층 컴포넌트만 테스트로 가져옴
 */

@ActiveProfiles("test")
@EnableAspectJAutoProxy // AOP 활성화
@Import({
        ValidAdvice.class,
        LogAdvice.class,
        SecurityConfig.class,
        FilterRegisterConfig.class
}) // Advice 와 Security 설정 가져오기
@WebMvcTest(
        // 필요한 Controller 가져오기, 특정 필터를 제외하기
        controllers = {AdminController.class}
)
public class AdminControllerUnitTest extends DummyEntity {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private AdminService adminService;
    @MockBean
    private ErrorLogRepository errorLogRepository;


    @WithMockAdmin
    @Test
    public void add_test() throws Exception {
        // given
        AdminRequest.AddInDTO addInDTO = new AdminRequest.AddInDTO();
        addInDTO.setTeamName("마케팅팀");
        String requestBody = om.writeValueAsString(addInDTO);

        // stub
        Mockito.when(adminService.add(any()))
                .thenReturn(new AdminResponse.AddOutDTO(newMockTeam(3L, "마케팅팀")));

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/admin/teams").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.teamId").value(3L));
        resultActions.andExpect(jsonPath("$.data.teamName").value("마케팅팀"));
        resultActions.andExpect(jsonPath("$.data.teamCount").value(0));
        resultActions.andExpect(status().isOk());
    }

    @WithMockAdmin
    @Test
    public void getAdminPage_test() throws Exception {
        // given
        String teamName = "개발팀";
        String keyword = "이";
        String page = "0";

        // stub
        Mockito.when(adminService.getAdminPage(any(), any(), anyInt()))
                .thenReturn(new AdminResponse.GetAdminPageOutDTO(
                        newMockTeamList(),
                        newMockUserListByDTO(teamName),
                        12,
                        3L,
                        1,
                        0,
                        true,
                        false,
                        false
                ));

        // when
        ResultActions resultActions = mvc
                .perform(get("/auth/admin")
                        .param("teamName", teamName)
                        .param("keyword", keyword)
                        .param("page", page));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.teamList[0].teamId").value(1L));
        resultActions.andExpect(jsonPath("$.data.teamList[0].teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.teamList[0].teamCount").value(3));
        resultActions.andExpect(jsonPath("$.data.teamList[1].teamId").value(2L));
        resultActions.andExpect(jsonPath("$.data.teamList[1].teamName").value("회계팀"));
        resultActions.andExpect(jsonPath("$.data.teamList[1].teamCount").value(1));
        resultActions.andExpect(jsonPath("$.data.teamList[2].teamId").value(3L));
        resultActions.andExpect(jsonPath("$.data.teamList[2].teamName").value("마케팅팀"));
        resultActions.andExpect(jsonPath("$.data.teamList[2].teamCount").value(1));
        resultActions.andExpect(jsonPath("$.data.userList[0].userId").value(1L));
        resultActions.andExpect(jsonPath("$.data.userList[0].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[0].name").value("이승민"));
        resultActions.andExpect(jsonPath("$.data.userList[0].email").value("이승민@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[0].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[0].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[0].teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.userList[0].role").value(Role.USER.getRole()));
        resultActions.andExpect(jsonPath("$.data.userList[1].userId").value(2L));
        resultActions.andExpect(jsonPath("$.data.userList[1].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[1].name").value("이윤경"));
        resultActions.andExpect(jsonPath("$.data.userList[1].email").value("이윤경@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[1].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[1].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[1].teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.userList[1].role").value(Role.USER.getRole()));
        resultActions.andExpect(jsonPath("$.data.userList[2].userId").value(3L));
        resultActions.andExpect(jsonPath("$.data.userList[2].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[2].name").value("이한울"));
        resultActions.andExpect(jsonPath("$.data.userList[2].email").value("이한울@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[2].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[2].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[2].teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.userList[2].role").value(Role.USER.getRole()));
        resultActions.andExpect(jsonPath("$.data.size").value(12));
        resultActions.andExpect(jsonPath("$.data.totalElements").value(3L));
        resultActions.andExpect(jsonPath("$.data.totalPages").value(1));
        resultActions.andExpect(jsonPath("$.data.curPage").value(0));
        resultActions.andExpect(jsonPath("$.data.first").value(true));
        resultActions.andExpect(jsonPath("$.data.last").value(false));
        resultActions.andExpect(jsonPath("$.data.empty").value(false));
        resultActions.andExpect(status().isOk());
    }
}
