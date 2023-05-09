package smash.teams.be.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.dto.user.UserRequest;
import smash.teams.be.dto.user.UserResponse;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest extends DummyEntity {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Spy
    private ObjectMapper om;

    @Test
    public void findMyId_test() throws Exception {
        // given
        Long id = 1L;

        // stub
        User cos = newMockUser(1L, "cos");
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(cos));

        // when
        UserResponse.findMyInfoOutDTO findMyInfoOutDTO = userService.findMyId(id);
        String responseBody = om.writeValueAsString(findMyInfoOutDTO);
        System.out.println("테스트2 : " + responseBody);

        // then
        Assertions.assertThat(findMyInfoOutDTO.getId()).isEqualTo(1L);
        Assertions.assertThat(findMyInfoOutDTO.getName()).isEqualTo("cos");
        Assertions.assertThat(findMyInfoOutDTO.getEmail()).isEqualTo("cos@gmail.com");
    }

    @Test
    public void update_test() throws Exception {
        // given
        Long id = 1L;

        UserRequest.UpdateInDto updateInDto = new UserRequest.UpdateInDto();
        updateInDto.setCurPassword("1234");
        updateInDto.setNewPassword("5678");
        updateInDto.setPhoneNumber("010-8765-4321");
        updateInDto.setStartWork("2023-05-10");
        updateInDto.setProfileImage("사진 33"); // request

        // stub
        User ssar = newMockUserUpdate(1L, "ssar"); // DB
        userRepository.save(ssar);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.ofNullable(ssar));

        String requestBody = om.writeValueAsString(updateInDto);
        System.out.println("테스트1 : " + requestBody);

        // when
        UserResponse.UpdateOutDTO updateOutDTO = userService.update(id, updateInDto);
        String responseBody = om.writeValueAsString(updateOutDTO);
        System.out.println("테스트2 : " + responseBody);

        // then
        Assertions.assertThat(updateOutDTO.getPhoneNumber()).isEqualTo("010-8765-4321");
        Assertions.assertThat(updateOutDTO.getProfileImage()).isEqualTo("사진 33");
    }
}
