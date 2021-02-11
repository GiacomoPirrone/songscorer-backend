package com.songscorer.songscorer.service;

import com.songscorer.songscorer.dto.AuthenticationResponse;
import com.songscorer.songscorer.dto.LoginRequest;
import com.songscorer.songscorer.dto.RegisterRequest;
import com.songscorer.songscorer.model.UserAccount;
import com.songscorer.songscorer.model.VerificationToken;
import com.songscorer.songscorer.repository.UserAccountRepository;
import com.songscorer.songscorer.repository.VerificationTokenRepository;
import com.songscorer.songscorer.exceptions.SymphonyzeException;
import com.songscorer.songscorer.security.JwtProvider;
import lombok.AllArgsConstructor;
import com.songscorer.songscorer.model.NotificationEmail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    /* No @Autowired here, it is better to use constructor injections
     * over field injections. An Explanation can be found here: https://bit.ly/2NENAjz
     */

    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository userAccountRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signup(RegisterRequest registerRequest) {
        UserAccount userAccount = new UserAccount();
        //Extrapolate data from RegisterRequest dto to get the basic info for signup
        userAccount.setUsername(registerRequest.getUsername());
        userAccount.setEmail(registerRequest.getEmail());
        userAccount.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        //The users account must document when the account was created, take the time of the instant
        userAccount.setCreated(Instant.now());
        //The users account must be enabled before it can login, set it to disabled
        userAccount.setEnabled(false);
        userAccountRepository.save(userAccount);

        String token = generateVerificationToken(userAccount);

        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                userAccount.getEmail(), "Thank you for signing up to Symphonyze! \n" +
                "To activate your account please click the url provided below: \n" +
                "http://localhost:8080/api/auth/accountVerification/" + token));
    }

    private String generateVerificationToken(UserAccount userAccount) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserAccount(userAccount);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SymphonyzeException("Invalid Token"));
        fetchUserAccountAndEnable(verificationToken.get());
    }

    @Transactional
    public void fetchUserAccountAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUserAccount().getUsername();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new SymphonyzeException("User with name '" + username + "' not found"));
        userAccount.setEnabled(true);
        userAccountRepository.save(userAccount);

    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtProvider.generateToken(auth);
        return new AuthenticationResponse(token, loginRequest.getUsername());
    }

    @Transactional(readOnly = true)
    public UserAccount getCurrentUserAccount() {
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
        return userAccountRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User with name '"
                        + principal.getUsername()
                        + "' not found!"));
    }
}
