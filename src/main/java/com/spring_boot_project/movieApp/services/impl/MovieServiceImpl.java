package com.spring_boot_project.movieApp.services.impl;


import com.spring_boot_project.movieApp.dto.MovieDto;
import com.spring_boot_project.movieApp.entities.Movie;
import com.spring_boot_project.movieApp.exceptions.FileExistsException;
import com.spring_boot_project.movieApp.exceptions.MovieNotFoundException;
import com.spring_boot_project.movieApp.repositories.MovieRepository;
import com.spring_boot_project.movieApp.services.FileService;
import com.spring_boot_project.movieApp.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {

        // 0. check already have same image file or not
        if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename())))
            throw new FileExistsException("File Already Exists With Name: " +file.getOriginalFilename() + " Please Enter Another File Name!");

        //1. upload file
        String uploadedFileName =  fileService.uploadFile(path, file);

        //2. set the value of field 'poster' as file name
        movieDto.setPoster(uploadedFileName);

        //3. convert movie DTO into Movie object
        Movie movie = modelMapper.map(movieDto, Movie.class);

        //4. save movie in DB
        movieRepository.save(movie);

        //5. update posterURL field in DTO
        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        //6. convert back movie object in MOVIE DTO and return
        MovieDto savedMovieDto = modelMapper.map(movie, MovieDto.class );
        savedMovieDto.setPosterUrl(posterUrl);
        return  savedMovieDto;

//        MovieDto response = new MovieDto(
//                savedMovie.getMovieId(),
//                savedMovie.getTitle(),
//                savedMovie.getDirector(),
//                savedMovie.getStudio(),
//                savedMovie.getMovieCast(),
//                savedMovie.getReleaseYear(),
//                savedMovie.getPoster(),
//                posterUrl
//        );

        //return response;

    }

    @Override
    public MovieDto getMovie(Long movieId) {

        //1. check data in db if exits then fetch data of given id
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow( () -> new MovieNotFoundException("Movie Not Found with ID: " +movieId));

        //2. generate posterUrl
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        //3. map to movie DTO and return
        MovieDto savedMovieDto = modelMapper.map(movie, MovieDto.class );
        savedMovieDto.setPosterUrl(posterUrl);

        return  savedMovieDto;
    }

    @Override
    public Page<MovieDto> getAllMovies(Integer pageNumber, Integer pageSize, String sortingDirection, String field) {

        //Sorting
        Sort sort = sortingDirection.equalsIgnoreCase("asc") ? Sort.by(field).ascending()
                : Sort.by(field).descending();

        //Paging
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort );

        // find all movies pages
        Page<Movie> moviePages =  movieRepository.findAll(pageable);

        //iterate over Movie object Pages and convert to movieDto object pages and return;
        return moviePages.map( movie -> modelMapper.map(movie, MovieDto.class));
    }

    @Override
    public MovieDto updateMovie(Long movieId, MovieDto movieDto, MultipartFile file) throws IOException {

        //1. check movie Exists or not with given id
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow( () -> new MovieNotFoundException("Movie Not Found with ID: " +movieId));

        //2. if movie exists then,
        //2.1  if file null (means user only want to update data) then do nothing with file
        String fileName = movie.getPoster();

        //2.2   if file not null then delete existing file and upload new file
        if(file != null)
        {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        //3. set movie dto poster value (means image name)
        movieDto.setPoster(fileName);

        //4. map to movie object
        Movie mappedMovie = modelMapper.map(movieDto, Movie.class);

        //5. save in db
        movieRepository.save(mappedMovie);

        //6. generate posterUrl
        String posterUrl = baseUrl + "/file/" + fileName;

        //7. map to movie Dto and return
        MovieDto savedMovieDto = modelMapper.map(mappedMovie, MovieDto.class );
        savedMovieDto.setPosterUrl(posterUrl);

        return  savedMovieDto;
    }

    @Override
    public void deleteMovie(Long movieId) throws IOException {

        //1. check movie exists or not
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow( () -> new MovieNotFoundException("Movie Not Found with ID: " +movieId));

        //2. delete the movie image file
        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));

        //3. delete movie object from DB
        movieRepository.delete(movie);
    }
}
