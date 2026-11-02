package com.jwt.secure_notes_app.exceptions;

public class DuplicateUsernameException extends RuntimeException{
 public DuplicateUsernameException(String userName){
     super("Username with the same name already registerd");
 }
}
