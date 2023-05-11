package smash.teams.be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import smash.teams.be.core.annotation.Log;

import smash.teams.be.core.auth.jwt.JwtProvider;


import smash.teams.be.core.auth.session.MyUserDetails;
import smash.teams.be.core.exception.Exception400;


import smash.teams.be.core.exception.Exception401;


import smash.teams.be.core.exception.Exception403;

import smash.teams.be.core.exception.Exception404;

import smash.teams.be.core.exception.Exception500;
import smash.teams.be.core.util.FileUtil;
import smash.teams.be.dto.user.UserRequest;
import smash.teams.be.dto.user.UserResponse;

import smash.teams.be.model.team.Team;

import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.Status;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;


import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Value("${file.path}")
    private String uploadFolder;

    @Log
    public UserResponse.LoginOutDTO login(UserRequest.LoginInDTO loginInDTO) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginInDTO.getEmail(), loginInDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
            String jwt = JwtProvider.create(myUserDetails.getUser());

            return new UserResponse.LoginOutDTO(new UserResponse.LoginInfoOutDTO(myUserDetails.getUser()), jwt);
        } catch (Exception e) {
            throw new Exception401("인증되지 않았습니다.");
        }
    }

    @Log
    @Transactional
    public UserResponse.JoinOutDTO join(UserRequest.JoinInDTO joinInDTO) {
        Optional<User> userOP = userRepository.findByName(joinInDTO.getName());
        Team teamOP = teamRepository.findTeamByTeamName(joinInDTO.getTeamName());

        if (userOP.isPresent()) {
            // 이 부분이 try catch 안에 있으면 Exception500에게 제어권을 뺏긴다.
            throw new Exception400("name", "이름이 존재합니다");
        }

        if (teamOP == null) {
            throw new Exception404(joinInDTO.getTeamName() + "이 존재하지 않습니다");
        }

        String encPassword = bCryptPasswordEncoder.encode(joinInDTO.getPassword()); // 60Byte
        joinInDTO.setPassword(encPassword);
        System.out.println("encPassword : " + encPassword);

        // 디비 save 되는 쪽만 try catch로 처리하자.
        try {
            User userPS = userRepository.save(joinInDTO.toEntity(teamOP));
            return new UserResponse.JoinOutDTO(userPS);
        } catch (Exception e) {
            throw new Exception500("회원가입 실패 : " + e.getMessage());
        }
    }

    @Log
    @Transactional(readOnly = true)
    public UserResponse.FindMyInfoOutDTO findMyId(Long id) {
        try {
            User userPS = userRepository.findById(id).orElseThrow(
                    () -> new Exception400("id", "해당 유저를 찾을 수 없습니다")
            );
            return new UserResponse.FindMyInfoOutDTO(userPS);
        } catch (Exception e) {
            throw new Exception500("내 정보 조회 실패 : " + e.getMessage());
        }
    }

    @Log
    @Transactional
    public UserResponse.UpdateOutDTO update(Long userId, UserRequest.UpdateInDTO updateInDTO) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception400("id", "해당 유저를 찾을 수 없습니다.")
        );

        if (!bCryptPasswordEncoder.matches(updateInDTO.getCurPassword(), userPS.getPassword())) {
            throw new Exception400("password", "비밀번호가 일치하지 않습니다.");
        }

        try {
            User userEntity = updateInDTO.toEntity();

            String rawPassword = userEntity.getPassword();
            String encPassword = bCryptPasswordEncoder.encode(rawPassword);

            userPS.updatePassword(encPassword);
            userPS.updatePhoneNumber(userEntity.getPhoneNumber());
            userPS.updateStartWork(userEntity.getStartWork());
            userPS.updateProfileImage(userEntity.getProfileImage());

            userRepository.save(userPS);
            return new UserResponse.UpdateOutDTO(userPS);
        } catch (Exception e) {
            throw new Exception500("유저 변경 실패 : " + e.getMessage());
        }
    }


    @Log
    @Transactional
    public User uploadImage(MultipartFile profileImage, Long id) {
        try {
            String uuidImageName = FileUtil.write(uploadFolder, profileImage);

            User userPS = userRepository.findById(id)
                    .orElseThrow(() -> new Exception500("로그인 된 유저가 DB에 존재하지 않습니다."));
            userPS.uploadImage(uuidImageName);
            return userPS;
        } catch (Exception e) {
            throw new Exception500("프로필 사진 등록 실패 : " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public boolean checkDuplicateEmail(UserRequest.CheckInDTO checkInDTO) {
        Optional<User> userPS = userRepository.findByEmail(checkInDTO.getEmail());

        if (userPS.isPresent()) {
            if (userPS.get().getEmail().equals(checkInDTO.getEmail()))
                return true;
        }

        return false;
    }

    @Transactional
    public void withdraw(UserRequest.CancelUserInDTO cancelUserInDTO, Long id) {

        User userOP = userRepository.findUserById(id);
        if (bCryptPasswordEncoder.matches(cancelUserInDTO.getPassword(), userOP.getPassword())) {
            try {
                userOP.changeStatus(Status.INACTIVE.getStatus());
                userRepository.save(userOP);
            } catch (Exception e) {
                throw new Exception500("탈퇴 실패 : " + e.getMessage());
            }
        }else{
            throw new Exception403("비밀번호가 맞지 않습니다");
        }
    }
}

