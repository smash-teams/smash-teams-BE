package smash.teams.be.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.core.exception.*;
import smash.teams.be.dto.schedule.ScheduleRequest;
import smash.teams.be.dto.schedule.ScheduleResponse;
import smash.teams.be.model.schedule.Schedule;
import smash.teams.be.model.schedule.ScheduleRepository;
import smash.teams.be.model.schedule.Status;
import smash.teams.be.model.schedule.Type;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest extends DummyEntity {

    @InjectMocks
    private ScheduleService scheduleService;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Spy
    private ObjectMapper om;

    @DisplayName("개인 스케쥴 내역")
    @Test
    public void getScheduleList_test() {
        // given
        User user = User.builder().id(3L).build();
        Long userId = 3L;

        List<Schedule> schedules = new ArrayList<>();
        Schedule schedule1 = newScheduleForTest(1L, 1L, "CEO", "kimuceo", null, null, "APPROVED", "병가");
        Schedule schedule2 = newScheduleForTest(2L, 2L, "MANAGER", "kimmanager", 1L, "총무팀", "APPROVED", "휴가");
        Schedule schedule3 = newScheduleForTest(3L, 3L, "USER", "kimuser", 2L, "개발팀", "APPROVED", "병가");
        Schedule schedule4 = newScheduleForTest(4L, 3L, "USER", "kimuser", 2L, "개발팀", "REJECTED", "휴가");
        Schedule schedule5 = newScheduleForTest(5L, 2L, "MANAGER", "kimmanager", 1L, "총무팀", "FIRST", "휴가");
        Schedule schedule6 = newScheduleForTest(6L, 1L, "CEO", "kimceo", null, null, "APPROVED", "휴가");
        Schedule schedule7 = newScheduleForTest(7L, 3L, "USER", "kimuser", 2L, "개발팀", "LAST", "휴가");

        schedules.add(schedule3);
        schedules.add(schedule4);
        schedules.add(schedule7);

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(scheduleRepository.findSchedulesByUserId(any())).thenReturn(schedules);

        // when
        ScheduleResponse.ScheduleListDTO scheduleListDTO = scheduleService.getScheduleList(userId);

        // Then
        assertThat(scheduleListDTO).isNotNull();
        assertThat(scheduleListDTO.getScheduleList()).isNotNull();
        assertThat(scheduleListDTO.getScheduleList().size()).isEqualTo(schedules.size());
        assertThat(scheduleListDTO.getScheduleList()).hasSize(3);
        assertThat(scheduleListDTO.getScheduleList().get(0).getUser().getUserId()).isEqualTo(schedules.get(0).getUser().getId());
        assertThat(scheduleListDTO.getScheduleList().get(0).getScheduleId()).isEqualTo(schedules.get(0).getId());
        assertThat(scheduleListDTO.getScheduleList().get(0).getType()).isEqualTo(schedules.get(0).getType());
        assertThat(scheduleListDTO.getScheduleList().get(0).getReason()).isEqualTo(schedules.get(0).getReason());
        assertThat(scheduleListDTO.getScheduleList().get(0).getEndDate()).isEqualTo(schedules.get(0).getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(scheduleListDTO.getScheduleList().get(0).getStatus()).isEqualTo(schedules.get(0).getStatus());
        assertThat(scheduleListDTO.getScheduleList().get(1).getUser().getUserId()).isEqualTo(schedules.get(1).getUser().getId());
        assertThat(scheduleListDTO.getScheduleList().get(1).getUser().getName()).isEqualTo(schedules.get(1).getUser().getName());
        assertThat(scheduleListDTO.getScheduleList().get(1).getUser().getEmail()).isEqualTo(schedules.get(1).getUser().getEmail());
        assertThat(scheduleListDTO.getScheduleList().get(1).getUser().getPhoneNumber()).isEqualTo(schedules.get(1).getUser().getPhoneNumber());
        assertThat(scheduleListDTO.getScheduleList().get(2).getUser().getRole()).isEqualTo(schedules.get(2).getUser().getRole());
        assertThat(scheduleListDTO.getScheduleList().get(2).getUser().getTeamName()).isEqualTo(schedules.get(2).getUser().getTeam().getTeamName());
        assertThat(scheduleListDTO.getScheduleList().get(2).getUser().getStartWork()).isEqualTo(schedules.get(2).getUser().getStartWork().format(DateTimeFormatter.ISO_LOCAL_DATE));
        assertThat(scheduleListDTO.getScheduleList().get(2).getUser().getProfileImage()).isEqualTo(schedules.get(2).getUser().getProfileImage());
    }

    @DisplayName("스케쥴 관리페이지 : 팀장 권한 조회 (같은 팀원 스케쥴)")
    @Test
    public void getScheduleListForManage_case1_test() {
        // given
        User user = User.builder().id(2L).role("MANAGER").team(Team.builder().teamName("개발팀").build()).build();
        Long userId = 2L;

        Schedule schedule1 = newScheduleForTest(1L, 1L, "CEO", "kimuceo", null, null, "APPROVED", "병가");
        Schedule schedule2 = newScheduleForTest(2L, 2L, "MANAGER", "kimmanager", 1L, "개발팀", "APPROVED", "휴가");
        Schedule schedule3 = newScheduleForTest(3L, 3L, "USER", "kimuser", 1L, "개발팀", "APPROVED", "병가");
        Schedule schedule4 = newScheduleForTest(4L, 3L, "USER", "kimuser", 1L, "개발팀", "REJECTED", "휴가");
        Schedule schedule5 = newScheduleForTest(5L, 2L, "MANAGER", "kimmanager", 1L, "개발팀", "FIRST", "휴가");
        Schedule schedule6 = newScheduleForTest(6L, 1L, "CEO", "kimceo", null, null, "APPROVED", "휴가");
        Schedule schedule7 = newScheduleForTest(7L, 3L, "USER", "kimuser", 1L, "개발팀", "LAST", "휴가");

        List<Schedule> schedules = new ArrayList<>();
        schedules.add(schedule1);
        schedules.add(schedule2);
        schedules.add(schedule3);
        schedules.add(schedule4);
        schedules.add(schedule5);
        schedules.add(schedule6);
        schedules.add(schedule7);

        List<Schedule> schedulesManager = new ArrayList<>();

        for (Schedule schedule : schedules) {
            if (schedule.getStatus().equals(Status.FIRST.getStatus()) && schedule.getUser().getTeam().getId().equals(user.getTeam().getId())) {
                schedulesManager.add(schedule);
            }
        }

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(scheduleRepository.findSchedules()).thenReturn(schedulesManager);


        // when
        ScheduleResponse.ScheduleListDTO scheduleListDTO = scheduleService.getScheduleListForManage(userId);

        // Then
        assertThat(scheduleListDTO).isNotNull();
        assertThat(scheduleListDTO.getScheduleList()).isNotNull();
        assertThat(scheduleListDTO.getScheduleList().size()).isEqualTo(0);

    }

    @DisplayName("스케쥴 관리페이지 : CEO권한 스케쥴조회")
    @Test
    public void getScheduleListForManage_case2_test() {
        // given
        User user = User.builder().id(1L).role("CEO").team(null).build();
        Long userId = 1L;
        Schedule schedule1 = newScheduleForTest(1L, 1L, "CEO", "kimuceo", null, null, "APPROVED", "병가");
        Schedule schedule2 = newScheduleForTest(2L, 2L, "MANAGER", "kimmanager", 1L, "개발팀", "APPROVED", "휴가");
        Schedule schedule3 = newScheduleForTest(3L, 3L, "USER", "kimuser", 1L, "개발팀", "APPROVED", "병가");
        Schedule schedule4 = newScheduleForTest(4L, 3L, "USER", "kimuser", 1L, "개발팀", "REJECTED", "휴가");
        Schedule schedule5 = newScheduleForTest(5L, 2L, "MANAGER", "kimmanager", 1L, "개발팀", "FIRST", "휴가");
        Schedule schedule6 = newScheduleForTest(6L, 1L, "CEO", "kimceo", null, null, "APPROVED", "휴가");
        Schedule schedule7 = newScheduleForTest(7L, 3L, "USER", "kimuser", 1L, "개발팀", "LAST", "휴가");

        List<Schedule> schedules = new ArrayList<>();
        schedules.add(schedule1);
        schedules.add(schedule2);
        schedules.add(schedule3);
        schedules.add(schedule4);
        schedules.add(schedule5);
        schedules.add(schedule6);
        schedules.add(schedule7);

        List<Schedule> schedulesCEO = new ArrayList<>();

        for (Schedule schedule : schedules) {
            if (schedule.getStatus().equals(Status.LAST.getStatus())) {
                schedulesCEO.add(schedule);
            }
        }

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(scheduleRepository.findSchedules()).thenReturn(schedulesCEO);


        // when
        ScheduleResponse.ScheduleListDTO scheduleListDTO = scheduleService.getScheduleListForManage(user.getId());

        // Then
        assertThat(scheduleListDTO).isNotNull();
        assertThat(scheduleListDTO.getScheduleList()).isNotNull();
        assertThat(scheduleListDTO.getScheduleList().size()).isEqualTo(schedulesCEO.size());

    }


    @Test
    public void findByScheduleList_test() throws Exception {
        // given
        List<Schedule> scheduleListPS = new ArrayList<>();
        scheduleListPS.add(newMockScheduleWithUserWithTeam(1L, 11L, 111L, "유저A", "A팀"));
        scheduleListPS.add(newMockScheduleWithUserWithTeam(2L, 22L, 222L, "유저B", "B팀"));
        scheduleListPS.add(newMockScheduleWithUserWithTeam(3L, 33L, 333L, "유저C", "C팀"));
        scheduleListPS.add(newMockScheduleWithUserWithTeam(4L, 44L, 444L, "유저D", "D팀"));

        Mockito.when(scheduleRepository.findSchedulesWithName()).thenReturn(scheduleListPS); // 영속화 된 상태 가정
        System.out.println("테스트1 : " + scheduleListPS);

        // when
        ScheduleResponse.ListOutDto listOutDto = scheduleService.findByScheduleList(); // Dto 된 상태
        String responseBody = om.writeValueAsString(listOutDto);
        System.out.println("테스트2 : " + responseBody);

        // then
        assertThat(listOutDto.getScheduleList().size()).isEqualTo(scheduleListPS.size());
        assertThat(listOutDto.getScheduleList().get(3).getUser().getTeamName()).isEqualTo("D팀");
        assertThat(listOutDto.getScheduleList().get(1).getEndDate()).isEqualTo("2022-01-01T09:00:00");
    }

//    @Test
//    public void makeScheduleRequest_test() {
//        // given
//        MakeScheduleRequestInDTO makeScheduleRequestInDTO = new MakeScheduleRequestInDTO();
//        makeScheduleRequestInDTO.setStartDate("2023-03-03T09:00:00");
//        makeScheduleRequestInDTO.setEndDate("2023-03-03T12:00:00");
//        makeScheduleRequestInDTO.setType(Type.HALFOFF.getType());
//        makeScheduleRequestInDTO.setReason("병원 예약");
//
//        // given2
//        Long userId = 1L;
//
//        // stub
//        User user = newMockUser(userId, "이승민");
//        Mockito.when(userRepository.findById(anyLong()))
//                .thenReturn(Optional.of(user));
//
//        Schedule schedulePS = newMockSchedule(user);
//        Mockito.when(scheduleRepository.save(any()))
//                .thenReturn(schedulePS);
//
//        // when
//        Schedule schedule = scheduleService.makeScheduleRequest(makeScheduleRequestInDTO, userId);
//
//        // then
//        assertThat(schedule.getId()).isEqualTo(1L);
//        assertThat(schedule.getStartDate()).isEqualTo(makeScheduleRequestInDTO.getStartDate());
//        assertThat(schedule.getEndDate()).isEqualTo(makeScheduleRequestInDTO.getEndDate());
//        assertThat(schedule.getType()).isEqualTo(makeScheduleRequestInDTO.getType());
//        assertThat(schedule.getReason()).isEqualTo(makeScheduleRequestInDTO.getReason());
//        assertThat(schedule.getUser().getId()).isEqualTo(userId);
//    }

    @DisplayName("연차승인 : FIRST-to-LAST")
    @Test
    public void orderSchedule_case1_test () {
        // given
        Long scheduleId = 1L;
        String status = "APPROVED";

        ScheduleRequest.OrderScheduleInDTO orderScheduleInDTO = new ScheduleRequest.OrderScheduleInDTO();
        orderScheduleInDTO.setScheduleId(scheduleId);
        orderScheduleInDTO.setStatus(status);

        User manager = User.builder().id(1L).role(Role.MANAGER.getRole()).build();
        Long id = 1L;
        User user = User.builder().remain(20).build();
        Schedule schedulePS = Schedule.builder().id(1L).user(user).status(Status.FIRST.getStatus()).type(Type.DAYOFF.getType()).build();

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(manager));
        Mockito.when(scheduleRepository.findScheduleByScheduleId(scheduleId)).thenReturn(schedulePS);
        Mockito.when(scheduleRepository.save(any())).thenReturn(schedulePS);

        // when
        ScheduleResponse.OrderScheduleOutWithRemainDTO result = scheduleService.orderSchedule(id, orderScheduleInDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRemain()).isEqualTo(20);
        assertThat(result.getStatus()).isEqualTo("LAST");
    }

    @DisplayName("연차승인 : LAST-to-APPROVED")
    @Test
    public void orderSchedule_case2_test () {
        // given
        Long scheduleId = 1L;
        String status = "APPROVED";

        ScheduleRequest.OrderScheduleInDTO orderScheduleInDTO = new ScheduleRequest.OrderScheduleInDTO();
        orderScheduleInDTO.setScheduleId(scheduleId);
        orderScheduleInDTO.setStatus(status);

        User ceo = User.builder().role(Role.CEO.getRole()).build();
        Long id = 2L;
        User user = User.builder().remain(20).build();
        Schedule schedulePS = Schedule.builder().id(1L).user(user).status(Status.LAST.getStatus()).type(Type.DAYOFF.getType()).build();

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(ceo));
        Mockito.when(scheduleRepository.findScheduleByScheduleId(scheduleId)).thenReturn(schedulePS);
        Mockito.when(scheduleRepository.save(any())).thenReturn(schedulePS);

        // when
        ScheduleResponse.OrderScheduleOutWithRemainDTO result = scheduleService.orderSchedule(id, orderScheduleInDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRemain()).isEqualTo(19);
        assertThat(result.getStatus()).isEqualTo("APPROVED");
    }

    @DisplayName("반차승인 : FIRST-to-LAST")
    @Test
    public void orderSchedule_case3_test () {
        // given
        Long scheduleId = 1L;
        String status = "APPROVED";

        ScheduleRequest.OrderScheduleInDTO orderScheduleInDTO = new ScheduleRequest.OrderScheduleInDTO();
        orderScheduleInDTO.setScheduleId(scheduleId);
        orderScheduleInDTO.setStatus(status);

        User manager = User.builder().role(Role.MANAGER.getRole()).build();
        Long id = 1L;
        User user = User.builder().remain(20).build();
        Schedule schedulePS = Schedule.builder().id(1L).user(user).status("FIRST").type("HALFOFF").build();

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(manager));
        Mockito.when(scheduleRepository.findScheduleByScheduleId(scheduleId)).thenReturn(schedulePS);
        Mockito.when(scheduleRepository.save(any())).thenReturn(schedulePS);

        // when
        ScheduleResponse.OrderScheduleOutWithRemainDTO result = scheduleService.orderSchedule(id,orderScheduleInDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRemain()).isEqualTo(20);
        assertThat(result.getStatus()).isEqualTo("LAST");
    }

    @DisplayName("반차승인 : LAST-to-APPROVED")
    @Test
    public void orderSchedule_case4_test () {
        // given
        Long scheduleId = 1L;
        String status = "APPROVED";

        ScheduleRequest.OrderScheduleInDTO orderScheduleInDTO = new ScheduleRequest.OrderScheduleInDTO();
        orderScheduleInDTO.setScheduleId(scheduleId);
        orderScheduleInDTO.setStatus(status);

        User ceo = User.builder().role(Role.CEO.getRole()).build();
        Long id = 2L;
        User user = User.builder().remain(20).build();
        Schedule schedulePS = Schedule.builder().id(1L).user(user).status("LAST").type("HALFOFF").build();

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(ceo));
        Mockito.when(scheduleRepository.findScheduleByScheduleId(scheduleId)).thenReturn(schedulePS);
        Mockito.when(scheduleRepository.save(any())).thenReturn(schedulePS);

        // when
        ScheduleResponse.OrderScheduleOutWithRemainDTO result = scheduleService.orderSchedule(id, orderScheduleInDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRemain()).isEqualTo(19.5);
        assertThat(result.getStatus()).isEqualTo("APPROVED");
    }

    @DisplayName("당직승인 : FIRST-to-LAST")
    @Test
    public void orderSchedule_case5_test () {
        // given
        Long scheduleId = 1L;
        String status = "APPROVED";

        ScheduleRequest.OrderScheduleInDTO orderScheduleInDTO = new ScheduleRequest.OrderScheduleInDTO();
        orderScheduleInDTO.setScheduleId(scheduleId);
        orderScheduleInDTO.setStatus(status);

        User manager = User.builder().role(Role.MANAGER.getRole()).build();
        Long id = 1L;
        User user = User.builder().remain(20).build();
        Schedule schedulePS = Schedule.builder().id(1L).user(user).status("FIRST").type("SHIFT").build();

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(manager));
        Mockito.when(scheduleRepository.findScheduleByScheduleId(scheduleId)).thenReturn(schedulePS);
        Mockito.when(scheduleRepository.save(any())).thenReturn(schedulePS);

        // when
        ScheduleResponse.OrderScheduleOutWithRemainDTO result = scheduleService.orderSchedule(id,orderScheduleInDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRemain()).isEqualTo(20);
        assertThat(result.getStatus()).isEqualTo("LAST");
    }

    @DisplayName("당직승인 : LAST-to-APPROVED")
    @Test
    public void orderSchedule_case6_test () {
        // given
        Long scheduleId = 1L;
        String status = "APPROVED";

        ScheduleRequest.OrderScheduleInDTO orderScheduleInDTO = new ScheduleRequest.OrderScheduleInDTO();
        orderScheduleInDTO.setScheduleId(scheduleId);
        orderScheduleInDTO.setStatus(status);

        User ceo = User.builder().role(Role.CEO.getRole()).build();
        Long id = 2L;
        User user = User.builder().remain(20).build();
        Schedule schedulePS = Schedule.builder().id(1L).user(user).status("LAST").type("SHIFT").build();
        Schedule updatedSchedulePS = Schedule.builder().id(1L).user(user).status("APPROVED").type("SHIFT").build();

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(ceo));
        Mockito.when(scheduleRepository.findScheduleByScheduleId(scheduleId)).thenReturn(schedulePS);
        Mockito.when(scheduleRepository.save(any())).thenReturn(updatedSchedulePS);

        // when
        ScheduleResponse.OrderScheduleOutWithRemainDTO result = scheduleService.orderSchedule(id,orderScheduleInDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRemain()).isEqualTo(20);
        assertThat(result.getStatus()).isEqualTo("APPROVED");
    }

    @DisplayName("거절하기 : FIRST & LAST to REJECTED")
    @Test
    public void orderSchedule_case7_test () {
        // given
        Long scheduleId = 1L;
        String status = "REJECTED";

        ScheduleRequest.OrderScheduleInDTO orderScheduleInDTO = new ScheduleRequest.OrderScheduleInDTO();
        orderScheduleInDTO.setScheduleId(scheduleId);
        orderScheduleInDTO.setStatus(status);

        User ceo = User.builder().role(Role.CEO.getRole()).build();
        Long id = 2L;
        User user = User.builder().remain(20).build();
        Schedule schedulePS = Schedule.builder().id(1L).user(user).status("LAST").type("DAYOFF").build();

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(ceo));
        Mockito.when(scheduleRepository.findScheduleByScheduleId(scheduleId)).thenReturn(schedulePS);
        Mockito.when(scheduleRepository.save(any())).thenReturn(schedulePS);

        // when
        ScheduleResponse.OrderScheduleOutWithRemainDTO result = scheduleService.orderSchedule(id, orderScheduleInDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRemain()).isEqualTo(20);
        assertThat(result.getStatus()).isEqualTo("REJECTED");
    }

    @DisplayName("예외발생 테스트 : 이미 승인되었거나 거절된 스케쥴")
    @Test
    public void orderSchedule_case8_test () {
        // given
        Long scheduleId = 1L;
        String status = "APPROVED";

        ScheduleRequest.OrderScheduleInDTO orderScheduleInDTO = new ScheduleRequest.OrderScheduleInDTO();
        orderScheduleInDTO.setScheduleId(scheduleId);
        orderScheduleInDTO.setStatus(status);

        User manager = User.builder().role(Role.MANAGER.getRole()).build();
        Long id = 1L;
        User user = User.builder().remain(20).build();
        Schedule schedulePS = Schedule.builder().id(1L).user(user).status("APPROVED").type("DAYOFF").build();

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(manager));
        Mockito.when(scheduleRepository.findScheduleByScheduleId(scheduleId)).thenReturn(schedulePS);

        // when, then
        assertThrows(Exception400.class, () -> scheduleService.orderSchedule(id,orderScheduleInDTO));

    }

    @DisplayName("예외발생 테스트 : 이미 승인되었거나 거절된 스케쥴")
    @Test
    public void orderSchedule_case9_test () {
        // given
        Long scheduleId = 1L;
        String status = "REJECTED";

        ScheduleRequest.OrderScheduleInDTO orderScheduleInDTO = new ScheduleRequest.OrderScheduleInDTO();
        orderScheduleInDTO.setScheduleId(scheduleId);
        orderScheduleInDTO.setStatus(status);

        User ceo = User.builder().role(Role.CEO.getRole()).build();
        Long id = 2L;
        User user = User.builder().remain(20).build();
        Schedule schedulePS = Schedule.builder().id(1L).user(user).status("APPROVED").type("DAYOFF").build();

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(ceo));
        Mockito.when(scheduleRepository.findScheduleByScheduleId(scheduleId)).thenReturn(schedulePS);

        // when, then
        assertThrows(Exception400.class, () -> scheduleService.orderSchedule(id,orderScheduleInDTO));

    }

}

