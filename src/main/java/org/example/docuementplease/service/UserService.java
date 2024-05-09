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

    public int deductDailyTickets(String userName, int usedDailyTicketCount) {
        Optional<User> user = findUserbyUsername(userName);
        if (user.isEmpty()) {
            throw new RuntimeException("User를 찾지 못하였습니다.");
        } else {
            int tickets = user.get().getDaily_tickets() - usedDailyTicketCount;
            if (tickets < 0) {
                throw new RuntimeException("잔여 티켓 수가 0보다 작습니다.");
            }
            user.get().setDaily_tickets(tickets);
            userSave(user.get());
            return tickets;
        }
    }

    public int deductPaidTickets(String userName, int usedPaidTicketCount) {
        Optional<User> user = findUserbyUsername(userName);
        if (user.isEmpty()) {
            throw new RuntimeException("User를 찾지 못하였습니다.");
        } else {
            int tickets = user.get().getDaily_tickets() - usedPaidTicketCount;
            if (tickets < 0) {
                throw new RuntimeException("잔여 티켓 수가 0보다 작습니다.");
            }
            user.get().setDaily_tickets(tickets);
            userSave(user.get());
            return tickets;
        }
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

    public int deductFreeTickets(String userName, int usedFreeTicketCount) {
        Optional<User> user = findUserbyUsername(userName);
        if (user.isEmpty()) {
            throw new RuntimeException("User를 찾지 못하였습니다.");
        } else {
            int tickets = user.get().getFree_tickets() - usedFreeTicketCount;
            if (tickets < 0) {
                throw new RuntimeException("잔여 티켓 수가 0보다 작습니다.");
            }
            user.get().setFree_tickets(tickets);
            userSave(user.get());
            return tickets;
        }
    }

    public int returnFreeTickets(String userName) {
        Optional<User> user = findUserbyUsername(userName);
        if (user.isEmpty()) {
            throw new RuntimeException("User를 찾지 못하였습니다.");
        } else {
            int tickets = user.get().getFree_tickets();
            return tickets;
        }
    }

    public int returnPaidTickets(String userName) {
        Optional<User> user = findUserbyUsername(userName);
        if(user.isEmpty()) {
            throw new RuntimeException("user를 찾지 못하였습니다.");
        } else {
            int tickets = user.get().getPaid_tickets();
            return tickets;
        }
    }

    public int returnFreeAsk(String userName) {
        Optional<User> user = findUserbyUsername(userName);
        if(user.isEmpty()) {
            throw new RuntimeException("user를 찾지 못하였습니다.");
        } else {
            int tickets = user.get().getPaid_tickets();
            return tickets;
        }
    }
}

