package org.example.docuementplease.service;

import org.example.docuementplease.domain.DocumentInputResponse;
import org.example.docuementplease.domain.DocumentOutputResponse;
import org.example.docuementplease.domain.Documents;
import org.example.docuementplease.domain.User;
import org.example.docuementplease.exceptionHandler.DocumentSaveException;
import org.example.docuementplease.repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        Optional<User> user = userRepository.findByUsername(userName);
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

    public int updateDocumentCreateCount(String user_name) {
        User user = userRepository.findByUsername(user_name).orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
        int originCount = user.getDocument_create_count() + 1;
        user.setDocument_create_count(originCount);
        userSave(user);
        return originCount;
    }

    public Long saveDocInput(String user_name, String type, String target, String text, int amount) {
        Documents document = new Documents();
        document.setTarget(target);
        document.setType(type);
        document.setAmount(amount);
        document.setText(text);

        Optional<User> user = userRepository.findByUsername(user_name);
        if (user.isPresent()) {
            user.get().getDocuments().add(document);
            document.setUser(user.get());
            document = documentService.documentSave(document);
            userSave(user.get());
            return document.getId();
        } else {
            throw new DocumentSaveException("user를 찾지 못하였습니다.");
        }
    }

    public void changePassword(String user_name, String new_password) {
        User user = userRepository.findByUsername(user_name)
                .orElseThrow(() -> new RuntimeException("user를 찾지 못하였습니다."));
        if (passwordEncoder.matches(new_password, user.getPassword())) {
            throw new RuntimeException("새로운 비밀번호가 동일합니다.");
        } else {
            user.setPassword(passwordEncoder.encode(new_password));
            userRepository.save(user);
        }
    }

    public void saveDocOutput(Long doc_id, String document_name, String content, String user_name) {
        User user = findUserbyUsername(user_name)
                .orElseThrow(() -> new RuntimeException("user를 찾지 못하였습니다."));

        List<String> nameList = documentService.findDocumentsByUserId(user.getId()).stream().map(
                Documents::getName
        ).toList();

        String unique_name = document_name;

        int num = 0;
        for (int i = 0; i < nameList.size(); i++) {
            if (nameList.get(i) != null && nameList.get(i).equals(unique_name)) {
                unique_name = document_name + " - (" + ++num + ")";
            }
        }

        try {
            String decodedContent = URLDecoder.decode(content, "UTF-8");
            Optional<Documents> document = documentService.findDocumentsById(doc_id);
            if (document.isEmpty()) {
                throw new DocumentSaveException("문서가 존재하지 않습니다.");
            } else {
                document.get().setContent(decodedContent);
                document.get().setName(unique_name);
                documentService.documentSave(document.get());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
        if (user.isEmpty()) {
            throw new RuntimeException("user를 찾지 못하였습니다.");
        } else {
            int tickets = user.get().getPaid_tickets();
            return tickets;
        }
    }

    public int returnDailyFreeAsk(String userName) {
        Optional<User> user = findUserbyUsername(userName);
        if (user.isEmpty()) {
            throw new RuntimeException("user를 찾지 못하였습니다.");
        } else {
            return user.get().getDaily_tickets();
        }
    }

    public List<DocumentInputResponse> returnDocForInput(String user_name, String type) {
        User user = findUserbyUsername(user_name)
                .orElseThrow(() -> new RuntimeException("user를 찾지 못하였습니다."));

        return documentService.returncat(user.getId(), type)
                .stream().map(document -> {
                    return new DocumentInputResponse(document.getType(), document.getTarget(), document.getAmount(), document.getText());
                }).toList();
    }

    public List<DocumentOutputResponse> returnDocForOutput(String user_name, String type) {
        User user = findUserbyUsername(user_name)
                .orElseThrow(() -> new RuntimeException("user를 찾지 못하였습니다."));

        return documentService.returncat(user.getId(), type)
                .stream().map(document -> {
                    return new DocumentOutputResponse(document.getName(), document.getContent());
                }).toList();
    }

    public String findId(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user를 찾지 못하였습니다."));
        return user.getUsername();
    }


    public void deleteDoc(String user_name, String name) {
        User user = findUserbyUsername(user_name).orElseThrow(() -> new NotFoundException("유저를 찾지 못하였습니다."));
        if (documentService.findDocumentByNameAndUserId(name, user.getId()).isEmpty()) {
            throw new NotFoundException("문서가 존재하지 않습니다.");
        }
        documentService.deleteDoc(user.getId(), name);
        if (documentService.findDocumentByNameAndUserId(name, user.getId()).isPresent()) {
            throw new RuntimeException("삭제가 되지 않았습니다.");
        }
    }

    public void saveProfileImage(MultipartFile file, String user_name) throws IOException {
        Path uploadPath = Paths.get("./profile/" + user_name);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        if (Files.list(uploadPath).findAny().isPresent()) {
            throw new IOException("이미 다른 프로필 사진이 존재합니다.");
        }

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        User user = userRepository.findByUsername(user_name).orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));
        user.setProfileUrl(filePath.toString());
        userSave(user);
    }

    public void updateProfileImage(MultipartFile file, String userName) throws IOException {
        Optional<User> userOptional = userRepository.findByUsername(userName);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            Path oldFilePath = Paths.get(user.getProfileUrl());
            Files.deleteIfExists(oldFilePath);

            String fileName = file.getOriginalFilename();
            Path newFilePath = Paths.get("./profile/" + userName).resolve(fileName);
            Files.copy(file.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

            user.setProfileUrl(newFilePath.toString());
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with username: " + userName);
        }
    }

    public void deleteProfileImage(String user_name) throws IOException {
        Optional<User> userOptional = userRepository.findByUsername(user_name);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getProfileUrl() == null) {
                throw new IOException("프로필 사진이 존재하지 않습니다. username : " + user_name);
            }

            Path oldFilePath = Paths.get(user.getProfileUrl());
            Files.deleteIfExists(oldFilePath);
            user.setProfileUrl(null);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with username: " + user_name);
        }
    }

    public Resource loadProfileImage(String user_name) throws MalformedURLException {
        Optional<User> user = userRepository.findByUsername(user_name);
        if (user.isPresent()) {
            Path filePath = Paths.get(user.get().getProfileUrl());
            System.out.println("filePath = " + filePath);
            Resource resource = new UrlResource(filePath.toUri());
            System.out.println("resource = " + resource);
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filePath);
            }
        } else {
            throw new RuntimeException("User not found with id: " + user_name);
        }
    }
}


