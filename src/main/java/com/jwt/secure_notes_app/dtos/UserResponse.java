package com.jwt.secure_notes_app.dtos;

import com.jwt.secure_notes_app.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private Role role;
}