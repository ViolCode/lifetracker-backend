package com.example.lifetracker.persistence;

import com.example.lifetracker.domains.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Integer id);
    List<User> searchUsers(String query);
}