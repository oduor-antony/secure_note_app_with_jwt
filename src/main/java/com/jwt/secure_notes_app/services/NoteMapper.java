package com.jwt.secure_notes_app.services;

import com.jwt.secure_notes_app.dtos.NoteResponse;
import com.jwt.secure_notes_app.models.Note;

import java.util.List;

public class NoteMapper {

    // Utility class: all methods are static, so nobody should create an instance
    private NoteMapper() {
    }

    // Converts one Note entity into the DTO the client receives
    public static NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt());
    }

    // Converts a list of entities into a list of DTOs
    public static List<NoteResponse> toResponses(List<Note> notes) {
        return notes.stream()
                .map(NoteMapper::toResponse)
                .toList();
    }
}