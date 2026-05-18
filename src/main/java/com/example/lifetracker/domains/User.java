package com.example.lifetracker.domains;

import com.example.lifetracker.exceptions.ValidationException;
import lombok.Getter;

@Getter
public class User {

    private final Integer id;
    private String email;
    private String password;
    private String name;
    private Boolean isPremium;

    public User(
            Integer id,
            String email,
            String password,
            String name,
            Boolean isPremium
    ) {

        validate(email, password, name);

        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.isPremium = isPremium;
    }

    public User(
            String email,
            String password,
            String name
    ) {
        this(null, email, password, name, false);
    }

    private void validate(
            String email,
            String password,
            String name
    ) {

        if (email == null || email.isBlank()) {
            throw new ValidationException("Email is required");
        }

        if (password == null || password.length() < 6) {
            throw new ValidationException(
                    "Password must be at least 6 characters"
            );
        }

        if (name == null || name.isBlank()) {
            throw new ValidationException("Name is required");
        }
    }
}