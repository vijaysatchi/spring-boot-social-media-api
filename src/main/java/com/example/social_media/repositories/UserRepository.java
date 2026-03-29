package com.example.social_media.repositories;

import com.example.social_media.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    @Query("Select u.id from User u where u.email = :email")
    Optional<Long> findIdByEmail(@Param(value = "email") String email);
    Optional<String> findEmailById(long id);
}
