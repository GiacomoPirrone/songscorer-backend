package com.songscorer.songscorer.service;

import com.songscorer.songscorer.dto.RegisterRequest;
import com.songscorer.songscorer.model.UserAccount;
import com.songscorer.songscorer.model.VerificationToken;
import com.songscorer.songscorer.repository.UserAccountRepository;
import com.songscorer.songscorer.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    /* No @Autowired here, it is better to use constructor injections
     * over field injections. An Explanation can be found here: https://bit.ly/2NENAjz
     */

    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository userAccountRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    @Transactional
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
        userAccountRepository.save(userAccount);

        String token = generateVerificationToken(userAccount);
    }

    private String generateVerificationToken(UserAccount userAccount) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(userAccount);

        verificationTokenRepository.save(verificationToken);
        return token;
    }
}
