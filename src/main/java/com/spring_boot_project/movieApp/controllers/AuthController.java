package com.spring_boot_project.movieApp.controllers;

import com.spring_boot_project.movieApp.dto.LoginRequestDto;
import com.spring_boot_project.movieApp.dto.LoginResponseDto;
import com.spring_boot_project.movieApp.dto.SignupDto;
import com.spring_boot_project.movieApp.dto.UserDto;
import com.spring_boot_project.movieApp.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    ResponseEntity<UserDto> signup(@RequestBody SignupDto signupDto)
    {
        return new ResponseEntity<>(authService.signUp(signupDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto,
                                           HttpServletRequest httpServletRequest,
                                           HttpServletResponse httpServletResponse)
    {
        // at 0 index -> accessToken  (we pass accessToken in response)
        //at 1 index -> refreshToken  (we Store refreshToken in Cookie)
        String tokens[] = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        //create Cookie for refresh Token
        Cookie cookie = new Cookie("token", tokens[1]);
        cookie.setHttpOnly(true); // so client can not access the cookie

        //send cookie in response
        httpServletResponse.addCookie(cookie);

        return ResponseEntity.ok( new LoginResponseDto(tokens[0]));
    }


    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request)
    {
        //we get refresh token from cookies
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "token".equals(cookie.getName()))
                .findFirst()
                .map(cookie -> cookie.getValue())
                .orElseThrow( () -> new AuthenticationServiceException("Refresh Token Not Found Inside the Cookies"));

        String accessToken = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }
}
