package com.spring_boot_project.movieApp.dto;

import lombok.Data;

@Data
public class LoginRequestDto {

    private String email;

    private String password;
}
