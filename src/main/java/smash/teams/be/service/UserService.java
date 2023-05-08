package smash.teams.be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smash.teams.be.core.exception.Exception400;
import smash.teams.be.dto.user.UserResponse;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private UserRepository userRepository;

    public UserResponse.findMyInfoOutDTO findMyId(Long id) {
        User userPS = userRepository.findById(id).orElseThrow(
                () -> new Exception400("id", "해당 유저를 찾을 수 없습니다")
        );
        return new UserResponse.findMyInfoOutDTO(userPS);
    }
}
