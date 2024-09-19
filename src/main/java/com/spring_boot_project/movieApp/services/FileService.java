package com.spring_boot_project.movieApp.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FileService {

    //return file name , and this function get path where to upload file,
    // it is needed when we want to save the poster file for movie entity
    String uploadFile(String path, MultipartFile file) throws IOException;

    //it is needed when we get data from our file directory return file object
    InputStream getResourceFile(String path, String fileName) throws FileNotFoundException;
}
