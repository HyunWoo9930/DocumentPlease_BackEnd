package org.example.docuementplease.service;

import org.example.docuementplease.domain.Documents;
import org.example.docuementplease.domain.User;
import org.example.docuementplease.exceptionHandler.DocumentSaveException;
import org.example.docuementplease.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final DocumentService documentService;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, DocumentService documentService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.documentService = documentService;
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

    public void saveDocument(String user_name, String document_name, String type, String content, String target, String text, int amount) {
        Documents document = new Documents();
        document.setName(document_name);
        document.setTarget(target);
        document.setType(type);
        document.setAmount(amount);
        document.setContent(content);
        document.setText(text);

        Optional<User> user = userRepository.findByUsername(user_name);
        if (user.isPresent()) {
            user.get().getDocuments().add(document);
            document.setUser(user.get());
            documentService.documentSave(document);
            userSave(user.get());
        } else {
            throw new DocumentSaveException("user를 찾지 못하였습니다.");
        }
    }
}

