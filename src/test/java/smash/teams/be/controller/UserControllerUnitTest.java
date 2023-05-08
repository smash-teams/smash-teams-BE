package smash.teams.be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import smash.teams.be.core.MyWithMockUser;
import smash.teams.be.core.advice.LogAdvice;
import smash.teams.be.core.advice.ValidAdvice;
import smash.teams.be.core.config.FilterRegisterConfig;
import smash.teams.be.core.config.SecurityConfig;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.dto.schedule.ScheduleResponse;
import smash.teams.be.dto.user.UserResponse;
import smash.teams.be.model.errorLog.ErrorLogRepository;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;
import smash.teams.be.service.AdminService;
import smash.teams.be.service.UserService;

import javax.persistence.EntityManager;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        controllers = {UserController.class}
)
public class UserControllerUnitTest extends DummyEntity {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ErrorLogRepository errorLogRepository;

    @MyWithMockUser(id = 1L, name = "cos", role = "USER", status = "ACTIVE")
    @Test
    public void findMyInfo_test() throws Exception {
        // given
        Long id = 1L;
        User cosPS = newMockUser(1L, "cos");

        UserResponse.findMyInfoOutDTO findMyInfoOutDTO = new UserResponse.findMyInfoOutDTO(cosPS);
        System.out.println("테스트1 : " + findMyInfoOutDTO);
        Mockito.when(userService.findMyId(any())).thenReturn(findMyInfoOutDTO);

        // when
        ResultActions resultActions = mvc
                .perform(get("/auth/user/"+id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트2 : " + responseBody);

        // then
//        resultActions.andExpect(jsonPath("$.data.id").value(1L));
//        resultActions.andExpect(jsonPath("$.data.username").value("ssar"));
//        resultActions.andExpect(jsonPath("$.data.email").value("ssar@nate.com"));
//        resultActions.andExpect(jsonPath("$.data.fullName").value("쌀"));
//        resultActions.andExpect(jsonPath("$.data.role").value("USER"));
//        resultActions.andExpect(status().isOk());
    }
}
