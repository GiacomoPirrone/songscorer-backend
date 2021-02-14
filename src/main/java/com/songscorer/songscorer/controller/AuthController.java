package com.songscorer.songscorer.controller;

import com.songscorer.songscorer.dto.AuthenticationResponse;
import com.songscorer.songscorer.dto.LoginRequest;
import com.songscorer.songscorer.dto.RefreshTokenRequest;
import com.songscorer.songscorer.dto.RegisterRequest;
import com.songscorer.songscorer.service.RefreshTokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.songscorer.songscorer.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

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
        return new ResponseEntity<>("User Registration Successful", OK);
    }

    /*
     * The default account verification api endpoint, I have disabled the internal filter on
     * this endpoint as it blocks users from creating a new account and this endpoint does not
     * expose any sensitive information.
     */
    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Successfully Activated", OK);
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

    @PostMapping("refresh/token")
    public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(OK).body("The Refresh Token has Been Successfully Deleted!");
    }
}
