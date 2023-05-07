package smash.teams.be.core.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import smash.teams.be.core.auth.session.MyUserDetails;
import smash.teams.be.model.errorLog.ErrorLog;
import smash.teams.be.model.errorLog.ErrorLogRepository;

import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class LogAdvice {

    private final ErrorLogRepository errorLogRepository;

    @Pointcut("@annotation(smash.teams.be.core.annotation.Log)")
    public void log() {
    }

    @Pointcut("@annotation(smash.teams.be.core.annotation.ErrorLog)")
    public void errorLog() {
    }

    @AfterReturning("log()")
    public void logAdvice(JoinPoint jp) throws Exception {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        log.debug("디버그 : " + method.getName() + " 성공");
    }

    @Before("errorLog()")
    public void errorLogAdvice(JoinPoint jp) throws Exception {
        Object[] args = jp.getArgs();

        for (Object arg : args) {
            if (arg instanceof Exception) {
                Exception e = (Exception) arg;
                log.error("에러 : " + e.getMessage());
                errorLogRepository.save(ErrorLog.builder()
                        .msg(e.getMessage())
                        .userId(getUserId())
                        .build());
            }
        }
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof MyUserDetails) {
            return ((MyUserDetails) principal).getUser().getId();
        }

        return null;
    }
}