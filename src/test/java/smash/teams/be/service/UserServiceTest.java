package smash.teams.be.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.http.MediaType;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.web.multipart.MultipartFile;
import smash.teams.be.core.auth.jwt.JwtProvider;
import smash.teams.be.core.auth.session.MyUserDetails;

import org.springframework.test.web.servlet.ResultActions;



import smash.teams.be.core.auth.session.MyUserDetails;

import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.core.util.FileUtil;
import smash.teams.be.dto.user.UserRequest;
import smash.teams.be.dto.user.UserResponse;

import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;

import java.time.LocalDateTime;

import smash.teams.be.model.team.Team;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.Status;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static smash.teams.be.core.auth.jwt.JwtProvider.verify;
import static smash.teams.be.dto.user.UserRequest.LoginInDTO;
import static smash.teams.be.dto.user.UserRequest.UpdateInDTO;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest extends DummyEntity {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TeamRepository teamRepository;
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


    @DisplayName("회원가입 성공")
    @Test
    public void join_test() throws Exception {
        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setName("권으뜸");
        joinInDTO.setPassword("1234");
        joinInDTO.setEmail("user7777777@gmail.com");
        joinInDTO.setPhoneNumber("010-1111-1111");
        joinInDTO.setStartWork("2020-05-01");
        joinInDTO.setTeamName("개발팀");
        String requestBody = om.writeValueAsString(joinInDTO);

        Team 개발팀 = Team.builder().teamName("개발팀").id(1L)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        User 권으뜸 = User.builder().id(1L).name("권으뜸").email("user7777777@gmail.com")
                .password(bCryptPasswordEncoder.encode("1234"))
                .status(Status.ACTIVE.getStatus()).team(개발팀)
                .startWork(LocalDate.parse("2020-05-01", DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay())
                .phoneNumber("010-1111-1111").role(Role.USER.getRole()).remain(20).profileImage(null).build();

        // stub 1
        Mockito.when(userRepository.findByName(any())).thenReturn(Optional.empty());
        Mockito.when(teamRepository.findTeamByTeamName(any())).thenReturn(개발팀);

        // stub 2
        Mockito.when(userRepository.save(any())).thenReturn(권으뜸);

        // when
        UserResponse.JoinOutDTO joinOutDTO = userService.join(joinInDTO);

        // then
        Assertions.assertThat(joinOutDTO.getId()).isEqualTo(1L);
        Assertions.assertThat(joinOutDTO.getName()).isEqualTo("권으뜸");
        Assertions.assertThat(joinOutDTO.getEmail()).isEqualTo("user7777777@gmail.com");
    }


    @Test
    public void check_test() throws Exception{
        // given
        UserRequest.CheckInDTO checkInDTO = new UserRequest.CheckInDTO();
        checkInDTO.setEmail("user1234@gmail.com");

        User userPS = User.builder().email("user1234@gmail.com").build();
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(userPS));

        boolean checkOutDTO = userService.checkDuplicateEmail(checkInDTO);

        Assertions.assertThat(checkOutDTO).isNotNull();
        Assertions.assertThat(checkOutDTO).isEqualTo(true);
    }


    @Test
    public void cancelUser_test() throws Exception{
        // given
        UserRequest.WithdrawInDTO withdrawInDTO = new UserRequest.WithdrawInDTO();
        withdrawInDTO.setEmail("user1234@gmail.com");
        withdrawInDTO.setPassword("1234");

        Long id = 7L;

        String loginEmail = "user1234@gmail.com";
        String loginPassword = bCryptPasswordEncoder.encode("1234");
        User userOP = User.builder().email("user1234@gmail.com").password(bCryptPasswordEncoder.encode("1234")).build();

        // stub
        Mockito.when(userRepository.findUserById(any())).thenReturn(userOP);
//        doNothing().when(userRepository).deleteById(any());

        // when
        userService.withdraw(withdrawInDTO,id);

        // then

    }
}
