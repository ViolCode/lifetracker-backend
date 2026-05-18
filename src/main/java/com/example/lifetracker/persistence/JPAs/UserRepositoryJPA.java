package com.example.lifetracker.persistence.JPAs;

import com.example.lifetracker.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepositoryJPA
        extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);
}