package com.jwt.secure_notes_app.dtos;

import com.jwt.secure_notes_app.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;


import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor

public class RegisterRequest {
    @NotBlank(message = "Username should not be blank")
    @Size(min = 3,max = 20,message = "The username should not be less that 3 charactrs and more than 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9_.-]+$",
            message = "Username may only contain letters, digits, dot, underscore and hyphen")
    private String username;

    @NotBlank(message = "Please fill the password, it should not be blank")
    @Size(min = 8, max = 16, message = "password should be between 8 and 16 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "Password must contain at least one letter and one digit")
    private String passWordHarsh;


}
