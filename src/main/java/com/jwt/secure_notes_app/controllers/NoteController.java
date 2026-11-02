package com.jwt.secure_notes_app.controllers;

import com.jwt.secure_notes_app.dtos.NoteRequest;
import com.jwt.secure_notes_app.dtos.NoteResponse;
import com.jwt.secure_notes_app.services.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping("/create")
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request,
                                               @AuthenticationPrincipal UserDetails user) {
        NoteResponse created = noteService.create(request, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/getAll")
    public List<NoteResponse> listMine(@AuthenticationPrincipal UserDetails user) {
        return noteService.listMine(user.getUsername());
    }

    @GetMapping("/{id}")
    public NoteResponse getOne(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails user) {
        return noteService.getMine(id, user.getUsername());
    }

    @PutMapping("/{id}")
    public NoteResponse update(@PathVariable Long id,
                               @Valid @RequestBody NoteRequest request,
                               @AuthenticationPrincipal UserDetails user) {
        return noteService.update(id, request, user.getUsername());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails user) {
        noteService.delete(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}