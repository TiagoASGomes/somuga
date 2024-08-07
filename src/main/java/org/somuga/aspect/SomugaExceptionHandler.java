package org.somuga.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.game.GameNotFoundException;
import org.somuga.exception.game_genre.GenreAlreadyExistsException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.exception.like.AlreadyLikedException;
import org.somuga.exception.like.LikeNotFoundException;
import org.somuga.exception.media.MediaNotFoundException;
import org.somuga.exception.movie.InvalidCrewRoleException;
import org.somuga.exception.movie.MovieNotFoundException;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
import org.somuga.exception.platform.PlatformAlreadyExistsException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.somuga.exception.review.AlreadyReviewedException;
import org.somuga.exception.review.ReviewNotFoundException;
import org.somuga.exception.user.DuplicateFieldException;
import org.somuga.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@Component
@ControllerAdvice
public class SomugaExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SomugaExceptionHandler.class);

    private static String getErrorMessage(Exception e, HttpServletRequest request) {
        return "Path: " + request.getRequestURI() + " Method: " + request.getMethod() + " Error: " + e.getMessage();
    }

    @ExceptionHandler({UserNotFoundException.class,
            ReviewNotFoundException.class,
            GameNotFoundException.class,
            MovieNotFoundException.class,
            MediaNotFoundException.class,
            LikeNotFoundException.class,
            DeveloperNotFoundException.class,
            GenreNotFoundException.class,
            PlatformNotFoundException.class,
            MovieCrewNotFoundException.class})
    public ResponseEntity<ErrorDto> handleNotFound(Exception e, HttpServletRequest request) {
        String errorMessage = getErrorMessage(e, request);
        logger.error(errorMessage);
        return new ResponseEntity<>(new ErrorDto(
                e.getMessage()
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DuplicateFieldException.class,
            AlreadyLikedException.class,
            AlreadyReviewedException.class,
            GenreAlreadyExistsException.class,
            PlatformAlreadyExistsException.class,
            InvalidCrewRoleException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            SQLIntegrityConstraintViolationException.class,})
    public ResponseEntity<ErrorDto> handleBadRequest(Exception e, HttpServletRequest request) {
        String errorMessage = getErrorMessage(e, request);
        logger.error(errorMessage);
        return new ResponseEntity<>(new ErrorDto(
                e.getMessage()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        StringBuilder errorMessageBuilder = new StringBuilder();
        errors.forEach(error -> errorMessageBuilder.append(error).append(", "));
        errorMessageBuilder.delete(errorMessageBuilder.length() - 2, errorMessageBuilder.length()).append(".");
        String errorMessage = errorMessageBuilder.toString();
        String error = "Path: " + request.getRequestURI() + " Method: " + request.getMethod() + " Error: " + errorMessage;
        logger.error(error);
        return new ResponseEntity<>(new ErrorDto(
                errorMessage
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPermissionException.class)
    public ResponseEntity<ErrorDto> handleForbidden(Exception e, HttpServletRequest request) {
        String errorMessage = getErrorMessage(e, request);
        logger.error(errorMessage);
        return new ResponseEntity<>(new ErrorDto(
                e.getMessage()
        ), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception e, HttpServletRequest request) {
        String errorMessage = getErrorMessage(e, request);
        logger.error(errorMessage);
        return new ResponseEntity<>(new ErrorDto(
                e.getMessage()
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
