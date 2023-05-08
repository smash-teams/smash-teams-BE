package smash.teams.be.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import smash.teams.be.core.auth.session.MyUserDetails;
import smash.teams.be.core.exception.Exception400;
import smash.teams.be.core.exception.Exception403;
import smash.teams.be.dto.ResponseDTO;
import smash.teams.be.dto.user.UserResponse;
import smash.teams.be.service.UserService;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/auth/user/{id}")
    public ResponseEntity<?> findMyInfo(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) throws JsonProcessingException {
        if (id.longValue() != myUserDetails.getUser().getId()) {
            throw new Exception403("권한이 없습니다.");
        }
        UserResponse.findMyInfoOutDTO findMyInfoOutDTO = userService.findMyId(id);
        System.out.println(new ObjectMapper().writeValueAsString(findMyInfoOutDTO));
        ResponseDTO<?> responseDTO = new ResponseDTO<>(findMyInfoOutDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
