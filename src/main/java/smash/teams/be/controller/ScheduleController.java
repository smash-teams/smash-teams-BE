package smash.teams.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import smash.teams.be.core.annotation.Log;
import smash.teams.be.core.auth.session.MyUserDetails;
import smash.teams.be.dto.ResponseDTO;
import smash.teams.be.dto.schedule.ScheduleResponse;
import smash.teams.be.service.ScheduleService;

@RequiredArgsConstructor
@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Log
    @GetMapping("/auth/user/schedule")
    public ResponseEntity<?> getScheduleList(@AuthenticationPrincipal MyUserDetails myUserDetails){
        Long userId = myUserDetails.getUser().getId();
        ScheduleResponse.ScheduleListDTO scheduleListDTO = scheduleService.getScheduleList(userId);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(scheduleListDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/auth/suser/schedule")
    public ResponseEntity<?> getScheduleListForManage(@AuthenticationPrincipal MyUserDetails myUserDetails){
        Long userId = myUserDetails.getUser().getId();
        String role = myUserDetails.getUser().getRole();
        String teamName = myUserDetails.getUser().getTeam().getTeamName();

        ScheduleResponse.ScheduleListDTO scheduleListDTO = scheduleService.getScheduleListForManage(userId, role, teamName);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(scheduleListDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
