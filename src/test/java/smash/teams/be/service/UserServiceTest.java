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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import smash.teams.be.core.auth.jwt.JwtProvider;
import smash.teams.be.core.auth.session.MyUserDetails;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.core.util.FileUtil;
import smash.teams.be.dto.user.UserResponse;
import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static smash.teams.be.core.auth.jwt.JwtProvider.verify;
import static smash.teams.be.dto.user.UserRequest.LoginInDTO;
import static smash.teams.be.dto.user.UserRequest.UpdateInDTO;

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
    public void login_test() throws Exception {
        // given
        LoginInDTO loginInDTO = new LoginInDTO();
        loginInDTO.setEmail("seungmin@gmail.com");
        loginInDTO.setPassword("dltmdals123!");

        // stub
        User user = newMockUserWithTeam(1L, "seungmin", newMockTeam(1L, "개발팀"));
        MyUserDetails myUserDetails = new MyUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                myUserDetails, myUserDetails.getPassword(), myUserDetails.getAuthorities()
        );
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // when
        UserResponse.LoginOutDTO loginOutDTO = userService.login(loginInDTO);
        System.out.println(om.writeValueAsString(loginOutDTO));

        // then
        Assertions.assertThat(loginOutDTO.getLoginInfoOutDTO().getName()).isEqualTo("seungmin");
        Assertions.assertThat(loginOutDTO.getLoginInfoOutDTO().getEmail()).isEqualTo("seungmin@gmail.com");
        Assertions.assertThat(loginOutDTO.getLoginInfoOutDTO().getPhoneNumber()).isEqualTo("010-1234-5678");
        Assertions.assertThat(loginOutDTO.getLoginInfoOutDTO().getProfileImage()).isNull();
        Assertions.assertThat(loginOutDTO.getLoginInfoOutDTO().getStartWork()).isEqualTo(LocalDateTime.now().toLocalDate().toString());
        Assertions.assertThat(loginOutDTO.getLoginInfoOutDTO().getRemain()).isEqualTo(20);
        Assertions.assertThat(loginOutDTO.getLoginInfoOutDTO().getTeamName()).isEqualTo("개발팀");
        Assertions.assertThat(loginOutDTO.getLoginInfoOutDTO().getRole()).isEqualTo(Role.USER.getRole());
        Assertions.assertThat(loginOutDTO.getJwt().startsWith(JwtProvider.TOKEN_PREFIX)).isTrue();
    }

    @Test
    public void findMyId_test() throws Exception {
        // given
        Long id = 1L;

        // stub
        User cos = newMockUser(1L, "cos");
        when(userRepository.findById(any())).thenReturn(Optional.of(cos));

        // when
        UserResponse.FindMyInfoOutDTO findMyInfoOutDTO = userService.findMyId(id);
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

        UpdateInDTO updateInDTO = new UpdateInDTO();
        updateInDTO.setCurPassword("dltmdals123!");
        updateInDTO.setNewPassword("dltmdals1234!");
        updateInDTO.setPhoneNumber("010-8765-4321");
        updateInDTO.setStartWork("2023-05-10");
        updateInDTO.setProfileImage("사진 33"); // request

        // stub
        User ssar = newMockUserUpdate(1L, "ssar"); // DB
        userRepository.save(ssar);
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(ssar));

        String requestBody = om.writeValueAsString(updateInDTO);
        System.out.println("테스트1 : " + requestBody);

        // when
        UserResponse.UpdateOutDTO updateOutDTO = userService.update(id, updateInDTO);
        String responseBody = om.writeValueAsString(updateOutDTO);
        System.out.println("테스트2 : " + responseBody);

        // then
        Assertions.assertThat(updateOutDTO.getPhoneNumber()).isEqualTo("010-8765-4321");
        Assertions.assertThat(updateOutDTO.getProfileImage()).isEqualTo("사진 33");
    }

//    @Test
//    public void uploadImage_test() {
//        // given
//        Long id = 1L;
//
//        User userPS = newMockImage(1L, "ssar");
//        byte[] fileContent = "".getBytes();
//        MultipartFile profileImage = new MockMultipartFile("profileImage",
//                "person.png", "multipart/form-data", fileContent);
//
//        when(userRepository.findById(id)).thenReturn(Optional.of(userPS));
//
//        // when
//        User result = userService.uploadImage(profileImage, id);
//        System.out.println(result.getProfileImage());
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getId()).isEqualTo(1L);
//        assertThat(result.getProfileImage()).isNotNull();
//    }
}
