package com.songscorer.songscorer.security;

import com.songscorer.songscorer.exceptions.SymphonyzeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

import static com.songscorer.songscorer.constants.JksConstants.*;
import static io.jsonwebtoken.Jwts.parser;


@Service
public class JwtProvider {

    private KeyStore keyStore;

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

    public String generateToken(Authentication authentication){
        User principal = (org.springframework.security.core.userdetails.User)authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey(JKS_ALIAS, JKS_PASSWORD.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SymphonyzeException(("Exception occurred while retrieving public key from key store"));
        }
    }

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
}
