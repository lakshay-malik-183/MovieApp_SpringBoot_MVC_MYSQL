package com.spring_boot_project.movieApp.services.impl;


import com.spring_boot_project.movieApp.dto.SignupDto;
import com.spring_boot_project.movieApp.dto.UserDto;
import com.spring_boot_project.movieApp.entities.User;
import com.spring_boot_project.movieApp.entities.enums.Role;
import com.spring_boot_project.movieApp.exceptions.ResourceNotFoundException;
import com.spring_boot_project.movieApp.exceptions.RuntimeConflictException;
import com.spring_boot_project.movieApp.repositories.UserRepository;
import com.spring_boot_project.movieApp.security.JwtService;
import com.spring_boot_project.movieApp.services.AuthService;
import com.spring_boot_project.movieApp.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final  UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;

    @Override
    public UserDto signUp(SignupDto signupDto) {

        //check in repository with this email have already a user or not
       User user = userRepository.findByEmail(signupDto.getEmail()).orElse(null);

       if(user != null)
           throw  new RuntimeConflictException("Cant not Signup, User Already Registered With Email id: "+signupDto.getEmail());

       //convert signupDto to user object
        User newUser = modelMapper.map(signupDto, User.class);

       //set default role as Customer
        newUser.setRoles(Set.of(Role.CUSTOMER));

        //bcrypt password
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        //save user
        User savedUser = userRepository.save(newUser);

        //create a customer
        customerService.createNewCustomer(savedUser);

        //return user dto
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public String[] login(String email, String password) {

        //use authentication manager to authenticate using email password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // get user from authentication
        User user = (User) authentication.getPrincipal();

        //generate both token for this user
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        String tokens[] = {accessToken, refreshToken};
        return tokens;
    }

    @Override
    public String refreshToken(String refreshToken) {

        //get User id From Token
        Long userId = jwtService.getUserIdFromToken(refreshToken);

        //find user using  user Id
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new ResourceNotFoundException("user not found with id: "+userId) );

        //call jwt service again to generate Access Token
        return jwtService.generateAccessToken(user);
    }
}
