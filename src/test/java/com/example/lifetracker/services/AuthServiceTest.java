package com.example.lifetracker.services;

import com.example.lifetracker.domains.User;
import com.example.lifetracker.exceptions.ConflictException;
import com.example.lifetracker.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository repository;
    private PasswordEncoder passwordEncoder;
    private AuthService service;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        service = new AuthService(
                repository,
                passwordEncoder
        );
    }

    @Test
    void shouldRegisterUser() {

        when(repository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashedPassword");

        User savedUser = new User(
                1,
                "test@test.com",
                "hashedPassword",
                "John",
                false
        );

        when(repository.save(any(User.class)))
                .thenReturn(savedUser);

        User result = service.register(
                "test@test.com",
                "password123",
                "John"
        );

        assertEquals(1, result.getId());
        assertEquals("test@test.com", result.getEmail());

        verify(passwordEncoder)
                .encode("password123");

        verify(repository)
                .save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {

        User existing = new User(
                1,
                "test@test.com",
                "password",
                "John",
                false
        );

        when(repository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(existing));

        assertThrows(
                ConflictException.class,
                () -> service.register(
                        "test@test.com",
                        "password123",
                        "John"
                )
        );

        verify(repository, never())
                .save(any());
    }

    @Test
    void shouldHashPasswordBeforeSaving() {

        when(repository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("HASHED");

        User savedUser = new User(
                1,
                "test@test.com",
                "HASHED",
                "John",
                false
        );

        when(repository.save(any(User.class)))
                .thenReturn(savedUser);

        service.register(
                "test@test.com",
                "password123",
                "John"
        );

        verify(passwordEncoder)
                .encode("password123");
    }
}