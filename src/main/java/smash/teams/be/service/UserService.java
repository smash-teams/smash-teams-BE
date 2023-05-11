package smash.teams.be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import smash.teams.be.core.annotation.Log;
import smash.teams.be.core.auth.jwt.JwtProvider;
import smash.teams.be.core.auth.session.MyUserDetails;
import smash.teams.be.core.exception.Exception400;
import smash.teams.be.core.exception.Exception401;
import smash.teams.be.core.exception.Exception500;
import smash.teams.be.core.util.FileUtil;
import smash.teams.be.dto.user.UserRequest;
import smash.teams.be.dto.user.UserResponse;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;

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
}
