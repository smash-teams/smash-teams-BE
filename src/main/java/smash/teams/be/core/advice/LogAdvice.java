package smash.teams.be.core.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class LogAdvice {

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
            }
        }
    }
}