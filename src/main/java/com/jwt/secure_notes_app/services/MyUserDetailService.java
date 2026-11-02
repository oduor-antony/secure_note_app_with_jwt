package com.jwt.secure_notes_app.services;

import com.jwt.secure_notes_app.models.Users;
import com.jwt.secure_notes_app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users=userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User doesnt exist") );
        return User.withUsername(users.getUsername())
                .password(users.getPassWordHarsh())
                .roles(users.getRole().name())
                .build();

    }
}
