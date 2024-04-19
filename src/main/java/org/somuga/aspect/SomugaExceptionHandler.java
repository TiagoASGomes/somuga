package org.somuga.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.somuga.exception.user.UserDuplicateFieldException;
import org.somuga.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.List;

@Component
@ControllerAdvice
public class SomugaExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SomugaExceptionHandler.class);

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Error> handleNotFound(Exception e, HttpServletRequest request) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(new Error(
                e.getMessage(),
                request.getRequestURI(),
                HttpStatus.NOT_FOUND.value(),
                request.getMethod(),
                new Date()
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserDuplicateFieldException.class})
    public ResponseEntity<Error> handleBadRequest(Exception e, HttpServletRequest request) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(new Error(
                e.getMessage(),
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                request.getMethod(),
                new Date()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        StringBuilder errorMessageBuilder = new StringBuilder();
        errors.forEach(error -> errorMessageBuilder.append(error).append(", "));
        errorMessageBuilder.delete(errorMessageBuilder.length() - 2, errorMessageBuilder.length()).append(".");
        String errorMessage = errorMessageBuilder.toString();
        logger.error(errorMessage);
        return new ResponseEntity<>(new Error(
                errorMessage,
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                request.getMethod(),
                new Date()
        ), HttpStatus.BAD_REQUEST);
    }
}
