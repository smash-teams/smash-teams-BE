package smash.teams.be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smash.teams.be.core.exception.Exception404;
import smash.teams.be.dto.schedule.ScheduleResponse;
import smash.teams.be.model.schedule.Schedule;
import smash.teams.be.model.schedule.ScheduleRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleResponse.ScheduleListDTO getScheduleList(Long userId){
        List<Schedule> schedules = scheduleRepository.findSchedulesByUserId(userId);
        if(schedules.isEmpty()){
            throw new Exception404("스케쥴을 찾을 수 없습니다.");
        }
        List<ScheduleResponse.ScheduleOutDTO> scheduleList = new ArrayList<>();
        for(Schedule schedule : schedules){
            ScheduleResponse.UserOutWithScheduleOutDTO userOutWithScheduleDTO = new ScheduleResponse.UserOutWithScheduleOutDTO(schedule.getUser());
            scheduleList.add(new ScheduleResponse.ScheduleOutDTO(schedule, userOutWithScheduleDTO));
        }

        return new ScheduleResponse.ScheduleListDTO(scheduleList);
    }
}
