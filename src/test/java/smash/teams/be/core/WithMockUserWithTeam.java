package smash.teams.be.core;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserWithTeamFactory.class)
public @interface WithMockUserWithTeam {
    long id() default 3L;
    String username() default "kimuser@gmail.com";
    String role() default "USER";
    long teamId() default 2L;
    String teamName() default "개발팀";
}
