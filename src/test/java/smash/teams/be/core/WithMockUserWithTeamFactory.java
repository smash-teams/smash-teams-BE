package smash.teams.be.core;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import smash.teams.be.core.auth.session.MyUserDetails;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.user.Status;
import smash.teams.be.model.user.User;

import java.time.LocalDateTime;

public class WithMockUserWithTeamFactory implements WithSecurityContextFactory<WithMockUserWithTeam> {
    @Override
    public SecurityContext createSecurityContext(WithMockUserWithTeam mockUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        User user = User.builder()
                .id(mockUser.id())
                .name(mockUser.username().replace("@gmail",""))
                .password("1234")
                .email(mockUser.username())
                .phoneNumber("010-1234-5678")
                .remain(20)
                .role(mockUser.role())
                .status(Status.ACTIVE.getStatus())
                .startWork(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .team(Team.builder()
                        .id(mockUser.teamId())
                        .teamName(mockUser.teamName()).build())
                .build();
        MyUserDetails myUserDetails = new MyUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(myUserDetails, myUserDetails.getPassword(), myUserDetails.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
