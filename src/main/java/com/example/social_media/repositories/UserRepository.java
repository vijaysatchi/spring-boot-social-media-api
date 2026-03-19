package com.example.social_media.repositories;

import com.example.social_media.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
