package com.example.lifetracker.security;

import com.example.lifetracker.persistence.UserRepository;

import org.springframework.security.core.userdetails
        .UserDetails;

import org.springframework.security.core.userdetails
        .UserDetailsService;

import org.springframework.security.core.userdetails
        .UsernameNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository repository;

    public CustomUserDetailsService(
            UserRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String email
    ) {

        var user =
                repository.findByEmail(email)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "User not found"
                                )
                        );

        return new UserPrincipal(user);
    }
}