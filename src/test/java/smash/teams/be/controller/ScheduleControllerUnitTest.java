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
import smash.teams.be.core.WithMockUser;
import smash.teams.be.core.WithMockUserWithTeam;
import smash.teams.be.core.advice.LogAdvice;
import smash.teams.be.core.advice.ValidAdvice;
import smash.teams.be.core.config.FilterRegisterConfig;
import smash.teams.be.core.config.SecurityConfig;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.dto.schedule.ScheduleRequest.MakeScheduleRequestInDTO;
import smash.teams.be.dto.schedule.ScheduleResponse;
import smash.teams.be.model.errorLog.ErrorLogRepository;
import smash.teams.be.model.schedule.Schedule;
import smash.teams.be.model.schedule.Type;
import smash.teams.be.model.user.User;
import smash.teams.be.service.ScheduleService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
        controllers = {ScheduleController.class}
)
public class ScheduleControllerUnitTest extends DummyEntity {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private ScheduleService scheduleService;
    @MockBean
    private ErrorLogRepository errorLogRepository;

    @WithMockUserWithTeam
    @Test
    public void getScheduleList_test() throws Exception {
        // given
        Long id = 3L;
        Schedule schedule1 = newScheduleForTest(1L, 1L, "USER", "kimuser", 2L, "개발팀", "LAST", "병가");
        Schedule schedule2 = newScheduleForTest(2L, 1L, "USER", "kimuser", 2L, "개발팀", "REJECTED", "여행");
        Schedule schedule3 = newScheduleForTest(3L, 1L, "USER", "kimuser", 2L, "개발팀", "FIRST", "여행");

        List<ScheduleResponse.ScheduleOutDTO> scheduleOutList = new ArrayList<>();
        scheduleOutList.add(new ScheduleResponse.ScheduleOutDTO(schedule1, new ScheduleResponse.UserOutDTOWithScheduleOutDTO(schedule1.getUser())));
        scheduleOutList.add(new ScheduleResponse.ScheduleOutDTO(schedule2, new ScheduleResponse.UserOutDTOWithScheduleOutDTO(schedule2.getUser())));
        scheduleOutList.add(new ScheduleResponse.ScheduleOutDTO(schedule3, new ScheduleResponse.UserOutDTOWithScheduleOutDTO(schedule3.getUser())));

        ScheduleResponse.ScheduleListDTO scheduleListDTO = new ScheduleResponse.ScheduleListDTO(scheduleOutList);

        Mockito.when(scheduleService.getScheduleList(any())).thenReturn(scheduleListDTO);

        // when
        ResultActions resultActions = mvc.perform(get("/auth/user/"+id+"/schedule"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.scheduleList.length()").value(3));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].scheduleId").value(1));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].scheduleId").value(2));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].scheduleId").value(3));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].reason").value("병가"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].status").value("REJECTED"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].type").value("DAYOFF"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].user.userId").value(1L));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].user.name").value("kimuser"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].user.teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].user.role").value("USER"));
        resultActions.andExpect(status().isOk());
    }

    @WithMockUserWithTeam(id=2L, username="kimmanager@gmail.com", role = "MANAGER", teamName = "개발팀")
    @Test
    public void getScheduleListForManage_test() throws Exception {
        // given
//        Long userId = 1L;
//        String role = "CEO";
//        String teamName = null;
        Long id = 2L;

        Schedule schedule1 = newScheduleForTest(1L, 3L, "USER", "kimuser", 2L, "개발팀", "LAST", "병가");
        Schedule schedule2 = newScheduleForTest(2L, 3L, "USER", "kimuser", 2L, "개발팀", "REJECTED", "여행");
        Schedule schedule3 = newScheduleForTest(3L, 3L, "USER", "kimuser", 2L, "개발팀", "FIRST", "여행");

        List<ScheduleResponse.ScheduleOutDTO> scheduleOutList = new ArrayList<>();
        scheduleOutList.add(new ScheduleResponse.ScheduleOutDTO(schedule1, new ScheduleResponse.UserOutDTOWithScheduleOutDTO(schedule1.getUser())));
        scheduleOutList.add(new ScheduleResponse.ScheduleOutDTO(schedule2, new ScheduleResponse.UserOutDTOWithScheduleOutDTO(schedule2.getUser())));
        scheduleOutList.add(new ScheduleResponse.ScheduleOutDTO(schedule3, new ScheduleResponse.UserOutDTOWithScheduleOutDTO(schedule3.getUser())));

        ScheduleResponse.ScheduleListDTO scheduleListDTO = new ScheduleResponse.ScheduleListDTO(scheduleOutList);

        Mockito.when(scheduleService.getScheduleListForManage(any())).thenReturn(scheduleListDTO);

        // when
        ResultActions resultActions = mvc.perform(get("/auth/super/schedule"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.scheduleList.length()").value(3));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].scheduleId").value(1));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].scheduleId").value(2));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].scheduleId").value(3));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].reason").value("병가"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].status").value("REJECTED"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].type").value("DAYOFF"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[2].user.userId").value(3L));
        resultActions.andExpect(jsonPath("$.data.scheduleList[0].user.name").value("kimuser"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].user.teamName").value("개발팀"));
        resultActions.andExpect(jsonPath("$.data.scheduleList[1].user.role").value("USER"));
        resultActions.andExpect(status().isOk());
    }

    @WithMockUser(id = 1L, name = "ssar", role = "USER", status = "ACTIVE")
    @Test
    public void loadScheduleList_test() throws Exception {
        // given
        Long id = 1L;
        List<Schedule> scheduleListPS = new ArrayList<>();
        scheduleListPS.add(newMockScheduleWithUserWithTeam(1L, 11L, 111L, "유저A", "A팀"));
        scheduleListPS.add(newMockScheduleWithUserWithTeam(2L, 22L, 222L, "유저B", "B팀"));
        scheduleListPS.add(newMockScheduleWithUserWithTeam(3L, 33L, 333L, "유저C", "C팀"));
        scheduleListPS.add(newMockScheduleWithUserWithTeam(4L, 44L, 444L, "유저D", "D팀"));

        // stub
        ScheduleResponse.ListOutDto listOutDto = new ScheduleResponse.ListOutDto(scheduleListPS);
        Mockito.when(scheduleService.findByScheduleList()).thenReturn(listOutDto);
        System.out.println("테스트2 : " + listOutDto);

        // when
        ResultActions resultActions = mvc.perform(get("/auth/user/main"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트2 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.status").value(200));
        resultActions.andExpect(jsonPath("$.msg").value("ok"));
    }

    @WithMockUserWithTeam(id=2L, username="kimmanager@gmail.com", role = "MANAGER", teamName = "개발팀")
    @Test
    public void orderSchedule_test() throws Exception {
        Long id = 2L;
        User user = User.builder().remain(19.5).build();
        Schedule schedule = Schedule.builder().id(1L).user(user).status("LAST").type("DAYOFF").build();

        ScheduleResponse.OrderScheduleOutWithRemainDTO orderScheduleOutWithRemainDTO
                = new ScheduleResponse.OrderScheduleOutWithRemainDTO(schedule);

        String requestBody = om.writeValueAsString(orderScheduleOutWithRemainDTO);

        Mockito.when(scheduleService.orderSchedule(any(),any())).thenReturn(orderScheduleOutWithRemainDTO);

        // when
        ResultActions resultActions = mvc.perform(post("/auth/super/schedule").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.scheduleId").value(1L));
        resultActions.andExpect(jsonPath("$.data.status").value("LAST"));
        resultActions.andExpect(jsonPath("$.data.remain").value(19.5));
        resultActions.andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    public void makeScheduleRequest_test() throws Exception {
        // given
        Long id = 1L;
        MakeScheduleRequestInDTO makeScheduleRequestInDTO = new MakeScheduleRequestInDTO();
        makeScheduleRequestInDTO.setStartDate("2023-03-03T09:00:00");
        makeScheduleRequestInDTO.setEndDate("2023-03-03T12:00:00");
        makeScheduleRequestInDTO.setType(Type.HALFOFF.getType());
        makeScheduleRequestInDTO.setReason("병원 예약");
        String requestBody = om.writeValueAsString(makeScheduleRequestInDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/user/"+id+"/schedule").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
    }

}
