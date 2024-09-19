package com.spring_boot_project.movieApp.controllers;


import com.spring_boot_project.movieApp.dto.ChangePasswordDto;
import com.spring_boot_project.movieApp.entities.ForgotPassword;
import com.spring_boot_project.movieApp.entities.User;
import com.spring_boot_project.movieApp.repositories.ForgotPasswordRepository;
import com.spring_boot_project.movieApp.repositories.UserRepository;
import com.spring_boot_project.movieApp.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;



    //Send mail  to User Email for Verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email)
    {
        //check user email is correct
        User user = userRepository.findByEmail(email)
                .orElseThrow( () -> new UsernameNotFoundException("Please Provide Valid Email!"));

        //create MailBody
        int otp = otpGenerator();

        //save this otp in forgetPassword Entity to verify with User Entered OTP
        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 1000 * 60 * 2))  //2 minutes
                .user(user)
                .build();

        //send mail
        emailService.sendEmail(email, "OTP for Forget Password Request", "This is OTP for Your Forgot Password Request : " + otp);

        //save in forgot password repository for verify otp later
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Email Successfully Sent for Forgot Password!");
    }

    //for verify OTP screen
    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email)
    {
        //check user email is correct
        User user = userRepository.findByEmail(email)
                .orElseThrow( () -> new UsernameNotFoundException("Please Provide Valid Email!"));

        //we create our own custom query using JPQL (To check User entered OTP and our OTP should same)
        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow( () -> new RuntimeException("Invalid OTP for email! " + email));

        //check expiration time for OTP also  (OTP EXPIRED)
        if(fp.getExpirationTime().before(Date.from(Instant.now())))
        {
            //delete form forgot password repo also because there we define OneToOne Mapping
            forgotPasswordRepository.deleteById(fp.getId());

            return new ResponseEntity<>("OTP has expired!", HttpStatus.EXPECTATION_FAILED);
        }

        //delete form forgot password repo also because there we define OneToOne Mapping
        forgotPasswordRepository.deleteById(fp.getId());

        //OTP NOT EXPIRED
        return ResponseEntity.ok("OTP Verified");
    }

    //Now after Otp Matches then User Can Change its Password
    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto,
                                                        @PathVariable String email)
    {
        // check password and repeat password not same
        if(!Objects.equals(changePasswordDto.getPassword(), changePasswordDto.getConfirmPassword()))
            return new ResponseEntity<>("Please Enter Password and Repeat Password Same",
                    HttpStatus.EXPECTATION_FAILED);


        //both equal , FIRST encrypt password
        String encodedPassword = passwordEncoder.encode(changePasswordDto.getPassword());

        //now update password in DB  (we created Custom Query in User Repository)
        userRepository.updatePassword(email, encodedPassword);

        return ResponseEntity.ok("Password Changed Successfully");
    }


    private Integer otpGenerator()
    {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);  //define minimum and maximum range
    }


}
