package com.jwt.secure_notes_app.exceptions;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponsee {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
}
