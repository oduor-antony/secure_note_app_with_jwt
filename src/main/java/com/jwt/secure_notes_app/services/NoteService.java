package com.jwt.secure_notes_app.services;

import com.jwt.secure_notes_app.dtos.NoteRequest;
import com.jwt.secure_notes_app.dtos.NoteResponse;
import com.jwt.secure_notes_app.exceptions.NoteNotFoundException;
import com.jwt.secure_notes_app.models.Note;
import com.jwt.secure_notes_app.models.Users;
import com.jwt.secure_notes_app.repositories.NoteRepository;
import com.jwt.secure_notes_app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Transactional
    public NoteResponse create(NoteRequest request, String username) {
        Users owner = findUser(username);
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setOwner(owner);
        return NoteMapper.toResponse(noteRepository.save(note));
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> listMine(String username) {
        Users owner = findUser(username);
        return NoteMapper.toResponses(noteRepository.findByOwnerOrderByCreatedAtDesc(owner));
    }

    @Transactional(readOnly = true)
    public NoteResponse getMine(Long id, String username) {
        Users owner = findUser(username);
        Note note = noteRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new NoteNotFoundException(id));
        return NoteMapper.toResponse(note);
    }

    @Transactional
    public NoteResponse update(Long id, NoteRequest request, String username) {
        Users owner = findUser(username);
        Note note = noteRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new NoteNotFoundException(id));
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        return NoteMapper.toResponse(noteRepository.save(note));
    }

    @Transactional
    public void delete(Long id, String username) {
        Users owner = findUser(username);
        Note note = noteRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new NoteNotFoundException(id));
        noteRepository.delete(note);
    }

    private Users findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}