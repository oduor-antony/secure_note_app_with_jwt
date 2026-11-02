package com.jwt.secure_notes_app.services;

import com.jwt.secure_notes_app.dtos.UserResponse;
import com.jwt.secure_notes_app.models.Users;
import com.jwt.secure_notes_app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    public List<UserResponse> getUsers(){
      List<Users> users=  userRepository.findAll();
    return users.stream()
              .map(u->new UserResponse(u.getId(),u.getUsername(),u.getRole()))
              .toList();
    }

}
