package com.example.lifetracker.mappers;

import com.example.lifetracker.domains.User;
import com.example.lifetracker.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {

        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getName(),
                entity.getIsPremium()
        );
    }

    public UserEntity toEntity(User user) {

        UserEntity entity = new UserEntity();

        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setName(user.getName());
        entity.setIsPremium(user.getIsPremium());

        return entity;
    }
}