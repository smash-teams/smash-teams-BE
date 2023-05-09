package smash.teams.be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smash.teams.be.core.annotation.Log;
import smash.teams.be.core.exception.Exception400;
import smash.teams.be.core.exception.Exception500;
import smash.teams.be.dto.user.UserRequest;
import smash.teams.be.dto.user.UserResponse;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserResponse.findMyInfoOutDTO findMyId(Long id) {
        User userPS = userRepository.findById(id).orElseThrow(
                () -> new Exception400("id", "해당 유저를 찾을 수 없습니다")
        );
        return new UserResponse.findMyInfoOutDTO(userPS);
    }

    @Log
    @Transactional
    public UserResponse.UpdateOutDTO update(Long userId, UserRequest.UpdateInDto updateInDto) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception400("id", "해당 유저를 찾을 수 없습니다.")
        );

        if (!bCryptPasswordEncoder.matches(updateInDto.getCurPassword(), userPS.getPassword())) {
            throw new Exception400("password", "비밀번호가 일치하지 않습니다.");
        } // 비밀번호 일치

        try {
            User userEntity = updateInDto.toEntity();

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
}
