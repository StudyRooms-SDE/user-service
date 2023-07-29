package com.sde.project.user.repositories;

import com.sde.project.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u FROM users u WHERE u.username = ?1")
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM users u WHERE u.email = ?1")
    Optional<Object> findByEmail(String email);
}
