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
import smash.teams.be.service.AdminService;

import static org.mockito.ArgumentMatchers.any;
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
        controllers = {AdminContoller.class}
)
public class AdminContollerUnitTest extends DummyEntity {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private AdminService adminService;

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
                .perform(post("/auth/admin/team/add").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.teamId").value(3L));
        resultActions.andExpect(jsonPath("$.data.teamName").value("마케팅팀"));
        resultActions.andExpect(jsonPath("$.data.teamCount").value(0));
        resultActions.andExpect(status().isOk());
    }
}
