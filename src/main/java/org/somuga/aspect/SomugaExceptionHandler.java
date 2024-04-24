package org.somuga.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.game.GameNotFoundException;
import org.somuga.exception.game_genre.GenreAlreadyExistsException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.exception.like.AlreadyLikedException;
import org.somuga.exception.like.LikeNotFoundException;
import org.somuga.exception.media.MediaNotFoundException;
import org.somuga.exception.movie.MovieNotFoundException;
import org.somuga.exception.platform.PlatformAlreadyExistsException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.somuga.exception.review.AlreadyReviewedException;
import org.somuga.exception.review.ReviewNotFoundException;
import org.somuga.exception.user.DuplicateFieldException;
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

    @ExceptionHandler({UserNotFoundException.class,
            ReviewNotFoundException.class,
            GameNotFoundException.class,
            MovieNotFoundException.class,
            MediaNotFoundException.class,
            LikeNotFoundException.class,
            DeveloperNotFoundException.class,
            GenreNotFoundException.class,
            PlatformNotFoundException.class})
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

    @ExceptionHandler({DuplicateFieldException.class,
            AlreadyLikedException.class,
            AlreadyReviewedException.class,
            GenreAlreadyExistsException.class,
            PlatformAlreadyExistsException.class})
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
