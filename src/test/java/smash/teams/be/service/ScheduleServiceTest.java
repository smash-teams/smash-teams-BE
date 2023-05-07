package smash.teams.be.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import smash.teams.be.dto.schedule.ScheduleResponse;
import smash.teams.be.model.schedule.Schedule;
import smash.teams.be.model.schedule.ScheduleRepository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest extends DummyEntity {

    @InjectMocks
    private ScheduleService scheduleService;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Spy
    private ObjectMapper om;

    @Test
    public void getScheduleList_test() {
        // given
        Long userId = 3L;

        List<Schedule> schedules = new ArrayList<>();
        Schedule schedule1 = newScheduleForTest(1L,1L,"CEO","kimuceo",null,null,"APPROVED","병가");
        Schedule schedule2 = newScheduleForTest(2L,2L,"MANAGER","kimmanager",1L,"총무팀","APPROVED","휴가");
        Schedule schedule3 = newScheduleForTest(3L,3L,"USER","kimuser",2L,"개발팀","APPROVED","병가");
        Schedule schedule4 = newScheduleForTest(4L,3L,"USER","kimuser",2L,"개발팀","REJECTED","휴가");
        Schedule schedule5 = newScheduleForTest(5L,2L,"MANAGER","kimmanager",1L,"총무팀","FIRST","휴가");
        Schedule schedule6 = newScheduleForTest(6L,1L,"CEO","kimceo",null,null,"APPROVED","휴가");
        Schedule schedule7 = newScheduleForTest(7L,3L,"USER","kimuser",2L,"개발팀","LAST","휴가");

        schedules.add(schedule3);
        schedules.add(schedule4);
        schedules.add(schedule7);


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

    @Test
    public void getScheduleListForManage_test() {
        // given
        Long userId = 1L;
        String role = "CEO";
        String teamName = null;

//        Long userId = 2L;
//        String role = "MANAGER";
//        String teamName = "개발팀";

        Schedule schedule1 = newScheduleForTest(1L,1L,"CEO",    "kimuceo",   null,null,   "APPROVED","병가");
        Schedule schedule2 = newScheduleForTest(2L,2L,"MANAGER","kimmanager",1L,  "개발팀","APPROVED","휴가");
        Schedule schedule3 = newScheduleForTest(3L,3L,"USER",   "kimuser",   1L,  "개발팀","APPROVED","병가");
        Schedule schedule4 = newScheduleForTest(4L,3L,"USER",   "kimuser",   1L,  "개발팀","REJECTED","휴가");
        Schedule schedule5 = newScheduleForTest(5L,2L,"MANAGER","kimmanager",1L,  "개발팀","FIRST",   "휴가");
        Schedule schedule6 = newScheduleForTest(6L,1L,"CEO",    "kimceo",    null,null,   "APPROVED","휴가");
        Schedule schedule7 = newScheduleForTest(7L,3L,"USER",   "kimuser",   1L,  "개발팀","LAST",    "휴가");

        List<Schedule> schedules = new ArrayList<>();
        schedules.add(schedule1);
        schedules.add(schedule2);
        schedules.add(schedule3);
        schedules.add(schedule4);
        schedules.add(schedule5);
        schedules.add(schedule6);
        schedules.add(schedule7);

        List<Schedule> schedulesManager = new ArrayList<>();
        if(role.equals("MANAGER")) {
            for(Schedule schedule : schedules){
                if(teamName.equals(schedule.getUser().getTeam().getTeamName())){
                    schedulesManager.add(schedule);
                }
            }
            Mockito.when(scheduleRepository.findSchedulesByTeamName(any())).thenReturn(schedulesManager);
        }
        if(role.equals("CEO")) {
            Mockito.when(scheduleRepository.findSchedulesWithName()).thenReturn(schedules);
        }

        // when
        ScheduleResponse.ScheduleListDTO scheduleListDTO = scheduleService.getScheduleListForManage(userId, role, teamName);

        // Then
        if(role.equals("CEO")) {
            assertThat(scheduleListDTO).isNotNull();
            assertThat(scheduleListDTO.getScheduleList()).isNotNull();
            assertThat(scheduleListDTO.getScheduleList().size()).isEqualTo(schedules.size());
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

        if(role.equals("MANAGER")){
            assertThat(scheduleListDTO).isNotNull();
            assertThat(scheduleListDTO.getScheduleList()).isNotNull();
            assertThat(scheduleListDTO.getScheduleList().size()).isEqualTo(schedulesManager.size());
            assertThat(scheduleListDTO.getScheduleList().get(0).getUser().getUserId()).isEqualTo(schedulesManager.get(0).getUser().getId());
            assertThat(scheduleListDTO.getScheduleList().get(0).getScheduleId()).isEqualTo(schedulesManager.get(0).getId());
            assertThat(scheduleListDTO.getScheduleList().get(0).getType()).isEqualTo(schedulesManager.get(0).getType());
            assertThat(scheduleListDTO.getScheduleList().get(0).getReason()).isEqualTo(schedulesManager.get(0).getReason());
            assertThat(scheduleListDTO.getScheduleList().get(0).getEndDate()).isEqualTo(schedulesManager.get(0).getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            assertThat(scheduleListDTO.getScheduleList().get(0).getStatus()).isEqualTo(schedulesManager.get(0).getStatus());
            assertThat(scheduleListDTO.getScheduleList().get(1).getUser().getUserId()).isEqualTo(schedulesManager.get(1).getUser().getId());
            assertThat(scheduleListDTO.getScheduleList().get(1).getUser().getName()).isEqualTo(schedulesManager.get(1).getUser().getName());
            assertThat(scheduleListDTO.getScheduleList().get(1).getUser().getEmail()).isEqualTo(schedulesManager.get(1).getUser().getEmail());
            assertThat(scheduleListDTO.getScheduleList().get(1).getUser().getPhoneNumber()).isEqualTo(schedulesManager.get(1).getUser().getPhoneNumber());
            assertThat(scheduleListDTO.getScheduleList().get(2).getUser().getRole()).isEqualTo(schedulesManager.get(2).getUser().getRole());
            assertThat(scheduleListDTO.getScheduleList().get(2).getUser().getTeamName()).isEqualTo(schedulesManager.get(2).getUser().getTeam().getTeamName());
            assertThat(scheduleListDTO.getScheduleList().get(2).getUser().getStartWork()).isEqualTo(schedulesManager.get(2).getUser().getStartWork().format(DateTimeFormatter.ISO_LOCAL_DATE));
            assertThat(scheduleListDTO.getScheduleList().get(2).getUser().getProfileImage()).isEqualTo(schedulesManager.get(2).getUser().getProfileImage());
        }
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
}
