package com.example.flatscraper.exception;

import com.example.flatscraper.exception.response.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    private final String ENTITY = "entity";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String TOKEN = "token";

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(USERNAME, e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(ENTITY, e.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(ENTITY, e.getMessage()));
    }

    @ExceptionHandler(UsernameIsTakenException.class)
    public ResponseEntity<ErrorResponse> handleUsernameIsTakenException(UsernameIsTakenException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(USERNAME, e.getMessage()));
    }

    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUsernameException(InvalidUsernameException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(USERNAME, e.getMessage()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(PASSWORD, e.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(TOKEN, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String field = e.getBindingResult().getFieldError().getField();
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(field, message));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(TOKEN, "Token has expired."));
    }
}
