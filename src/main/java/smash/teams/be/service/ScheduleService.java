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
        List<Schedule> scheduleList = scheduleRepository.findSchedulesByUserId(userId);
        if(scheduleList.isEmpty()){
            throw new Exception404("스케쥴을 찾을 수 없습니다.");
        }
        List<ScheduleResponse.ScheduleOutDTO> scheduleOutDTOList = new ArrayList<>();
        for(Schedule schedule : scheduleList){
            ScheduleResponse.UserOutDTOWithScheduleOutDTO userOutDTOWithScheduleOutDTO = new ScheduleResponse.UserOutDTOWithScheduleOutDTO(schedule.getUser());
            scheduleOutDTOList.add(new ScheduleResponse.ScheduleOutDTO(schedule, userOutDTOWithScheduleOutDTO));
        }

        return new ScheduleResponse.ScheduleListDTO(scheduleOutDTOList);
    }
}
