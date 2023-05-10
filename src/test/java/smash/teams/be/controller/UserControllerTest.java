package smash.teams.be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import smash.teams.be.core.RestDoc;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.dto.user.UserRequest;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.UserQueryRepository;
import smash.teams.be.model.user.UserRepository;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DisplayName("회원 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserControllerTest extends RestDoc {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserQueryRepository userQueryRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        userRepository.save(dummy.newUser("User1")); // 1
        userRepository.save(dummy.newUser("User2")); // 2
        userRepository.save(dummy.newAdmin("Admin1")); // 3
        userRepository.save(dummy.newAdmin("Admin2")); // 4
        userRepository.save(dummy.newCeo("Ceo")); // 5
        userRepository.save(dummy.newManager("Manager1")); // 6
        userRepository.save(dummy.newManager("Manager2")); // 7

        em.clear();
    }

    @DisplayName("내 정보 조회 성공")
    @WithUserDetails(value = "Ceo@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void findMyInfo_test() throws Exception {
        // given
        Long id = 5L;

        // when
        ResultActions resultActions = mvc.perform(get("/auth/user/" + id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));

        resultActions.andExpect(jsonPath("$.data.id").value(5));
        resultActions.andExpect(jsonPath("$.data.name").value("Ceo"));
        resultActions.andExpect(jsonPath("$.data.email").value("Ceo@gmail.com"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("내 정보 조회 실패")
    @WithUserDetails(value = "Ceo@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void findMyInfo_fail_test() throws Exception {
        // given
        Long id = 2L;

        // when
        ResultActions resultActions = mvc.perform(get("/auth/user/" + id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다."));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("개인정보 수정 성공")
    @WithUserDetails(value = "User1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_test() throws Exception {
        // given
        Long id = 1L;
        UserRequest.UpdateInDto updateInDto = new UserRequest.UpdateInDto();
        updateInDto.setCurPassword("1234");
        updateInDto.setNewPassword("5678");
        updateInDto.setPhoneNumber("010-8765-4321");
        updateInDto.setStartWork("2023-05-13");
        updateInDto.setProfileImage("User1의 사진!!");

        String requestBody = om.writeValueAsString(updateInDto);
        System.out.println("테스트1 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/user/"+ id + "/upload").content(requestBody).contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트2 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("성공"));
        resultActions.andExpect(jsonPath("$.data.phoneNumber").value("010-8765-4321"));
        resultActions.andExpect(jsonPath("$.data.startWork").value("2023-05-13T00:00:00"));
        resultActions.andExpect(jsonPath("$.data.profileImage").value("User1의 사진!!"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("개인정보 수정 실패")
    @WithUserDetails(value = "User1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_fail_test() throws Exception {
        // given
        Long id = 2L;
        UserRequest.UpdateInDto updateInDto = new UserRequest.UpdateInDto();
        updateInDto.setCurPassword("1234");
        updateInDto.setNewPassword("5678");
        updateInDto.setPhoneNumber("010-8765-4321");
        updateInDto.setStartWork("2023-05-13");
        updateInDto.setProfileImage("User1의 사진!!");

        String requestBody = om.writeValueAsString(updateInDto);
        System.out.println("테스트1 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/user/"+ id + "/upload").content(requestBody).contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트2 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다."));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
