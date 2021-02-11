package com.songscorer.songscorer.controller;

import com.songscorer.songscorer.dto.AuthenticationResponse;
import com.songscorer.songscorer.dto.LoginRequest;
import com.songscorer.songscorer.dto.RegisterRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.songscorer.songscorer.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    /*
     * The default signup api endpoint, I have disabled the internal filter on this endpoint
     * as it blocks users from creating a new account and this endpoint does not expose any
     * sensitive information.
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {
        // Send the request to signup with all the register information
        authService.signup(registerRequest);
        // And if the request is successful let the system know that the request is ok
        return new ResponseEntity<>("User Registration Successful", HttpStatus.OK);
    }

    /*
     * The default account verification api endpoint, I have disabled the internal filter on
     * this endpoint as it blocks users from creating a new account and this endpoint does not
     * expose any sensitive information.
     */
    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Successfully Activated", HttpStatus.OK);
    }

    /*
     * The default logout api endpoint, I have disabled the internal filter on this endpoint
     * as it blocks users from creating a new account and this endpoint does not expose any
     * sensitive information.
     */
    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
