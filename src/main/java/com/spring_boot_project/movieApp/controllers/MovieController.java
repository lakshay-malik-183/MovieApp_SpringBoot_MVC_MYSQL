package com.spring_boot_project.movieApp.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_boot_project.movieApp.dto.MovieDto;
import com.spring_boot_project.movieApp.exceptions.EmptyFileException;
import com.spring_boot_project.movieApp.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final ModelMapper modelMapper;

    @PostMapping("/addMovie")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<MovieDto> addMovie(@RequestPart MultipartFile file, @RequestPart String movieDto) throws EmptyFileException, IOException {

        if(file.isEmpty())
            throw new EmptyFileException("File is Empty... Please Upload a File");

        //convert this movieDto String (java Object) into movieDto object
        MovieDto dto =  convertToMovieDto(movieDto);

        return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovie(@PathVariable Long movieId)
    {
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<MovieDto>> getAllMovies(@RequestParam(defaultValue = "0", required = false) Integer pageNumber,
                                                       @RequestParam(defaultValue = "5", required = false) Integer pageSize,
                                                       @RequestParam(defaultValue = "id", required = false) String sortingDirection,
                                                       @RequestParam(defaultValue = "asc", required = false) String field)
    {

        return ResponseEntity.ok(movieService.getAllMovies(pageNumber, pageSize, sortingDirection, field));
    }


    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable Long movieId,
                                                       @RequestPart MultipartFile file,
                                                       @RequestPart String movieDtoObj) throws IOException {
        //set file to null if File not to be updated
        if(file.isEmpty())
            file = null;

        //convert String Dto into MovieDto
        MovieDto movieDto =  convertToMovieDto(movieDtoObj);

        return ResponseEntity.ok(movieService.updateMovie(movieId,movieDto, file ));
    }

    @DeleteMapping("/delete/{movieId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<String> deleteMovie(@PathVariable Long movieId) throws IOException
    {
        movieService.deleteMovie(movieId);
        return ResponseEntity.ok("Movie Successfully Deleted With Id: "+movieId);
    }


    private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {

        //using Object Mapper to convert
        ObjectMapper objectMapper = new ObjectMapper();

        //convert and return
        return objectMapper.readValue(movieDtoObj, MovieDto.class);
    }
}
