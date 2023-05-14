package smash.teams.be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
import smash.teams.be.core.WithMockUser;
import smash.teams.be.core.advice.LogAdvice;
import smash.teams.be.core.advice.ValidAdvice;
import smash.teams.be.core.config.FilterRegisterConfig;
import smash.teams.be.core.config.SecurityConfig;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.dto.user.UserRequest;
import smash.teams.be.dto.user.UserResponse;
import smash.teams.be.model.errorLog.ErrorLogRepository;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.Status;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;
import smash.teams.be.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @WithMockUser(id = 1L, name = "cos", role = "USER", status = "ACTIVE")
    @Test
    public void findMyInfo_test() throws Exception {
        // given
        User cosPS = newMockUser(1L, "cos");
        String prefix = "http://localhost:8080/upload/";

        UserResponse.FindMyInfoOutDTO findMyInfoOutDTO = new UserResponse.FindMyInfoOutDTO(cosPS);
        System.out.println("테스트1 : " + findMyInfoOutDTO);
        Mockito.when(userService.findMyId(any())).thenReturn(findMyInfoOutDTO);

        // when
        ResultActions resultActions = mvc.perform(get("/auth/user/"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트2 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.id").value(1L));
        resultActions.andExpect(jsonPath("$.data.name").value("cos"));
        resultActions.andExpect(jsonPath("$.data.email").value("cos@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.role").value("USER"));
        resultActions.andExpect(status().isOk());
    }

    @WithMockUser(id = 1L, name = "cos", role = "USER", status = "ACTIVE")
    @Test
    public void update_test() throws Exception {
        // given
        Long id = 1L;
        UserRequest.UpdateInDTO updateInDTO = new UserRequest.UpdateInDTO();
        updateInDTO.setCurPassword("dltmdals123!");
        updateInDTO.setNewPassword("dltmdals1234!");
        updateInDTO.setPhoneNumber("010-1234-5678");
        updateInDTO.setStartWork("2023-05-09");

        String requestBody = om.writeValueAsString(updateInDTO);
        System.out.println("테스트1 : " + requestBody);

        // stub
        User ssar = newMockUserUpdate(1L, "cos");
        UserResponse.UpdateOutDTO updateOutDTO = new UserResponse.UpdateOutDTO(ssar);
        Mockito.when(userService.update(any(), any())).thenReturn(updateOutDTO);

        // when
        ResultActions resultActions = mvc.perform(post("/auth/user/" + id + "/upload").
                content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트2 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("join 성공")
    @Test
    public void join_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setName("권으뜸");
        joinInDTO.setPassword("#asfdd1234");
        joinInDTO.setEmail("user7@gmail.com");
        joinInDTO.setPhoneNumber("010-1111-1111");
        joinInDTO.setStartWork("2020-05-01");
        joinInDTO.setTeamName("개발팀");
        String requestBody = om.writeValueAsString(joinInDTO);

        // when
        Team 개발팀 = Team.builder().teamName("개발팀").id(1L)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        User 권으뜸 = User.builder().id(1L).name("권으뜸").email("user7@gmail.com")
                .status(Status.ACTIVE.getStatus()).team(개발팀)
                .startWork(LocalDateTime.now())
                .phoneNumber("010-1111-1111").role(Role.USER.getRole()).remain(20).profileImage(null).build();

        UserResponse.JoinOutDTO joinOutDTO = new UserResponse.JoinOutDTO(권으뜸);
        Mockito.when(userService.join(any())).thenReturn(joinOutDTO);

        // 테스트진행
        ResultActions resultActions = mvc
                .perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // 검증해볼께
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("join 실패")
    @Test
    public void join_fail_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setName("권으뜸");
        joinInDTO.setPassword("#asfdd");
        joinInDTO.setEmail("user7@gmail.com");
        joinInDTO.setPhoneNumber("010-1111-1111");
        joinInDTO.setStartWork("2020-05-01");
        joinInDTO.setTeamName("개발팀");
        String requestBody = om.writeValueAsString(joinInDTO);

        // when
        Team 개발팀 = Team.builder().teamName("개발팀").id(1L)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        User 권으뜸 = User.builder().id(1L).name("권으뜸").email("user7@gmail.com")
                .status(Status.ACTIVE.getStatus()).team(개발팀)
                .startWork(LocalDateTime.now())
                .phoneNumber("010-1111-1111").role(Role.USER.getRole()).remain(20).profileImage(null).build();

        UserResponse.JoinOutDTO joinOutDTO = new UserResponse.JoinOutDTO(권으뜸);
        Mockito.when(userService.join(any())).thenReturn(joinOutDTO);

        // 테스트진행
        ResultActions resultActions = mvc
                .perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // 검증해볼께
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("password"));
        resultActions.andExpect(jsonPath("$.data.value").value("영문, 숫자, 특수문자를 각각 1개 이상 사용하여 8~20자 이내로 작성해주세요."));
    }


}
