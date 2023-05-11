package smash.teams.be.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import smash.teams.be.core.annotation.ErrorLog;
import smash.teams.be.core.annotation.Log;
import smash.teams.be.core.auth.jwt.JwtProvider;
import smash.teams.be.core.auth.session.MyUserDetails;
import smash.teams.be.core.exception.Exception400;
import smash.teams.be.core.exception.Exception403;
import smash.teams.be.dto.ResponseDTO;
import smash.teams.be.dto.user.UserRequest;
import smash.teams.be.dto.user.UserResponse;
import smash.teams.be.model.user.User;
import smash.teams.be.service.UserService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginInDTO loginInDTO, Errors errors) {
        UserResponse.LoginOutDTO loginOutDTO = userService.login(loginInDTO);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(loginOutDTO.getLoginInfoOutDTO());
        return ResponseEntity.ok().header(JwtProvider.HEADER, loginOutDTO.getJwt()).body(responseDTO);
    }

    @ErrorLog
    @Log
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinInDTO joinInDTO, Errors errors) {
        UserResponse.JoinOutDTO joinOutDTO = userService.join(joinInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/auth/user/{id}")
    public ResponseEntity<?> findMyInfo(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) throws JsonProcessingException {
        if (id.longValue() != myUserDetails.getUser().getId()) {
            throw new Exception403("권한이 없습니다.");
        }
        UserResponse.FindMyInfoOutDTO findMyInfoOutDTO = userService.findMyId(id);
        System.out.println(new ObjectMapper().writeValueAsString(findMyInfoOutDTO));
        ResponseDTO<?> responseDTO = new ResponseDTO<>(findMyInfoOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @Log
    @PostMapping("/auth/user/{id}/upload")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody @Valid UserRequest.UpdateInDTO updateInDTO,
                                    Errors errors,
                                    @AuthenticationPrincipal MyUserDetails myUserDetails) throws JsonProcessingException {
        if (id.longValue() != myUserDetails.getUser().getId()) {
            throw new Exception403("권한이 없습니다.");
        }

        UserResponse.UpdateOutDTO updateOutDTO  = userService.update(myUserDetails.getUser().getId(), updateInDTO);
        System.out.println(new ObjectMapper().writeValueAsString(updateOutDTO));
        ResponseDTO<?> responseDTO = new ResponseDTO<>(updateOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

<<<<<<< HEAD
    @Log
    @PostMapping("/auth/user/{id}/image")
    public ResponseEntity<?> uploadImage(@PathVariable Long id,
                                         MultipartFile profileImage,
                                         @AuthenticationPrincipal MyUserDetails myUserDetails) {
        if (id.longValue() != myUserDetails.getUser().getId()) {
            throw new Exception403("권한이 없습니다.");
        }

        if (profileImage.isEmpty()) {
            throw new Exception400("profile", "사진이 전송되지 않았습니다.");
        }

        User userPS = userService.uploadImage(profileImage, id);

        myUserDetails.setUser(userPS); // 동기화

        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);

    @PostMapping("/join/check")
    public ResponseEntity<?> checkDuplicateEmail(@RequestBody UserRequest.CheckInDTO checkInDTO) {
        boolean isDuplicated = userService.checkDuplicateEmail(checkInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(isDuplicated);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/auth/user/{id}/delete")
    public ResponseEntity<?> cancelUser(@PathVariable Long id,
                                        @RequestBody UserRequest.CancelUserInDTO cancelUserInDTO,
                                        @AuthenticationPrincipal MyUserDetails myUserDetails) {

        if(id.longValue() != myUserDetails.getUser().getId()){
            throw new Exception403("권한이 없습니다");
        }

        userService.cancelUser(cancelUserInDTO, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }
}
