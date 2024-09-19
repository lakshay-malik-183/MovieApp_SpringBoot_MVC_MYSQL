package com.spring_boot_project.movieApp.services.impl;

import com.spring_boot_project.movieApp.services.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {

        //get name of file
        String fileName = file.getOriginalFilename();

        //make complete file path to upload (path + fileName)
        String filePath = path + File.separator + fileName;

        //creating file object
        File f = new File(path);

        //save image file in our system directory ie posters
        if(!f.exists())
            f.mkdir();

        //copy/upload file,  to path
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }

    @Override
    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException {

        //get complete file path
        String filePath = path + File.separator + fileName;

        return new FileInputStream(filePath);
    }
}
