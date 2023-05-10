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
import smash.teams.be.dto.admin.AdminRequest;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.UserQueryRepository;
import smash.teams.be.model.user.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static smash.teams.be.dto.admin.AdminRequest.UpdateAuthAndTeamInDTO;

@DisplayName("관리자 권한 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AdminControllerTest extends RestDoc {

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
    private TeamRepository teamRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        Team teamPS = teamRepository.save(dummy.newTeam("개발팀"));
        Team teamPS2 = teamRepository.save(dummy.newTeam("회계팀"));
        Team teamPS3 = teamRepository.save(dummy.newTeam("마케팅팀"));
        Team teamPS4 = teamRepository.save(dummy.newTeam("기획팀"));

        userRepository.save(dummy.newAdmin("admin"));
        userRepository.save(dummy.newManagerWithTeam("이승민", teamPS));
        userRepository.save(dummy.newUserWithTeam("이윤경", teamPS));
        userRepository.save(dummy.newUserWithTeam("이한울", teamPS));
        userRepository.save(dummy.newUserWithTeam("이빛나", teamPS));
        userRepository.save(dummy.newUserWithTeam("이산", teamPS));
        userRepository.save(dummy.newUserWithTeam("이인호", teamPS));
        userRepository.save(dummy.newUserWithTeam("이경주", teamPS));
        userRepository.save(dummy.newManagerWithTeam("신연자", teamPS2));
        userRepository.save(dummy.newUserWithTeam("이재웅", teamPS2));
        userRepository.save(dummy.newUserWithTeam("윤인철", teamPS2));
        userRepository.save(dummy.newUserWithTeam("황보광석", teamPS2));
        userRepository.save(dummy.newUserWithTeam("이광조", teamPS2));
        userRepository.save(dummy.newUserWithTeam("이요한", teamPS2));
        userRepository.save(dummy.newManagerWithTeam("심진숙", teamPS3));
        userRepository.save(dummy.newUserWithTeam("허철진", teamPS3));
        userRepository.save(dummy.newUserWithTeam("문성희", teamPS3));
        userRepository.save(dummy.newUserWithTeam("이원우", teamPS3));
        userRepository.save(dummy.newUserWithTeam("이석주", teamPS3));
        userRepository.save(dummy.newUserWithTeam("이백준", teamPS3));
        userRepository.save(dummy.newUserWithTeam("이석준", teamPS3));

        em.clear();
    }

    @DisplayName("사용자 권한 설정 페이지 조회 성공")
    @WithUserDetails(value = "admin@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getAdminPage_test() throws Exception {
        // given
        String teamName = "";
        String keyword = "이";
        String page = "0";

        // when
        ResultActions resultActions = mvc
                .perform(get("/auth/admin")
                        .param("teamName", teamName)
                        .param("keyword", keyword)
                        .param("page", page));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));

        resultActions.andExpect(jsonPath("$.data.teamList[0].teamId").value(1L));
        resultActions.andExpect(jsonPath("$.data.teamList[0].teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.teamList[0].teamCount").value(7));
        resultActions.andExpect(jsonPath("$.data.teamList[1].teamId").value(2L));
        resultActions.andExpect(jsonPath("$.data.teamList[1].teamName").value("회계팀"));
        resultActions.andExpect(jsonPath("$.data.teamList[1].teamCount").value(6));
        resultActions.andExpect(jsonPath("$.data.teamList[2].teamId").value(3L));
        resultActions.andExpect(jsonPath("$.data.teamList[2].teamName").value("마케팅팀"));
        resultActions.andExpect(jsonPath("$.data.teamList[2].teamCount").value(7));

        resultActions.andExpect(jsonPath("$.data.userList[0].userId").value(8L));
        resultActions.andExpect(jsonPath("$.data.userList[0].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[0].name").value("이경주"));
        resultActions.andExpect(jsonPath("$.data.userList[0].email").value("이경주@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[0].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[0].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[0].teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.userList[0].role").value(Role.USER.getRole()));

        resultActions.andExpect(jsonPath("$.data.userList[1].userId").value(13L));
        resultActions.andExpect(jsonPath("$.data.userList[1].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[1].name").value("이광조"));
        resultActions.andExpect(jsonPath("$.data.userList[1].email").value("이광조@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[1].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[1].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[1].teamName").value("회계팀"));
        resultActions.andExpect(jsonPath("$.data.userList[1].role").value(Role.USER.getRole()));

        resultActions.andExpect(jsonPath("$.data.userList[2].userId").value(20L));
        resultActions.andExpect(jsonPath("$.data.userList[2].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[2].name").value("이백준"));
        resultActions.andExpect(jsonPath("$.data.userList[2].email").value("이백준@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[2].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[2].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[2].teamName").value("마케팅팀"));
        resultActions.andExpect(jsonPath("$.data.userList[2].role").value(Role.USER.getRole()));

        resultActions.andExpect(jsonPath("$.data.userList[3].userId").value(5L));
        resultActions.andExpect(jsonPath("$.data.userList[3].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[3].name").value("이빛나"));
        resultActions.andExpect(jsonPath("$.data.userList[3].email").value("이빛나@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[3].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[3].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[3].teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.userList[3].role").value(Role.USER.getRole()));

        resultActions.andExpect(jsonPath("$.data.userList[4].userId").value(6L));
        resultActions.andExpect(jsonPath("$.data.userList[4].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[4].name").value("이산"));
        resultActions.andExpect(jsonPath("$.data.userList[4].email").value("이산@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[4].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[4].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[4].teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.userList[4].role").value(Role.USER.getRole()));

        resultActions.andExpect(jsonPath("$.data.userList[5].userId").value(19L));
        resultActions.andExpect(jsonPath("$.data.userList[5].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[5].name").value("이석주"));
        resultActions.andExpect(jsonPath("$.data.userList[5].email").value("이석주@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[5].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[5].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[5].teamName").value("마케팅팀"));
        resultActions.andExpect(jsonPath("$.data.userList[5].role").value(Role.USER.getRole()));

        resultActions.andExpect(jsonPath("$.data.userList[6].userId").value(21L));
        resultActions.andExpect(jsonPath("$.data.userList[6].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[6].name").value("이석준"));
        resultActions.andExpect(jsonPath("$.data.userList[6].email").value("이석준@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[6].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[6].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[6].teamName").value("마케팅팀"));
        resultActions.andExpect(jsonPath("$.data.userList[6].role").value(Role.USER.getRole()));

        resultActions.andExpect(jsonPath("$.data.userList[7].userId").value(2L));
        resultActions.andExpect(jsonPath("$.data.userList[7].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[7].name").value("이승민"));
        resultActions.andExpect(jsonPath("$.data.userList[7].email").value("이승민@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[7].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[7].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[7].teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.userList[7].role").value(Role.MANAGER.getRole()));

        resultActions.andExpect(jsonPath("$.data.userList[8].userId").value(14L));
        resultActions.andExpect(jsonPath("$.data.userList[8].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[8].name").value("이요한"));
        resultActions.andExpect(jsonPath("$.data.userList[8].email").value("이요한@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[8].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[8].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[8].teamName").value("회계팀"));
        resultActions.andExpect(jsonPath("$.data.userList[8].role").value(Role.USER.getRole()));

        resultActions.andExpect(jsonPath("$.data.userList[9].userId").value(18L));
        resultActions.andExpect(jsonPath("$.data.userList[9].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[9].name").value("이원우"));
        resultActions.andExpect(jsonPath("$.data.userList[9].email").value("이원우@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[9].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[9].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[9].teamName").value("마케팅팀"));
        resultActions.andExpect(jsonPath("$.data.userList[9].role").value(Role.USER.getRole()));

        resultActions.andExpect(jsonPath("$.data.userList[10].userId").value(3L));
        resultActions.andExpect(jsonPath("$.data.userList[10].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[10].name").value("이윤경"));
        resultActions.andExpect(jsonPath("$.data.userList[10].email").value("이윤경@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[10].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[10].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[10].teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.userList[10].role").value(Role.USER.getRole()));

        resultActions.andExpect(jsonPath("$.data.userList[11].userId").value(7L));
        resultActions.andExpect(jsonPath("$.data.userList[11].profileImage").isEmpty());
        resultActions.andExpect(jsonPath("$.data.userList[11].name").value("이인호"));
        resultActions.andExpect(jsonPath("$.data.userList[11].email").value("이인호@gmail.com"));
        resultActions.andExpect(jsonPath("$.data.userList[11].phoneNumber").value("010-1234-5678"));
        resultActions.andExpect(jsonPath("$.data.userList[11].startWork").value(LocalDateTime.now().toLocalDate().toString()));
        resultActions.andExpect(jsonPath("$.data.userList[11].teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.userList[11].role").value(Role.USER.getRole()));

        resultActions.andExpect(jsonPath("$.data.size").value(12));
        resultActions.andExpect(jsonPath("$.data.totalElements").value(14L));
        resultActions.andExpect(jsonPath("$.data.totalPages").value(2));
        resultActions.andExpect(jsonPath("$.data.curPage").value(0));
        resultActions.andExpect(jsonPath("$.data.first").value(true));
        resultActions.andExpect(jsonPath("$.data.last").value(false));
        resultActions.andExpect(jsonPath("$.data.empty").value(false));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("사용자 권한 설정 페이지 조회 실패")
    @WithUserDetails(value = "이승민@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void getAdminPage_fail_test() throws Exception {
        // given
        String teamName = "";
        String keyword = "이";
        String page = "0";

        // when
        ResultActions resultActions = mvc
                .perform(get("/auth/admin")
                        .param("teamName", teamName)
                        .param("keyword", keyword)
                        .param("page", page));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(403));
        resultActions.andExpect(jsonPath("$.msg").value("forbidden"));
        resultActions.andExpect(jsonPath("$.data").value("권한이 없습니다."));
        resultActions.andExpect(status().isForbidden());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("사용자 권한/팀 변경 성공")
    @WithUserDetails(value = "admin@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void updateAuthAndTeam_test() throws Exception {
        // given
        UpdateAuthAndTeamInDTO updateAuthAndTeamInDTO = new UpdateAuthAndTeamInDTO();
        updateAuthAndTeamInDTO.setUserId(2L);
        updateAuthAndTeamInDTO.setTeamName("회계팀");
        updateAuthAndTeamInDTO.setRole(Role.USER.getRole());
        String requestBody = om.writeValueAsString(updateAuthAndTeamInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(patch("/auth/admin/user")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("사용자 권한/팀 변경 실패")
    @WithUserDetails(value = "admin@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void updateAuthAndTeam_fail_test() throws Exception {
        // given
        UpdateAuthAndTeamInDTO updateAuthAndTeamInDTO = new UpdateAuthAndTeamInDTO();
        updateAuthAndTeamInDTO.setUserId(2L);
        updateAuthAndTeamInDTO.setTeamName("영업팀");
        updateAuthAndTeamInDTO.setRole(Role.USER.getRole());
        String requestBody = om.writeValueAsString(updateAuthAndTeamInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(patch("/auth/admin/user")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(404));
        resultActions.andExpect(jsonPath("$.msg").value("notFound"));
        resultActions.andExpect(jsonPath("$.data").value("존재하지 않는 팀입니다."));
        resultActions.andExpect(status().isNotFound());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("팀 추가 성공")
    @WithUserDetails(value = "admin@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void add_test() throws Exception {
        // given
        AdminRequest.AddInDTO addInDTO = new AdminRequest.AddInDTO();
        addInDTO.setTeamName("영업팀");
        String requestBody = om.writeValueAsString(addInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/admin/team")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data.teamId").value(5L));
        resultActions.andExpect(jsonPath("$.data.teamName").value("영업팀"));
        resultActions.andExpect(jsonPath("$.data.teamCount").value(0));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("팀 추가 실패")
    @WithUserDetails(value = "admin@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void add_fail_test() throws Exception {
        // given
        AdminRequest.AddInDTO addInDTO = new AdminRequest.AddInDTO();
        addInDTO.setTeamName("개발팀");
        String requestBody = om.writeValueAsString(addInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/admin/team")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.value").value("이미 존재하는 팀입니다."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("팀 삭제 성공")
    @WithUserDetails(value = "admin@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void delete_test() throws Exception {
        // given
        Long teamId = 4L;

        // when
        ResultActions resultActions = mvc
                .perform(delete("/auth/admin/team/{id}", teamId));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
        resultActions.andExpect(jsonPath("$.data").isEmpty());
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("팀 삭제 실패")
    @WithUserDetails(value = "admin@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void delete_fail_test() throws Exception {
        // given
        Long teamId = 1L;

        // when
        ResultActions resultActions = mvc
                .perform(delete("/auth/admin/team/{id}", teamId));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(400));
        resultActions.andExpect(jsonPath("$.msg").value("badRequest"));
        resultActions.andExpect(jsonPath("$.data.key").value(String.valueOf(teamId)));
        resultActions.andExpect(jsonPath("$.data.value").value("팀에 소속된 인원이 1명 이상입니다."));
        resultActions.andExpect(status().isBadRequest());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
