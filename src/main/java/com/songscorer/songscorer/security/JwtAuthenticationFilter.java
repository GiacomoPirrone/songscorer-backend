package com.songscorer.songscorer.security;

import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserDetailsService userDetailsServiceImpl;

    /*
     * Most information from each endpoint which provides some sort of information
     * should only be accessed by those that are using an account. Therefore we will do
     * an internal filter to check that only symphonyze members are using these endpoints
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        /*
         * Users will be accessing endpoints which bearer tokens, sometimes these can
         * arrive at the server with additional information attached to it, we edit the bearer token
         * received by the user request and get only the substring containing the bearer token we need
         */
        String jwt = getJwtFromRequest(request);

        /*
         * Check if the jwt token actually contains any text and isn't null,
         * additionally check if the token exists on the system
         */
        if(StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
            // Collect information regarding which user this token came from
            String username = jwtProvider.getUsernameFromJwt(jwt);

            // Gather all the basic user details from this user
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            // Allow the request to access this endpoint
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        // Perform the filter based on whether the bearer token allows access or not
        filterChain.doFilter(request, response);
    }

    // A basic pre-check which assures certain endpoints do not undergo a filter
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
            throws ServletException {
        String path = request.getRequestURI();
        // These are the only three endpoints which shouldn't require a filter
        boolean doNotFilter =
                "/api/auth/login".equals(path) |
                "/api/auth/signup".equals(path) |
                "/accountVerification/{token}".equals(path);
        // If the user requests one of these endpoints doFilter = true, else return false and do filter the request
        return doNotFilter;
    }

    // Gets bearer token and only returns the substring which contains the token needed and removes unnecessary headers
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(request.getHeader("Authorization"));

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}
