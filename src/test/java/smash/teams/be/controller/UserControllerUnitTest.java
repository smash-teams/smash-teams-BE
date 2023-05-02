//package smash.teams.be.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.EnableAspectJAutoProxy;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import smash.teams.be.core.advice.LogAdvice;
//import smash.teams.be.core.advice.ValidAdvice;
//import smash.teams.be.core.config.FilterRegisterConfig;
//import smash.teams.be.core.config.SecurityConfig;
//
///**
// * @WebMvcTest는 웹 계층 컴포넌트만 테스트로 가져옴
// */
//
//@ActiveProfiles("test")
//@EnableAspectJAutoProxy // AOP 활성화
//@Import({
//        ValidAdvice.class,
//        LogAdvice.class,
//        SecurityConfig.class,
//        FilterRegisterConfig.class
//}) // Advice 와 Security 설정 가져오기
//@WebMvcTest(
//        // 필요한 Controller 가져오기, 특정 필터를 제외하기
//        controllers = {UserController.class}
//)
//public class UserControllerUnitTest {
//    @Autowired
//    private MockMvc mvc;
//    @Autowired
//    private ObjectMapper om;
////    @MockBean
////    private UserService userService;
//}
