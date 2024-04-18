package org.somuga.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Error> handleException(Exception e, HttpServletRequest request){
        logger.error(e.getMessage());
        return new ResponseEntity<>(
                Error.builder()
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .method(request.getMethod())
                        .timestamp(new Date())
                        .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
