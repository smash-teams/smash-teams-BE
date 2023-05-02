package smash.teams.be.core;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockAdminFactory.class)
public @interface WithMockAdmin {
    long id() default 1L;

    String username() default "admin@gmail.com";

    String role() default "ADMIN";
}
