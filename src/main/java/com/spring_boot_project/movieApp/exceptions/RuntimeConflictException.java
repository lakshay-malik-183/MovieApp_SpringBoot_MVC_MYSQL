package com.spring_boot_project.movieApp.exceptions;

public class RuntimeConflictException extends RuntimeException{


    public RuntimeConflictException() {
    }

    public RuntimeConflictException(String message) {
        super(message);
    }
}
