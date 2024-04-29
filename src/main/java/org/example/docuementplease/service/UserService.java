package org.example.docuementplease.service;

import org.example.docuementplease.domain.User;
import org.example.docuementplease.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerNewUserAccount(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean hasUserID(String userName) {
        Optional<User> user= userRepository.findByUsername(userName);
        return user.isPresent();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String userName) {
        userRepository.deleteUserByUsername(userName);
    }

    public Optional<User> findUserbyUsername(String userName) {
        return userRepository.findByUsername(userName);
    }

    public void userSave(User user) {
        userRepository.save(user);
    }

    public boolean login(String id, String password) {
        Optional<User> user = userRepository.findByUsername(id);
        return user.isPresent() && passwordEncoder.matches(password, user.get().getPassword());
    }
}

