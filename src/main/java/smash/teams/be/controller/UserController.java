package smash.teams.be.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginInDTO loginInDTO, Errors errors, HttpServletRequest request) {
        UserResponse.LoginOutDTO loginOutDTO = userService.login(loginInDTO);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(loginOutDTO.getLoginInfoOutDTO());
        return ResponseEntity.ok().header(JwtProvider.HEADER, loginOutDTO.getJwt()).body(responseDTO);
    }


    @Log
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinInDTO joinInDTO, Errors errors) {
        UserResponse.JoinOutDTO joinOutDTO = userService.join(joinInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/auth/user")
    public ResponseEntity<?> findMyInfo(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.FindMyInfoOutDTO findMyInfoOutDTO = userService.findMyId(myUserDetails.getUser().getId());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(findMyInfoOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @Log
    @PostMapping("/auth/user/{id}/upload")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody UserRequest.UpdateInDTO updateInDTO,
                                    @AuthenticationPrincipal MyUserDetails myUserDetails) throws JsonProcessingException {
        if (updateInDTO.getCurPassword().isBlank()) {
            throw new Exception400(String.valueOf(id), "현재 비밀번호를 입력해야합니다.");
        }

        if (!updateInDTO.getNewPassword().isBlank()
                && !updateInDTO.getNewPassword().matches("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,20}$")) {
            throw new Exception400(String.valueOf(id), "영문, 숫자, 특수문자를 각각 1개 이상 사용하여 8~20자 이내로 작성해주세요.");
        }

        if (!updateInDTO.getPhoneNumber().isBlank()
                && !updateInDTO.getPhoneNumber().matches("^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})$")) {
            throw new Exception400(String.valueOf(id), "휴대폰 번호(010-1234-5678)의 형태로 작성해주세요.");
        }

        if (!updateInDTO.getStartWork().isBlank()
                && !updateInDTO.getStartWork().matches("^(?:(?:19|20)\\d{2})-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[12][0-9]|3[01])$")) {
            throw new Exception400(String.valueOf(id), "입사일(2023-05-10)의 형태로 작성해주세요.");
        }

        if (id.longValue() != myUserDetails.getUser().getId()) {
            throw new Exception403("권한이 없습니다.");
        }

        UserResponse.UpdateOutDTO updateOutDTO = userService.update(myUserDetails.getUser().getId(), updateInDTO);
        System.out.println(new ObjectMapper().writeValueAsString(updateOutDTO));
        ResponseDTO<?> responseDTO = new ResponseDTO<>(updateOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

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
        UserResponse.UpdateImageOutDTO uploadImageOutDTO = new UserResponse.UpdateImageOutDTO(userPS);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(uploadImageOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/join/check")
    public ResponseEntity<?> checkDuplicateEmail(@RequestBody @Valid UserRequest.CheckInDTO checkInDTO) {
        boolean isDuplicated = userService.checkDuplicateEmail(checkInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(isDuplicated);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/auth/user/{id}/delete")
    public ResponseEntity<?> withdraw(@PathVariable Long id,
                                      @RequestBody @Valid UserRequest.WithdrawInDTO withdrawInDTO,
                                      @AuthenticationPrincipal MyUserDetails myUserDetails) {

        if(id.longValue() != myUserDetails.getUser().getId()){
            throw new Exception403("권한이 없습니다");
        }

        userService.withdraw(withdrawInDTO,id);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(null);
        return ResponseEntity.ok().body(responseDTO);
    }
}
