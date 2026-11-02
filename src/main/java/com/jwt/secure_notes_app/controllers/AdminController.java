package com.jwt.secure_notes_app.controllers;


import com.jwt.secure_notes_app.dtos.UserResponse;
import com.jwt.secure_notes_app.repositories.UserRepository;
import com.jwt.secure_notes_app.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserRepository userRepository;
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> listUsers(){
        return ResponseEntity.ok(adminService.getUsers());

    }
}
