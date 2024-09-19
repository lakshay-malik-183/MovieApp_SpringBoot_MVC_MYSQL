package com.spring_boot_project.movieApp.services;

import com.spring_boot_project.movieApp.dto.MovieDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface MovieService {


    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    MovieDto getMovie(Long movieId);

    Page<MovieDto> getAllMovies(Integer pageNumber, Integer pageSize, String sortingDirection, String field);

    MovieDto updateMovie(Long movieId, MovieDto movieDto, MultipartFile file) throws IOException;

    void deleteMovie(Long movieId) throws IOException;

}
