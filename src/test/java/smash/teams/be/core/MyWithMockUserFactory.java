package smash.teams.be.core;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import smash.teams.be.core.auth.session.MyUserDetails;
import smash.teams.be.model.user.User;

import java.time.LocalDateTime;

public class MyWithMockUserFactory implements WithSecurityContextFactory<MyWithMockUser> {
    @Override
    public SecurityContext createSecurityContext(MyWithMockUser mockUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        User user = User.builder()
                .id(mockUser.id())
                .name(mockUser.name())
                .password("1234")
                .email(mockUser.name()+"@gmail.com")
                .phoneNumber("010-" + mockUser.name())
                .profileImage("프로필 사진 : " + mockUser.name())
                .role(mockUser.role())
                .status(mockUser.status())
                .remain(20)
                .startWork(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        MyUserDetails userDetails = new MyUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}