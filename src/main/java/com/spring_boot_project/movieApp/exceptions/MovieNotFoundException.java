package com.spring_boot_project.movieApp.exceptions;

public class MovieNotFoundException extends RuntimeException{

    public MovieNotFoundException(String message)
    {
        super(message);
    }
}
