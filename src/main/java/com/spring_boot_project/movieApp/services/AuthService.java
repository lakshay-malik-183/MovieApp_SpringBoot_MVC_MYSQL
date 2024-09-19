package com.spring_boot_project.movieApp.services;


import com.spring_boot_project.movieApp.dto.LoginRequestDto;
import com.spring_boot_project.movieApp.dto.SignupDto;
import com.spring_boot_project.movieApp.dto.UserDto;

public interface AuthService {

    UserDto signUp(SignupDto signupDto);

    String[] login(String email,  String Password);

    String refreshToken(String refreshToken);

}
