package com.example.lifetracker.services;

import com.example.lifetracker.domains.User;
import com.example.lifetracker.persistence.UserRepository;
import com.example.lifetracker.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private CustomUserDetailsService service;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User(
                1,
                "test@test.com",
                "hashedPassword",
                "Test User",
                false
        );
    }

    @Test
    void loadUserByUsername_returnsUserPrincipal_whenUserExists() {
        when(repository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(sampleUser));

        UserDetails result = service.loadUserByUsername("test@test.com");

        assertNotNull(result);
        assertTrue(result instanceof UserPrincipal);
        assertEquals("test@test.com", result.getUsername());
        assertEquals("hashedPassword", result.getPassword());
        verify(repository).findByEmail("test@test.com");
    }

    @Test
    void loadUserByUsername_throwsUsernameNotFoundException_whenUserDoesNotExist() {
        when(repository.findByEmail("nonexistent@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("nonexistent@test.com"));
        verify(repository).findByEmail("nonexistent@test.com");
    }

    @Test
    void loadUserByUsername_throwsUsernameNotFoundException_whenEmailIsNull() {
        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername(null));
    }
}