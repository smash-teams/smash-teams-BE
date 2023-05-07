package smash.teams.be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smash.teams.be.core.annotation.Log;
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

    @Log
    @Transactional(readOnly = true)
    public ScheduleResponse.ScheduleListDTO getScheduleList(Long userId) {
        List<Schedule> schedules = scheduleRepository.findSchedulesByUserId(userId);
        if (schedules.isEmpty()) {
            throw new Exception404("스케쥴을 찾을 수 없습니다.");
        }
        List<ScheduleResponse.ScheduleOutDTO> scheduleOutDTOList = new ArrayList<>();
        for (Schedule schedule : schedules) {
            ScheduleResponse.UserOutDTOWithScheduleOutDTO userOutDTOWithScheduleOutDTO = new ScheduleResponse.UserOutDTOWithScheduleOutDTO(schedule.getUser());
            scheduleOutDTOList.add(new ScheduleResponse.ScheduleOutDTO(schedule, userOutDTOWithScheduleOutDTO));
        }

        return new ScheduleResponse.ScheduleListDTO(scheduleOutDTOList);
    }

    public ScheduleResponse.ScheduleListDTO getScheduleListForManage(Long userId, String role, String teamName) {
        if (role.equals("CEO")) {
            List<Schedule> schedules = scheduleRepository.findSchedules();
            if (schedules.isEmpty()) {
                throw new Exception404("스케쥴을 찾을 수 없습니다.");
            }

            List<ScheduleResponse.ScheduleOutDTO> scheduleOutDTOList = new ArrayList<>();
            for (Schedule schedule : schedules) {
                ScheduleResponse.UserOutDTOWithScheduleOutDTO userOutDTOWithScheduleOutDTO = new ScheduleResponse.UserOutDTOWithScheduleOutDTO(schedule.getUser());
                scheduleOutDTOList.add(new ScheduleResponse.ScheduleOutDTO(schedule, userOutDTOWithScheduleOutDTO));
            }

            return new ScheduleResponse.ScheduleListDTO(scheduleOutDTOList);


        }

        if (role.equals("MANAGER")) {
            List<Schedule> schedules = scheduleRepository.findSchedulesByTeamName(teamName);
            if (schedules.isEmpty()) {
                throw new Exception404("스케쥴을 찾을 수 없습니다.");
            }

            List<ScheduleResponse.ScheduleOutDTO> scheduleOutDTOList = new ArrayList<>();
            for (Schedule schedule : schedules) {
                ScheduleResponse.UserOutDTOWithScheduleOutDTO userOutDTOWithScheduleOutDTO = new ScheduleResponse.UserOutDTOWithScheduleOutDTO(schedule.getUser());
                scheduleOutDTOList.add(new ScheduleResponse.ScheduleOutDTO(schedule, userOutDTOWithScheduleOutDTO));
            }

            return new ScheduleResponse.ScheduleListDTO(scheduleOutDTOList);
        }

        return null;
    }
}
