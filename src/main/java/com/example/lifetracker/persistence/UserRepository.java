package com.example.lifetracker.persistence;

import com.example.lifetracker.domains.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
}