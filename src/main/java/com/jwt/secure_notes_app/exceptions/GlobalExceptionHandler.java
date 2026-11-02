package com.jwt.secure_notes_app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ErrorResponsee> notFound( RuntimeException e){
        return build(HttpStatus.CONFLICT,"Duplicatie  information");
    }

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ErrorResponsee> noteNotFound(RuntimeException e){
        return  build(HttpStatus.NOT_FOUND,"Notes not Found");
    }

    private ResponseEntity<ErrorResponsee> build(HttpStatus status, String message) {
        ErrorResponsee body = new ErrorResponsee(
                status.value(), status.getReasonPhrase(), message, LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }

}
