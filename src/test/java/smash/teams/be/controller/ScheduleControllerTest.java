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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import smash.teams.be.core.RestDoc;
import smash.teams.be.core.auth.session.MyUserDetails;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.dto.schedule.ScheduleRequest;
import smash.teams.be.model.schedule.ScheduleRepository;
import smash.teams.be.model.schedule.Type;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserQueryRepository;
import smash.teams.be.model.user.UserRepository;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static smash.teams.be.dto.schedule.ScheduleRequest.MakeScheduleRequestInDTO;
import static smash.teams.be.dto.schedule.ScheduleRequest.OrderScheduleInDTO;

@DisplayName("스케줄 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ScheduleControllerTest extends RestDoc {

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
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserQueryRepository userQueryRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        Team 개발팀 = teamRepository.save(dummy.newTeamForIntergratingTest("개발팀"));
        Team 인사팀 = teamRepository.save(dummy.newTeamForIntergratingTest("인사팀"));

        User 김사장1 = userRepository.save(dummy.newUserForIntergratingTest("김사장", 개발팀, "CEO", "ceo1"));
        User 정한울2 = userRepository.save(dummy.newUserForIntergratingTest("정한울", 개발팀, "MANAGER", "manager2"));
        User 최준기3 = userRepository.save(dummy.newUserForIntergratingTest("최준기", 개발팀, "USER", "user3"));
        User 서재식4 = userRepository.save(dummy.newUserForIntergratingTest("서재식", 개발팀, "USER", "user4"));
        User 권으뜸5 = userRepository.save(dummy.newUserForIntergratingTest("권으뜸", 개발팀, "ADMIN", "admin5"));
        User 김유리6 = userRepository.save(dummy.newUserForIntergratingTest("김유리", 개발팀, "USER", "user6"));
        User 남궁훈7 = userRepository.save(dummy.newUserForIntergratingTest("남궁훈", 개발팀, "USER", "user7"));
        User 최민우8 = userRepository.save(dummy.newUserForIntergratingTest("최민우", 인사팀, "MANAGER", "manager8"));
        User 김잔디9 = userRepository.save(dummy.newUserForIntergratingTest("김잔디", 인사팀, "USER", "user9"));
        User 강우람10 = userRepository.save(dummy.newUserForIntergratingTest("강우람", 인사팀, "USER", "user10"));
        User 황민서11 = userRepository.save(dummy.newUserForIntergratingTest("황민서", 인사팀, "USER", "user11"));
        User 황고은12 = userRepository.save(dummy.newUserForIntergratingTest("황고은", 인사팀, "USER", "user12"));

        scheduleRepository.save(dummy.newScheduleForIntergratingTest(김사장1, "DAYOFF", "APPROVED"));
        scheduleRepository.save(dummy.newScheduleForIntergratingTest(황민서11, "DAYOFF", "APPROVED"));
        scheduleRepository.save(dummy.newScheduleForIntergratingTest(최준기3, "DAYOFF", "APPROVED"));
        scheduleRepository.save(dummy.newScheduleForIntergratingTest(정한울2, "DAYOFF", "REJECTED"));
        scheduleRepository.save(dummy.newScheduleForIntergratingTest(남궁훈7, "HALFOFF", "APPROVED"));
        scheduleRepository.save(dummy.newScheduleForIntergratingTest(남궁훈7, "DAYOFF", "REJECTED"));
        scheduleRepository.save(dummy.newScheduleForIntergratingTest(최민우8, "HALFOFF", "LAST"));
        scheduleRepository.save(dummy.newScheduleForIntergratingTest(김잔디9, "DAYOFF", "LAST"));
        scheduleRepository.save(dummy.newScheduleForIntergratingTest(서재식4, "DAYOFF", "LAST"));
        scheduleRepository.save(dummy.newScheduleForIntergratingTest(황고은12, "SHIFT", "FIRST"));
        scheduleRepository.save(dummy.newScheduleForIntergratingTest(남궁훈7, "DAYOFF", "FIRST"));

        em.clear();
    }

    @DisplayName("개인 스케쥴 내역 조회")
    @WithUserDetails(value = "user7@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getScheduleList_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc.perform(get("/auth/user/schedule"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.scheduleList.length()").value(3));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].scheduleId").value(5));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].scheduleId").value(6));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].scheduleId").value(11));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].reason").value("쉬고싶음"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].status").value("REJECTED"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].type").value("DAYOFF"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].user.userId").value(7));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].user.name").value("남궁훈"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].user.teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].user.role").value("USER"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("개인 스케쥴 내역 조회 : unAuthorized")
//    @WithUserDetails(value = "user20@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getScheduleList_fail_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc.perform(get("/auth/user/schedule"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다."));
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }


    @DisplayName("스케쥴 관리 페이지(CEO)")
    @WithUserDetails(value = "ceo1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getScheduleListForManage_ceo_test() throws Exception {
        // when
        ResultActions resultActions = mvc.perform(get("/auth/super/schedule"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.scheduleList.length()").value(11));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].scheduleId").value(1));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].scheduleId").value(2));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].scheduleId").value(3));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].reason").value("쉬고싶음"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].status").value("APPROVED"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].type").value("DAYOFF"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[7].user.userId").value(9L));
        resultActions.andExpect(jsonPath("$.data.scheduleList[8].user.name").value("서재식"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[9].user.teamName").value("인사팀"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[10].user.role").value("USER"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("스케쥴 관리 페이지(manager)")
    @WithUserDetails(value = "manager2@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getScheduleListForManage_manager_test() throws Exception {
        // when
        ResultActions resultActions = mvc.perform(get("/auth/super/schedule"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.scheduleList.length()").value(7));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].scheduleId").value(1));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].scheduleId").value(3));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].scheduleId").value(4));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].reason").value("쉬고싶음"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].status").value("APPROVED"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].type").value("DAYOFF"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[3].user.userId").value(7L));
        resultActions.andExpect(jsonPath("$.data.scheduleList[4].user.name").value("남궁훈"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[5].user.teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[6].user.role").value("USER"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].user.teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].user.teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].user.teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[3].user.teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[4].user.teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[5].user.teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[6].user.teamName").value("개발팀"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("스케쥴 관리 페이지 : 권한 실패")
    @WithUserDetails(value = "user7@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getScheduleListForManage_fail_test() throws Exception {
        // when
        ResultActions resultActions = mvc.perform(get("/auth/super/schedule"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("승인 및 거절하기")
    @WithUserDetails(value = "ceo1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void orderSchedule_test() throws Exception {
        // given
        OrderScheduleInDTO orderScheduleInDTO = new OrderScheduleInDTO();
        orderScheduleInDTO.setScheduleId(7L);
        orderScheduleInDTO.setStatus("APPROVED");
        String requestBody = om.writeValueAsString(orderScheduleInDTO);

        // when
        ResultActions resultActions = mvc.perform(post("/auth/super/schedule").content(requestBody).contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("메인 페이지 조회 성공") // 로그인 O
    @WithUserDetails(value = "ceo1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void loadScheduleList_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc.perform(get("/auth/user/main"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));

        resultActions.andExpect(jsonPath("$.data.scheduleList[0].scheduleId").value(1));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].user.teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].reason").value("쉬고싶음"));

        resultActions.andExpect(jsonPath("$.data.scheduleList[4].scheduleId").value(5));
        resultActions.andExpect(jsonPath("$.data.scheduleList[4].user.name").value("남궁훈"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[4].type").value("HALFOFF"));

        resultActions.andExpect(jsonPath("$.data.scheduleList[9].scheduleId").value(10));
        resultActions.andExpect(jsonPath("$.data.scheduleList[9].user.userId").value(12));
        resultActions.andExpect(jsonPath("$.data.scheduleList[9].type").value("SHIFT"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("메인 페이지 조회 실패") // 로그인 X
    @Test
    public void loadScheduleList_fail_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc.perform(get("/auth/user/main"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(401));
        resultActions.andExpect(jsonPath("$.msg").value("unAuthorized"));
        resultActions.andExpect(jsonPath("$.data").value("인증되지 않았습니다."));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("승인 및 거절하기 : 권한 문제")
    @WithUserDetails(value = "manager2@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void orderSchedule_role_fail1_test() throws Exception {
        // given
        OrderScheduleInDTO orderScheduleInDTO = new OrderScheduleInDTO();
        orderScheduleInDTO.setScheduleId(7L);
        orderScheduleInDTO.setStatus("APPROVED");
        String requestBody = om.writeValueAsString(orderScheduleInDTO);

        // when
        ResultActions resultActions = mvc.perform(post("/auth/super/schedule").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("승인 및 거절하기 : 권한 문제")
    @WithUserDetails(value = "user3@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void orderSchedule_role_fail2_test() throws Exception {
        // given
        OrderScheduleInDTO orderScheduleInDTO = new OrderScheduleInDTO();
        orderScheduleInDTO.setScheduleId(7L);
        orderScheduleInDTO.setStatus("APPROVED");
        String requestBody = om.writeValueAsString(orderScheduleInDTO);

        // when
        ResultActions resultActions = mvc.perform(post("/auth/super/schedule").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다"));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
//        resultActions.andExpect(jsonPath("$.status").value(200));
//        resultActions.andExpect(jsonPath("$.msg").value("ok"));
//
//        resultActions.andExpect(jsonPath("$.data.id").value(5));
//        resultActions.andExpect(jsonPath("$.data.name").value("Ceo"));
//        resultActions.andExpect(jsonPath("$.data.email").value("Ceo@gmail.com"));
//        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("승인 요청 성공")
    @WithUserDetails(value = "user3@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void makeScheduleRequest_test() throws Exception {
        // given
        MakeScheduleRequestInDTO makeScheduleRequestInDTO = new MakeScheduleRequestInDTO();
        makeScheduleRequestInDTO.setStartDate("2023-03-03T09:00:00");
        makeScheduleRequestInDTO.setEndDate("2023-03-03T12:00:00");
        makeScheduleRequestInDTO.setType(Type.HALFOFF.getType());
        makeScheduleRequestInDTO.setReason("병원 예약");
        String requestBody = om.writeValueAsString(makeScheduleRequestInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/user/schedule")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("승인 요청 실패")
    @WithUserDetails(value = "admin5@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void makeScheduleRequest_fail_test() throws Exception {
        // given
        MakeScheduleRequestInDTO makeScheduleRequestInDTO = new MakeScheduleRequestInDTO();
        makeScheduleRequestInDTO.setStartDate("2023-03-03T09:00:00");
        makeScheduleRequestInDTO.setEndDate("2023-03-03T12:00:00");
        makeScheduleRequestInDTO.setType(Type.HALFOFF.getType());
        makeScheduleRequestInDTO.setReason("병원 예약");
        String requestBody = om.writeValueAsString(makeScheduleRequestInDTO);

        // given2
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUser().getId();

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/user/schedule")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value(userId));
        resultActions.andExpect(jsonPath("$.data.value").value("ADMIN 계정으로는 승인 요청이 불가능합니다."));
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
