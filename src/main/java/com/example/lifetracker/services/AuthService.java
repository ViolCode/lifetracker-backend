package com.example.lifetracker.services;

import com.example.lifetracker.domains.User;
import com.example.lifetracker.exceptions.ConflictException;
import com.example.lifetracker.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(
            String email,
            String password,
            String name
    ) {

        repository.findByEmail(email)
                .ifPresent(user -> {
                    throw new ConflictException(
                            "Email already exists"
                    );
                });

        String hashedPassword =
                passwordEncoder.encode(password);

        User user = new User(
                email,
                hashedPassword,
                name
        );

        return repository.save(user);
    }
}