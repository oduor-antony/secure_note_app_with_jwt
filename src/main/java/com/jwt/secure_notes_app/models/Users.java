package com.jwt.secure_notes_app.models;

import com.jwt.secure_notes_app.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.support.BeanDefinitionDsl;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
    @Column(nullable = false,unique = true)
   private String username;
    @Column(nullable = false)
   private String passWordHarsh;
   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
   private Role role;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
