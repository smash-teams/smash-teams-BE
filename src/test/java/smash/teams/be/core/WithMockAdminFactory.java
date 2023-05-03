package smash.teams.be.core;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import smash.teams.be.core.auth.session.MyUserDetails;
import smash.teams.be.model.user.Role;
import smash.teams.be.model.user.Status;
import smash.teams.be.model.user.User;

import java.time.LocalDateTime;

public class WithMockAdminFactory implements WithSecurityContextFactory<WithMockAdmin> {
    @Override
    public SecurityContext createSecurityContext(WithMockAdmin mockAdmin) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        User user = User.builder()
                .id(mockAdmin.id())
                .name("admin")
                .password("1234")
                .email(mockAdmin.username())
                .phoneNumber("010-1234-5678")
                .remain(20)
                .role(Role.ADMIN.getRole())
                .status(Status.ACTIVE.getStatus())
                .startWork(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        MyUserDetails myUserDetails = new MyUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(myUserDetails, myUserDetails.getPassword(), myUserDetails.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
