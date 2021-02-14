package com.songscorer.songscorer.service;

import com.songscorer.songscorer.exceptions.SymphonyzeException;
import com.songscorer.songscorer.model.RefreshToken;
import com.songscorer.songscorer.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /*
     * Refresh tokens allow the user to create new authentication tokens
     * when the expiration date/time is reached for the authentication token.
     * Refresh tokens are deleted when user logs out for security reasons.
     */
    public RefreshToken generateRefreshToken() {

        RefreshToken refreshToken = new RefreshToken();
        //Create new token using the UUID library for random generation
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());

        return refreshTokenRepository.save(refreshToken);
    }

    /*
     * Validate the refresh token by checking if it exists in the database,
     * repository statement must return true in order to proceed in generating
     * a new authentication token
     */
    void validateRefreshToken(String token) {
        // Refresh token must exist in order for user to create new auth token
        refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new SymphonyzeException("Invalid refresh token!"));
    }

    /*
     * Use case is for when user logs out of account, this is for
     * security reasons as, if the the user is not logged in then they
     * must not need to access api endpoints/data any more.
     */
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
