package com.jwt.secure_notes_app.exceptions;

public class NoteNotFoundException extends RuntimeException{
    public NoteNotFoundException(Long id){
        super(" Note not Found:"+ id);
    }
}
