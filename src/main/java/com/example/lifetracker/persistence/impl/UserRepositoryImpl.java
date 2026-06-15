package com.example.lifetracker.persistence.impl;

import com.example.lifetracker.domains.User;
import com.example.lifetracker.mappers.UserMapper;
import com.example.lifetracker.persistence.JPAs.UserRepositoryJPA;
import com.example.lifetracker.persistence.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserRepositoryJPA jpa;
    private final UserMapper mapper;

    public UserRepositoryImpl(
            UserRepositoryJPA jpa,
            UserMapper mapper
    ) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(
                jpa.save(
                        mapper.toEntity(user)
                )
        );
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return jpa.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<User> searchUsers(String query) {
        return jpa.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}