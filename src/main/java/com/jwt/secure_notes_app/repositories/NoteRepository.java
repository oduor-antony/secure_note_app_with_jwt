package com.jwt.secure_notes_app.repositories;

import com.jwt.secure_notes_app.models.Note;
import com.jwt.secure_notes_app.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note,Long> {
    List<Note> findByOwnerOrderByCreatedAtDesc(Users owner);
    Optional<Note> findByIdAndOwner(Long id, Users owner);
}
