package com.jwt.secure_notes_app.services;

import com.jwt.secure_notes_app.dtos.*;
import com.jwt.secure_notes_app.enums.Role;
import com.jwt.secure_notes_app.exceptions.DuplicateUsernameException;
import com.jwt.secure_notes_app.models.Users;
import com.jwt.secure_notes_app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUsernameException("The user with the same username exists");
        }
        Users users = new Users();
        users.setUsername(request.getUsername());
        users.setPassWordHarsh(passwordEncoder.encode(request.getPassWordHarsh()));
        users.setRole(Role.USER);
        userRepository.save(users);
        return new UserResponse(users.getId(), users.getUsername(), users.getRole());
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Users users = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(users.getUsername(), users.getRole());
        return new LoginResponse(token, "Bearer");
    }
}