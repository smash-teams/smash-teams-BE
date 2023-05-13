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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import smash.teams.be.core.RestDoc;
import smash.teams.be.core.auth.jwt.JwtProvider;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.dto.user.UserRequest;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.UserQueryRepository;
import smash.teams.be.model.user.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("사용자 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "52.78.70.225", uriPort = 7777)
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
        userRepository.save(dummy.newUserForIntergratingTest("권으뜸", teamPS, "USER", "user1234")); //8



        em.clear();
    }

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
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }


    @DisplayName("회원가입 실패: 이메일 형식")
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
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("email"));
        resultActions.andExpect(jsonPath("$.data.value").value("50자가 넘지 않도록 이메일 형식에 맞춰 작성해주세요."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원가입 실패: 비밀번호")
    @Test
    public void join_fail_password_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setName("권으뜸");
        joinInDTO.setPassword("sma1234");
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
//        resultActions.andExpect(jsonPath("$.status").value(400));
//        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
//        resultActions.andExpect(jsonPath("$.data.key").value("password"));
//        resultActions.andExpect(jsonPath("$.data.value").value("영문, 숫자, 특수문자를 각각 1개 이상 사용하여 8~20자 이내로 작성해주세요."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("회원가입 실패: 입사일")
    @Test
    public void join_fail_start_work_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setName("권으뜸");
        joinInDTO.setPassword("##smash1234");
        joinInDTO.setEmail("user7777@gmail.com");
        joinInDTO.setPhoneNumber("010-1234-5678");
        joinInDTO.setStartWork("2020-05-01T00:00:00");
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
        resultActions.andExpect(jsonPath("$.data.key").value("startWork"));
        resultActions.andExpect(jsonPath("$.data.value").value("입사일(2023-05-10)의 형태로 작성해주세요."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }


    @DisplayName("이메일 중복화인: 중복된 이메일")
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
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }


    @DisplayName("이메일 중복확인: 중복된 이메일 X")
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
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
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
        Assertions.assertThat(jsonPath("$.data.id").value(1L));
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

        // when
        ResultActions resultActions = mvc.perform(get("/auth/user/"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));

        resultActions.andExpect(jsonPath("$.data.id").value(5));
        resultActions.andExpect(jsonPath("$.data.name").value("Ceo"));
        resultActions.andExpect(jsonPath("$.data.email").value("Ceo@gmail.com"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }


    @DisplayName("내 정보 조회 실패")
    @Test
    public void find_my_info_fail_test() throws Exception {
        // given

        // when

        ResultActions resultActions = mvc.perform(get("/auth/user/"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다."));
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
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
                .perform(RestDocumentationRequestBuilders.post("/auth/user/{id}/upload",id).content(requestBody).contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트2 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.phoneNumber").value("010-8765-4321"));
        resultActions.andExpect(jsonPath("$.data.startWork").value("2023-05-13"));
        resultActions.andExpect(jsonPath("$.data.profileImage").value("User1의 사진!!"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(document.document(pathParameters(parameterWithName("id").description("유저 id"))));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("개인정보 수정 실패: api-path의 id와 로그인한 유저의 id가 다를 때")
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
                .perform(RestDocumentationRequestBuilders.post("/auth/user/{id}/upload",id).content(requestBody).contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트2 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다."));
        resultActions.andExpect(status().isForbidden());
        resultActions.andDo(document.document(pathParameters(parameterWithName("id").description("유저 id"))));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
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




    @DisplayName("회원탈퇴 성공")
    @WithUserDetails(value = "User1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdraw_test() throws Exception {
        // given
        Long id = 1L;
        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setEmail("User1@gmail.com");
        withdrawInDTO.setPassword("dltmdals123!");
        String requestBody = om.writeValueAsString(withdrawInDTO);

        // when
        ResultActions resultActions = mvc.perform(RestDocumentationRequestBuilders.post("/auth/user/{id}/delete",id)
                .content(requestBody).contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(document.document(pathParameters(parameterWithName("id").description("유저 id"))));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }


    @DisplayName("회원탈퇴 실패 : 이메일 틀림")
    @WithUserDetails(value = "User1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdraw_fail_email_test() throws Exception {
        // given
        Long id = 1L;
        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setEmail("user1234567@gmail.com");
        withdrawInDTO.setPassword("dltmdals123!");
        String requestBody = om.writeValueAsString(withdrawInDTO);

        // when
        ResultActions resultActions = mvc.perform(RestDocumentationRequestBuilders.post("/auth/user/{id}/delete",id)
                .content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("email"));
        resultActions.andExpect(jsonPath("$.data.value").value("이메일이 틀렸습니다"));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(document.document(pathParameters(parameterWithName("id").description("유저 id"))));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }


    @DisplayName("회원탈퇴 실패 : 비밀번호 틀림")
    @WithUserDetails(value = "User1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdraw_fail_password_test() throws Exception {
        // given
        Long id = 1L;
        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setEmail("User1@gmail.com");
        withdrawInDTO.setPassword("smash1234");
        String requestBody = om.writeValueAsString(withdrawInDTO);

        // when
        ResultActions resultActions = mvc.perform(RestDocumentationRequestBuilders.post("/auth/user/{id}/delete",id)
                .content(requestBody).contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("password"));
        resultActions.andExpect(jsonPath("$.data.value").value("비밀번호가 틀렸습니다"));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(document.document(pathParameters(parameterWithName("id").description("유저 id"))));
        resultActions.andDo(document.document(requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }


}
