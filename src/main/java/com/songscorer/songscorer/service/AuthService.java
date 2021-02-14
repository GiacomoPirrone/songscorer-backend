package com.songscorer.songscorer.service;

import com.songscorer.songscorer.dto.AuthenticationResponse;
import com.songscorer.songscorer.dto.LoginRequest;
import com.songscorer.songscorer.dto.RefreshTokenRequest;
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

    /*
     * No @Autowired here, it is better to use constructor injections
     * over field injections. An Explanation can be found here: https://bit.ly/2NENAjz
     */

    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository userAccountRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

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

    /*
     * User must generate new verification token to enable their account and start using the
     * api as an active user. This token generated in this method allows users to enable their
     * account.
     */
    private String generateVerificationToken(UserAccount userAccount) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserAccount(userAccount);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    /*
     * Simply checks if the verification token exists in the database, if it does then the
     * user account is enabled and can freely use the api endpoints, until it is not activated
     * it cannot access the api endpoints necessary to use the websites services.
     */
    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SymphonyzeException("Invalid Token"));
        fetchUserAccountAndEnable(verificationToken.get());
    }

    /*
     * This method fetches the user account associated with the given verification token, if
     * a user is found in the database that matches the verification token, then the user
     * account is given the priveledge to be set to enabled.
     */
    @Transactional
    public void fetchUserAccountAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUserAccount().getUsername();
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new SymphonyzeException("User with name '" + username + "' not found"));
        userAccount.setEnabled(true);
        userAccountRepository.save(userAccount);

    }

    /*
     * The main login method, allows users to login given that they provide this endpoint with a
     * username that matches a username in the system and a password which when encrypted using
     * this systems chosen password encryptor matches that encrypted password stored in the database
     * of that specific user.
     *
     * Additionally a jwt token is generated a provided to the user storing it in their account in the database.
     * The token is assigned an expiration date, which is the time from now plus the expiration time in millis
     * which can be changed in application.properties.
     */
    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtProvider.generateToken(auth);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();
    }

    // Used for when the system needs to know what the user account that accessed an endpoint at a given moment
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

    /*
     * Authentication tokens expire given that the current time is past the expiration time of the token,
     * refresh tokens allow users to create new authentication tokens if they expire and the user
     * still needs to access filtered api endpoints.
     */
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUsername(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }
}
