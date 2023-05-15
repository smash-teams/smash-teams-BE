package smash.teams.be.core.advice;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import smash.teams.be.core.annotation.ErrorLog;
import smash.teams.be.core.auth.session.MyUserDetails;
import smash.teams.be.core.exception.*;
import smash.teams.be.dto.ResponseDTO;
import smash.teams.be.model.errorLog.ErrorLogRepository;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final ErrorLogRepository errorLogRepository;

    @ErrorLog
    @ExceptionHandler(Exception400.class)
    public ResponseEntity<?> badRequest(Exception400 e) {
        return new ResponseEntity<>(e.body(), e.status());
    }

    @ErrorLog
    @ExceptionHandler(Exception401.class)
    public ResponseEntity<?> unAuthorized(Exception401 e) {
        return new ResponseEntity<>(e.body(), e.status());
    }

    @ErrorLog
    @ExceptionHandler(Exception403.class)
    public ResponseEntity<?> forbidden(Exception403 e) {
        return new ResponseEntity<>(e.body(), e.status());
    }

    @ErrorLog
    @ExceptionHandler(Exception404.class)
    public ResponseEntity<?> notFound(Exception404 e) {
        return new ResponseEntity<>(e.body(), e.status());
    }

    @ErrorLog
    @ExceptionHandler(Exception500.class)
    public ResponseEntity<?> serverError(Exception500 e) {
        errorLogRepository.save(smash.teams.be.model.errorLog.ErrorLog.builder() // db에 로그 저장
                .msg(e.getMessage())
                .userId(getUserId())
                .build());
        Sentry.captureException(e); // sentry.io로 로그 전송
        return new ResponseEntity<>(e.body(), e.status());
    }

    @ErrorLog
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unknownServerError(Exception e) {
        errorLogRepository.save(smash.teams.be.model.errorLog.ErrorLog.builder() // db에 로그 저장
                .msg(e.getMessage())
                .userId(getUserId())
                .build());
        Sentry.captureException(e); // sentry.io로 로그 전송
        ResponseDTO<String> responseDTO = new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR, "unknownServerError", e.getMessage());
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
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
