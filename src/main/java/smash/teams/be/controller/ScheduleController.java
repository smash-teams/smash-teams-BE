package smash.teams.be.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import smash.teams.be.core.annotation.Log;
import smash.teams.be.core.auth.session.MyUserDetails;
import smash.teams.be.dto.ResponseDTO;
import smash.teams.be.dto.schedule.ScheduleRequest;
import smash.teams.be.dto.schedule.ScheduleResponse;
import smash.teams.be.model.schedule.Schedule;
import smash.teams.be.model.schedule.Status;
import smash.teams.be.model.user.User;
import smash.teams.be.service.ScheduleService;

import javax.validation.Valid;

import static smash.teams.be.dto.schedule.ScheduleRequest.MakeScheduleRequestInDTO;

@RequiredArgsConstructor
@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Log
    @GetMapping("/auth/user/schedule")
    public ResponseEntity<?> getScheduleList(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        Long userId = myUserDetails.getUser().getId();
        ScheduleResponse.ScheduleListDTO scheduleListDTO = scheduleService.getScheduleList(userId);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(scheduleListDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/auth/super/schedule")
    public ResponseEntity<?> getScheduleListForManage(@AuthenticationPrincipal MyUserDetails myUserDetails){

        User user = myUserDetails.getUser();

        ScheduleResponse.ScheduleListDTO scheduleListDTO = scheduleService.getScheduleListForManage(user);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(scheduleListDTO);
        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("/auth/user/main")
    public ResponseEntity<?> loadScheduleList() throws JsonProcessingException {
        ScheduleResponse.ListOutDto listOutDto = scheduleService.findByScheduleList();
        System.out.println(new ObjectMapper().writeValueAsString(listOutDto));
        ResponseDTO<?> responseDTO = new ResponseDTO<>(listOutDto);
        return ResponseEntity.ok(responseDTO);
    }

    @Log
    @PostMapping("/auth/user/schedule")
    public ResponseEntity<?> makeScheduleRequest(@RequestBody @Valid MakeScheduleRequestInDTO makeScheduleRequestInDTO,
                                                 Errors errors,
                                                 @AuthenticationPrincipal MyUserDetails myUserDetails) {
        // check - id 대조
        scheduleService.makeScheduleRequest(makeScheduleRequestInDTO, myUserDetails.getUser().getId());

        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/auth/super/schedule/order")
    public ResponseEntity<?> orderSchedule(@RequestBody @Valid ScheduleRequest.OrderScheduleInDTO orderScheduleInDTO,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails) {

        ScheduleResponse.OrderScheduleOutWithRemainDTO orderScheduleOutDTO = scheduleService.orderSchedule(orderScheduleInDTO);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(orderScheduleOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

}
