package org.somuga.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.somuga.exception.user.UserDuplicateFieldException;
import org.somuga.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@Component
@ControllerAdvice
public class SomugaExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SomugaExceptionHandler.class);

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Error> handleNotFound(Exception e, HttpServletRequest request) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(
                Error.builder()
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .status(HttpStatus.NOT_FOUND.value())
                        .method(request.getMethod())
                        .timestamp(new Date())
                        .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserDuplicateFieldException.class})
    public ResponseEntity<Error> handleBadRequest(Exception e, HttpServletRequest request) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(
                Error.builder()
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .method(request.getMethod())
                        .timestamp(new Date())
                        .build(), HttpStatus.BAD_REQUEST);
    }
}
