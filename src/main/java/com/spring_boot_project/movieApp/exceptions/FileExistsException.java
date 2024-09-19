package com.spring_boot_project.movieApp.exceptions;

public class FileExistsException extends RuntimeException{

    public FileExistsException(String message)
    {
        super(message);
    }
}
