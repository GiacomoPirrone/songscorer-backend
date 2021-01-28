package com.songscorer.songscorer.service;

import com.songscorer.songscorer.dto.RegisterRequest;
import com.songscorer.songscorer.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void signup(RegisterRequest registerRequest) {
        UserAccount userAccount = new UserAccount();
        //Extrapolate data from RegisterRequest dto to get the basic info for signup
        userAccount.setUsername(registerRequest.getUsername());
        userAccount.setEmail(registerRequest.getEmail());
        userAccount.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        //The users account must document when the account was created, take the time of the instant
        userAccount.setCreated(Instant.now());
        //The users account must be enabled befoere it can login, set it to disabled
        userAccount.setEnabled(false);
    }
}
