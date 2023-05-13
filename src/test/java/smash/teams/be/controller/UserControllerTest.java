package smash.teams.be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.mock.web.MockMultipartFile;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.multipart.MultipartFile;
import smash.teams.be.core.RestDoc;

import smash.teams.be.core.auth.jwt.JwtProvider;

import smash.teams.be.core.auth.session.MyUserDetails;

import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.dto.user.UserRequest;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserQueryRepository;
import smash.teams.be.model.user.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("사용자 API")
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
    private TeamRepository teamRepository;

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {


        Team teamPS = teamRepository.save(dummy.newTeam("개발팀"));
        Team teamPS2 = teamRepository.save(dummy.newTeam("회계팀"));
        Team teamPS3 = teamRepository.save(dummy.newTeam("마케팅팀"));
        Team teamPS4 = teamRepository.save(dummy.newTeam("기획팀"));
        Team teamPS5 = teamRepository.save(dummy.newTeam("admin"));

        userRepository.save(dummy.newUserWithTeam("User1", teamPS)); // 1
        userRepository.save(dummy.newUserWithTeam("User2", teamPS)); // 2
        userRepository.save(dummy.newAdminWithTeam("Admin1", teamPS5)); // 3
        userRepository.save(dummy.newAdminWithTeam("Admin2", teamPS5)); // 4
        userRepository.save(dummy.newCeoWithTeam("Ceo", teamPS5)); // 5
        userRepository.save(dummy.newManagerWithTeam("Manager1", teamPS5)); // 6
        userRepository.save(dummy.newManagerWithTeam("Manager2", teamPS5)); // 7
        userRepository.save(dummy.newUserForIntergratingTest("권으뜸",teamPS,"USER","user1234")); //8


        em.clear();
    }

    @DisplayName("로그인 성공")
    @Test
    public void login_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail("User1@gmail.com");
        loginInDTO.setPassword("dltmdals123!");
        String requestBody = om.writeValueAsString(loginInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        Assertions.assertThat(jsonPath("$.data.name").value("User1"));
        Assertions.assertThat(jsonPath("$.data.email").value("User1@gmail.com"));
        Assertions.assertThat(jsonPath("$.data.phoneNumber").value("010-1234-5678"));
        Assertions.assertThat(jsonPath("$.data.profileImage").isEmpty());
        Assertions.assertThat(jsonPath("$.data.startWork").value(LocalDateTime.now().toLocalDate().toString()));
        Assertions.assertThat(jsonPath("$.data.remain").value(20));
        Assertions.assertThat(jsonPath("$.data.teamName").value("개발팀"));
        Assertions.assertThat(jsonPath("$.data.role").value("USER"));
        String jwt = resultActions.andReturn().getResponse().getHeader(JwtProvider.HEADER);
        Assertions.assertThat(jwt.startsWith(JwtProvider.TOKEN_PREFIX)).isTrue();
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("로그인 실패")
    @Test
    public void login_fail_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail("User1@gmail.com");
        loginInDTO.setPassword("dltmdals1!");
        String requestBody = om.writeValueAsString(loginInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        Assertions.assertThat(jsonPath("$.data").value("인증되지 않았습니다."));
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("내 정보 조회 성공")
    @WithUserDetails(value = "Ceo@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void find_my_info_test() throws Exception {
        // given
        Long id = 5L;

        // when
        ResultActions resultActions = mvc.perform(get("/auth/user/" + id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));

        resultActions.andExpect(jsonPath("$.data.id").value(5));
        resultActions.andExpect(jsonPath("$.data.name").value("Ceo"));
        resultActions.andExpect(jsonPath("$.data.email").value("Ceo@gmail.com"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("내 정보 조회 실패")
    @WithUserDetails(value = "Ceo@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void find_my_info_fail_test() throws Exception {
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
        UserRequest.UpdateInDTO updateInDTO = new UserRequest.UpdateInDTO();
        updateInDTO.setCurPassword("dltmdals123!");
        updateInDTO.setNewPassword("dltmdals1234!");
        updateInDTO.setPhoneNumber("010-8765-4321");
        updateInDTO.setStartWork("2023-05-13");

        String requestBody = om.writeValueAsString(updateInDTO);
        System.out.println("테스트1 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/user/" + id + "/upload").content(requestBody).contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트2 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.phoneNumber").value("010-8765-4321"));
        resultActions.andExpect(jsonPath("$.data.startWork").value("2023-05-13"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("개인정보 수정 실패")
    @WithUserDetails(value = "User1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_fail_test() throws Exception {
        // given
        Long id = 2L;
        UserRequest.UpdateInDTO updateInDTO = new UserRequest.UpdateInDTO();
        updateInDTO.setCurPassword("dltmdals123!");
        updateInDTO.setNewPassword("dltmdals1234!");
        updateInDTO.setPhoneNumber("010-8765-4321");
        updateInDTO.setStartWork("2023-05-13");

        String requestBody = om.writeValueAsString(updateInDTO);
        System.out.println("테스트1 : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/user/" + id + "/upload").content(requestBody).contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트2 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다."));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }


//    @DisplayName("이미지 업로드 성공")
//    @WithUserDetails(value = "User1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void uploadImage_test() throws Exception {
//        // given
//        Long id = 1L;
//        MockMultipartFile multipartFile = new MockMultipartFile("profileImage",
//                "person.png", "multipart/form-data", "Hello, World!".getBytes());
////        String requestBody = om.writeValueAsString(multipartFile);
////        System.out.println("테스트1 : " + requestBody);
//
//        // when
//        ResultActions resultActions = mvc.perform(
//                post("/auth/user/" + id + "/image")
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//        );
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트2 : " + responseBody);
//
//        // then
//        resultActions.andExpect(jsonPath("$.status").value(200));
//        resultActions.andExpect(jsonPath("$.msg").value("OK"));
//        resultActions.andExpect(jsonPath("$.data").value("null"));
//        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
//    }
//
//    @DisplayName("이미지 업로드 실패") //
//    @WithUserDetails(value = "User1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void uploadImage_fail_test() throws Exception {
//        // given
//        Long id = 1L;
//        MockMultipartFile multipartFile = new MockMultipartFile("profileImage",
//                "person.png", "multipart/form-data", "Hello, World!".getBytes());
//
//        // when
//        ResultActions resultActions = mvc.perform(
//                MockMvcRequestBuilders.multipart("/auth/user/" + id + "/image")
//                        .file(multipartFile)
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//        );
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("테스트2 : " + responseBody);
//
//        // then
//        resultActions.andExpect(jsonPath("$.status").value(200));
//        resultActions.andExpect(jsonPath("$.msg").value("ok"));
//        resultActions.andExpect(jsonPath("$.data").value(null));
//        //resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
//    }

    @DisplayName("회원가입 성공")
    @Test
    public void join_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setName("권민수");
        joinInDTO.setPassword("##234dkfid");
        joinInDTO.setEmail("user7777@gmail.com");
        joinInDTO.setPhoneNumber("010-1111-1111");
        joinInDTO.setStartWork("2020-05-01");
        joinInDTO.setTeamName("개발팀");
        String requestBody = om.writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
//        resultActions.andExpect(jsonPath("$.status").value(200));
//        resultActions.andExpect(jsonPath("$.msg").value("성공"));
//        resultActions.andExpect(jsonPath("$.data").value(null));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }


    @DisplayName("회원가입 실패 : 이름이 이미 존재할 때")
    @Test
    public void join_fail_name_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setName("권으뜸");
        joinInDTO.setPassword("##smash1234");
        joinInDTO.setEmail("user7777@gmail.com");
        joinInDTO.setPhoneNumber("010-1111-1111");
        joinInDTO.setStartWork("2020-05-01");
        joinInDTO.setTeamName("개발팀");
        String requestBody = om.writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("name"));
        resultActions.andExpect(jsonPath("$.data.value").value("이름이 존재합니다"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원가입 실패 : 이메일 형식을 지키지 않았을 때")
    @Test
    public void join_fail_email_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setName("권으뜸");
        joinInDTO.setPassword("##smash1234");
        joinInDTO.setEmail("user7777@gmail.c");
        joinInDTO.setPhoneNumber("010-1111-1111");
        joinInDTO.setStartWork("2020-05-01");
        joinInDTO.setTeamName("개발팀");
        String requestBody = om.writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
//        resultActions.andExpect(jsonPath("$.status").value(200));
//        resultActions.andExpect(jsonPath("$.msg").value("성공"));
//        resultActions.andExpect(jsonPath("$.data").value(null));
//        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("email"));
        resultActions.andExpect(jsonPath("$.data.value").value("50자가 넘지 않도록 이메일 형식에 맞춰 작성해주세요."));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("이메일 중복확인 : 중복된 이메일이 아닙니다")
    @Test
    public void check_duplicate_email_false_test() throws Exception{
        // given
        UserRequest.CheckInDTO checkInDTO = new UserRequest.CheckInDTO();
        checkInDTO.setEmail("user7777777@gmail.com");
        String requestBody = om.writeValueAsString(checkInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/join/check").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").value(false));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @DisplayName("이메일 중복화인 : 중복된 이메일입니다.")
    @Test
    public void check_duplicate_email_true_test() throws Exception{
        // given
        UserRequest.CheckInDTO checkInDTO = new UserRequest.CheckInDTO();
        checkInDTO.setEmail("user1234@gmail.com");
        String requestBody = om.writeValueAsString(checkInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/join/check").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").value(true));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }


    @DisplayName("회원탈퇴 성공")
    @WithUserDetails(value = "user1234@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdraw_test() throws Exception{
        // given
        Long id = 8L;
        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setEmail("user1234@gmail.com");
        withdrawInDTO.setPassword("##smash1234");
        String requestBody = om.writeValueAsString(withdrawInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/user/"+id+"/delete").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }


    @DisplayName("회원탈퇴 실패 : 이메일 틀림")
    @WithUserDetails(value = "user1234@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdraw_fail_email_test() throws Exception{
        // given
        Long id = 8L;
        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setEmail("user1234567@gmail.com");
        withdrawInDTO.setPassword("##smash1234");
        String requestBody = om.writeValueAsString(withdrawInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/user/"+id+"/delete").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("email"));
        resultActions.andExpect(jsonPath("$.data.value").value("이메일이 틀렸습니다"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }


    @DisplayName("회원탈퇴 실패 : 비밀번호 틀림")
    @WithUserDetails(value = "user1234@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdraw_fail_password_test() throws Exception{
        // given
        Long id = 8L;
        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setEmail("user1234@gmail.com");
        withdrawInDTO.setPassword("smash1234");
        String requestBody = om.writeValueAsString(withdrawInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/user/"+id+"/delete").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("password"));
        resultActions.andExpect(jsonPath("$.data.value").value("비밀번호가 틀렸습니다"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }


}
