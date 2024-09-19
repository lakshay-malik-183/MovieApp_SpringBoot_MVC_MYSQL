package com.spring_boot_project.movieApp.exceptions;

public class EmptyFileException  extends Throwable{

    public EmptyFileException(String message)
    {
        super(message) ;
    }
}
