package com.songscorer.songscorer.security;

import com.songscorer.songscorer.exceptions.SymphonyzeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

import static com.songscorer.songscorer.constants.JksConstants.*;
import static io.jsonwebtoken.Jwts.parser;
import static java.util.Date.from;

/*
 * ---------------------------------------IMPORTANT NOTE!!!---------------------------------------
 * All sensitive keystore/JKS file information needed to access the file is kept locally as of now
 * for obvious security reasons. If you are an open source worker who would like to help and you need
 * information to access the keys for development please contact me on github. I have generated constant
 * files which I keep locally, I can provide these files to those who need them given that they have
 * clear good intentions to improve the project.
 */

@Service
public class JwtProvider {

    /*
     * Our key store for the jwt provider, simply put the keystore is a generated file
     * which contains cryptographic keys and certificates needed sign sensitive information.
     */
    private KeyStore keyStore;

    // The value at which authentication tokens expire (is modular can be changed in application.properties)
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    // Initializes the keystore with the information provided to acces the jks file generated for this backend
    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream(JKS_FILE_PATH);
            keyStore.load(resourceAsStream, JKS_PASSWORD.toCharArray());
        } catch(KeyStoreException | CertificateException | NoSuchAlgorithmException| IOException e) {
            throw new SymphonyzeException("Exception occured while loading keystore");
        }
    }

    /*
     * Generates the authentication jwt token needed for users to access api endpoints using the builder method
     * please check the jwt documentations to see how this works.
     */
    public String generateToken(Authentication authentication){
        User principal = (org.springframework.security.core.userdetails.User)authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    // If need jwt authentication tokens can be generated simply by the users username, this is for select scenarios
    public String generateTokenWithUsername(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    // Get the private key provided by the keystore using private information to access the JKS file
    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey(JKS_ALIAS, JKS_PASSWORD.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SymphonyzeException(("Exception occurred while retrieving public key from key store"));
        }
    }

    // We can validate the token with the public keys provided by the JKS file
    public boolean validateToken(String jwt) {
        parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate(JKS_ALIAS).getPublicKey();
        } catch (KeyStoreException e) {
            throw new SymphonyzeException("Exception occured while " +
                    "retrieving public key from the keystore");
        }
    }
    
    public String getUsernameFromJwt(String token){
        Claims claims = parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Return the time it take for a token to expire, this is set in application.properties
    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }
}
