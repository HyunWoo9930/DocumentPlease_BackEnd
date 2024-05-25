package org.example.docuementplease.repository;

import jakarta.transaction.Transactional;
import org.example.docuementplease.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long user_id);

    @Transactional
    void deleteUserByUsername(String username);
}
