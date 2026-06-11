package com.example.lifetracker.domains;

import com.example.lifetracker.exceptions.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateValidUser() {

        User user = new User(
                "test@test.com",
                "password123",
                "John"
        );

        assertEquals(
                "test@test.com",
                user.getEmail()
        );

        assertEquals(
                "John",
                user.getName()
        );

        assertFalse(
                user.getIsPremium()
        );
    }

    @Test
    void shouldThrowWhenEmailIsBlank() {

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> new User(
                                "",
                                "password123",
                                "John"
                        )
                );

        assertEquals(
                "Email is required",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowWhenPasswordTooShort() {

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> new User(
                                "test@test.com",
                                "123",
                                "John"
                        )
                );

        assertEquals(
                "Password must be at least 6 characters",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowWhenNameIsBlank() {

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> new User(
                                "test@test.com",
                                "password123",
                                ""
                        )
                );

        assertEquals(
                "Name is required",
                exception.getMessage()
        );
    }

    @Test
    void shouldCreateUserWithIdConstructor() {

        User user = new User(
                10,
                "test@test.com",
                "password123",
                "John",
                true
        );

        assertEquals(10, user.getId());
        assertTrue(user.getIsPremium());
    }
}