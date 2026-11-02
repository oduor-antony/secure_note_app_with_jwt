package com.jwt.secure_notes_app.repositories;

import com.jwt.secure_notes_app.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Long> {

    boolean existsByUsername(String username);

    Optional<Users> findByUsername(String username);
}
