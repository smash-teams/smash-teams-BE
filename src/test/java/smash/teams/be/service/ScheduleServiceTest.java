package smash.teams.be.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
}
