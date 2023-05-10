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
import smash.teams.be.model.schedule.*;
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

    @Transactional(readOnly = true)
    public ScheduleResponse.ScheduleListDTO getScheduleListForManage(User user) {

        if(user.getRole().equals(Role.USER.getRole()) || user.getRole().equals(Role.ADMIN.getRole())){
            throw new Exception403("권한이 없습니다");
        }

        if (user.getRole().equals(Role.CEO.getRole())) {

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

        if (user.getRole().equals(Role.MANAGER.getRole())) {
            List<Schedule> schedules = scheduleRepository.findSchedulesByTeamId(user.getTeam().getId());

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

    @Transactional(readOnly = true)
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

    @Transactional
    public ScheduleResponse.OrderScheduleOutWithRemainDTO orderSchedule(User user, ScheduleRequest.OrderScheduleInDTO orderScheduleInDTO) {
        Long scheduleId = orderScheduleInDTO.getScheduleId();
        String status = orderScheduleInDTO.getStatus();

        Schedule schedulePS = scheduleRepository.findScheduleByScheduleId(scheduleId);

        if (schedulePS == null) {
            throw new Exception404("스케쥴을 찾을 수 없습니다.");
        }

        if(user.getRole().equals(Role.MANAGER.getRole())){
            if (status.equals(Status.APPROVED.getStatus())) {
                if (schedulePS.getType().equals(Type.DAYOFF.getType())) {
                    if (schedulePS.getStatus().equals(Status.FIRST.getStatus())) {
                        try {
                            schedulePS.changeStatus(Status.LAST.getStatus());
                            Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                            return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                        } catch (Exception e) {
                            throw new Exception500("승인 실패 : " + e.getMessage());
                        }
                    }
                    if(schedulePS.getStatus().equals(Status.LAST.getStatus())){
                        throw new Exception403("권한이 없습니다");
                    }
                    if(schedulePS.getStatus().equals(Status.APPROVED.getStatus()) || schedulePS.getStatus().equals(Status.REJECTED.getStatus())){
                        throw new Exception400("잘못된 요청","이미 최종승인되었거나 거절된 스케쥴입니다");
                    }
                }
                if (schedulePS.getType().equals(Type.HALFOFF.getType())) {
                    if (schedulePS.getStatus().equals(Status.FIRST.getStatus())) {
                        try {
                            schedulePS.changeStatus(Status.LAST.getStatus());
                            Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                            return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                        } catch (Exception e) {
                            throw new Exception500("승인 실패 : " + e.getMessage());
                        }
                    }
                    if(schedulePS.getStatus().equals(Status.LAST.getStatus())){
                        throw new Exception403("권한이 없습니다");
                    }
                    if(schedulePS.getStatus().equals(Status.APPROVED.getStatus()) || schedulePS.getStatus().equals(Status.REJECTED.getStatus())){
                        throw new Exception400("잘못된 요청","이미 최종승인되었거나 거절된 스케쥴입니다");
                    }
                }
                if (schedulePS.getType().equals(Type.SHIFT.getType())) {
                    if (schedulePS.getStatus().equals(Status.FIRST.getStatus())) {
                        try {
                            schedulePS.changeStatus(Status.LAST.getStatus());
                            Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                            return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                        } catch (Exception e) {
                            throw new Exception500("승인 실패 : " + e.getMessage());
                        }
                    }
                    if(schedulePS.getStatus().equals(Status.LAST.getStatus())){
                        throw new Exception403("권한이 없습니다");
                    }
                    if(schedulePS.getStatus().equals(Status.APPROVED.getStatus()) || schedulePS.getStatus().equals(Status.REJECTED.getStatus())){
                        throw new Exception400("잘못된 요청","이미 최종승인되었거나 거절된 스케쥴입니다");
                    }
                }
            }
            if(status.equals(Status.REJECTED.getStatus())){
                if(schedulePS.getStatus().equals(Status.FIRST.getStatus())){
                    try {
                        schedulePS.changeStatus(Status.REJECTED.getStatus());
                        Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                        return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                    }catch (Exception e){
                        throw new Exception500("거절 실패 : "+e.getMessage());
                    }
                }
                if(schedulePS.getStatus().equals(Status.LAST.getStatus())){
                    throw new Exception403("권한이 없습니다");
                }
                if(schedulePS.getStatus().equals(Status.APPROVED.getStatus()) || schedulePS.getStatus().equals(Status.REJECTED.getStatus())){
                    throw new Exception400("잘못된 요청","이미 최종승인되었거나 거절된 스케쥴입니다");
                }
            }
        }

        if(user.getRole().equals(Role.CEO.getRole())){
            if (status.equals(Status.APPROVED.getStatus())) {
                if (schedulePS.getType().equals(Type.DAYOFF.getType())) {
                    if (schedulePS.getStatus().equals(Status.LAST.getStatus())) {
                        try {
                            schedulePS.changeStatus(Status.APPROVED.getStatus());
                            schedulePS.onApprove();
                            schedulePS.getUser().changeRemain(schedulePS.getUser().getRemain()-1);
                            Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                            return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                        } catch (Exception e) {
                            throw new Exception500("승인 실패 : " + e.getMessage());
                        }
                    }
                    if(schedulePS.getStatus().equals(Status.FIRST.getStatus())){
                        throw new Exception403("권한이 없습니다");
                    }
                    if(schedulePS.getStatus().equals(Status.APPROVED.getStatus()) || schedulePS.getStatus().equals(Status.REJECTED.getStatus())){
                        throw new Exception400("잘못된 승인 요청","이미 최종승인되었거나 거절된 스케쥴입니다");
                    }
                }
                if (schedulePS.getType().equals(Type.HALFOFF.getType())) {
                    if (schedulePS.getStatus().equals(Status.LAST.getStatus())) {
                        try {
                            schedulePS.changeStatus(Status.APPROVED.getStatus());
                            schedulePS.onApprove();
                            schedulePS.getUser().changeRemain(schedulePS.getUser().getRemain()-0.5);
                            Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                            return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                        } catch (Exception e) {
                            throw new Exception500("승인 실패 : " + e.getMessage());
                        }
                    }
                    if(schedulePS.getStatus().equals(Status.FIRST.getStatus())){
                        throw new Exception403("권한이 없습니다");
                    }
                    if(schedulePS.getStatus().equals(Status.APPROVED.getStatus()) || schedulePS.getStatus().equals(Status.REJECTED.getStatus())){
                        throw new Exception400("잘못된 승인 요청","이미 최종승인되었거나 거절된 스케쥴입니다");
                    }
                }
                if (schedulePS.getType().equals(Type.SHIFT.getType())) {
                    if (schedulePS.getStatus().equals(Status.LAST.getStatus())) {
                        try {
                            schedulePS.changeStatus(Status.APPROVED.getStatus());
                            schedulePS.onApprove();
                            Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                            return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                        } catch (Exception e) {
                            throw new Exception500("승인 실패 : " + e.getMessage());
                        }
                    }
                    if(schedulePS.getStatus().equals(Status.FIRST.getStatus())){
                        throw new Exception403("권한이 없습니다");
                    }
                    if(schedulePS.getStatus().equals(Status.APPROVED.getStatus()) || schedulePS.getStatus().equals(Status.REJECTED.getStatus())){
                        throw new Exception400("잘못된 승인 요청","이미 최종승인되었거나 거절된 스케쥴입니다");
                    }
                }
            }
            if(status.equals(Status.REJECTED.getStatus())){
                if(schedulePS.getStatus().equals(Status.LAST.getStatus())){
                    try {
                        schedulePS.changeStatus(Status.REJECTED.getStatus());
                        schedulePS.onApprove();
                        Schedule updatedSchedulePS = scheduleRepository.save(schedulePS);
                        return new ScheduleResponse.OrderScheduleOutWithRemainDTO(updatedSchedulePS);
                    }catch (Exception e){
                        throw new Exception500("거절 실패 : "+e.getMessage());
                    }
                }
                if(schedulePS.getStatus().equals(Status.FIRST.getStatus())){
                    throw new Exception403("권한이 없습니다");
                }
                if(schedulePS.getStatus().equals(Status.APPROVED.getStatus()) || schedulePS.getStatus().equals(Status.REJECTED.getStatus())){
                    throw new Exception400("잘못된 승인 요청","이미 최종승인되었거나 거절된 스케쥴입니다");
                }
            }
        }

        throw new Exception403("권한이 없습니다");
    }
}
