package com.songscorer.songscorer.service;

import com.songscorer.songscorer.model.UserAccount;
import com.songscorer.songscorer.repository.UserAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        Optional<UserAccount> userAccountOptional = userAccountRepository.findByUsername(username);
        UserAccount userAccount = userAccountOptional.orElseThrow(() -> new UsernameNotFoundException("No User " +
                "with username: '" + username + "' was found"));

        return new org.springframework.security.core.userdetails.User(
                userAccount.getUsername(), userAccount.getPassword(), userAccount.isEnabled(),
                true, true, true, getAuthorities("USER")
                );

    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return singletonList(new SimpleGrantedAuthority(role));
    }

}
