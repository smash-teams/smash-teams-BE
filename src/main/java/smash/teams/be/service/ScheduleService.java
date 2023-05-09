package smash.teams.be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smash.teams.be.core.annotation.Log;
import smash.teams.be.core.exception.Exception400;
import smash.teams.be.core.exception.Exception403;
import smash.teams.be.core.exception.Exception400;
import smash.teams.be.core.exception.Exception404;
import smash.teams.be.core.exception.Exception500;
import smash.teams.be.dto.schedule.ScheduleRequest.MakeScheduleRequestInDTO;
import smash.teams.be.dto.schedule.ScheduleResponse;
import smash.teams.be.model.schedule.Schedule;
import smash.teams.be.model.schedule.ScheduleRepository;
import smash.teams.be.model.schedule.Status;
import smash.teams.be.model.user.Role;
import smash.teams.be.dto.schedule.ScheduleRequest;
import smash.teams.be.dto.schedule.ScheduleResponse;
import smash.teams.be.model.schedule.Schedule;
import smash.teams.be.model.schedule.ScheduleRepository;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

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

    public ScheduleResponse.ScheduleListDTO getScheduleListForManage(User user) {
        if(user.getRole().equals("CEO")){
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

        if(user.getRole().equals("MANAGER")){
            List<Schedule> schedules = scheduleRepository.findSchedulesByTeamName(user.getTeam().getTeamName());

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

    public ScheduleResponse.ListOutDto findByScheduleList() {
        List<Schedule> scheduleListPS = scheduleRepository.findSchedulesWithName();
        if (scheduleListPS == null) {
            throw new Exception403("스케줄을 찾을 수 없습니다.");
        }

        return new ScheduleResponse.ListOutDto(scheduleListPS);
    }

    @Log
    @Transactional
    public void makeScheduleRequest(MakeScheduleRequestInDTO makeScheduleRequestInDTO, Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception404("존재하지 않는 사용자입니다.")
        );
        String role = userPS.getRole();
        if (role.equals(Role.ADMIN.getRole())) {
            throw new Exception400(String.valueOf(userId), "ADMIN 계정으로는 승인 요청이 불가능합니다.");
        }
        try {
            if (role.equals(Role.USER.getRole())) {
                scheduleRepository.save(makeScheduleRequestInDTO.toEntity(userPS, Status.FIRST.getStatus()));
            } else if (role.equals(Role.MANAGER.getRole())) {
                scheduleRepository.save(makeScheduleRequestInDTO.toEntity(userPS, Status.LAST.getStatus()));
            } else if (role.equals(Role.CEO.getRole())) {
                scheduleRepository.save(makeScheduleRequestInDTO.toEntity(userPS, Status.APPROVED.getStatus()));
            }
        } catch (Exception e) {
            throw new Exception500("승인 요청 실패 : " + e.getMessage());
        }
    }

    public ScheduleResponse.OrderScheduleOutWithRemainDTO orderSchedule(ScheduleRequest.OrderScheduleInDTO orderScheduleInDTO) {
        Long scheduleId = orderScheduleInDTO.getScheduleId();
        String status = orderScheduleInDTO.getStatus();

        Schedule schedulePS = scheduleRepository.findScheduleByScheduleId(scheduleId);

        if (schedulePS == null) {
            throw new Exception404("스케쥴을 찾을 수 없습니다.");
        }

        if (status.equals("APPROVED")) {
            if (schedulePS.getType().equals("DAYOFF")) {
                if (schedulePS.getStatus().equals("FIRST")) {
                    try {
                        schedulePS.changeStatus("LAST");
                        Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                        return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                    } catch (Exception e) {
                        throw new Exception500("승인 실패 : " + e.getMessage());
                    }
                }
                if (schedulePS.getStatus().equals("LAST")) {
                    try {
                        schedulePS.changeStatus("APPROVED");
                        schedulePS.getUser().changeRemain(schedulePS.getUser().getRemain()-1);
                        Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                        return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                    } catch (Exception e) {
                        throw new Exception500("승인 실패 : " + e.getMessage());
                    }
                }
            }
            if (schedulePS.getType().equals("HALFOFF")) {
                if (schedulePS.getStatus().equals("FIRST")) {
                    try {
                        schedulePS.changeStatus("LAST");
                        Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                        return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                    } catch (Exception e) {
                        throw new Exception500("승인 실패 : " + e.getMessage());
                    }
                }
                if (schedulePS.getStatus().equals("LAST")) {
                    try {
                        schedulePS.changeStatus("APPROVED");
                        schedulePS.getUser().changeRemain(schedulePS.getUser().getRemain()-0.5);
                        Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                        return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                    } catch (Exception e) {
                        throw new Exception500("승인 실패 : " + e.getMessage());
                    }
                }
            }
            if (schedulePS.getType().equals("SHIFT")) {
                if (schedulePS.getStatus().equals("FIRST")) {
                    try {
                        schedulePS.changeStatus("LAST");
                        Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                        return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                    } catch (Exception e) {
                        throw new Exception500("승인 실패 : " + e.getMessage());
                    }
                }
                if (schedulePS.getStatus().equals("LAST")) {
                    try {
                        schedulePS.changeStatus("APPROVED");
                        Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                        return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                    } catch (Exception e) {
                        throw new Exception500("승인 실패 : " + e.getMessage());
                    }
                }
            }
            if(schedulePS.getStatus().equals("APPROVED") || schedulePS.getStatus().equals("REJECTED")){
                throw new Exception400("잘못된 승인 요청","이미 최종승인되었거나 거절된 스케쥴입니다");
            }
        }
        if(status.equals("REJECTED")){
            if(schedulePS.getStatus().equals("FIRST") || schedulePS.getStatus().equals("LAST")){
                try {
                    schedulePS.changeStatus("REJECTED");
                    Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                    return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                }catch (Exception e){
                    throw new Exception500("거절 실패 : "+e.getMessage());
                }
            }
            if(schedulePS.getStatus().equals("APPROVED") || schedulePS.getStatus().equals("REJECTED")){
                throw new Exception400("스케쥴 승인 요청","이미 승인되었거나 거절된 스케쥴입니다");
            }
        }

        return null;
    }

}
