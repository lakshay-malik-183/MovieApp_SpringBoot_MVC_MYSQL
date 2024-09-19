package com.spring_boot_project.movieApp.dto;

import lombok.*;

@Data

public class ChangePasswordDto {

    String password;
    String confirmPassword;
}
