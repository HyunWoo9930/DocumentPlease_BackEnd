package org.example.docuementplease.service;

import jakarta.transaction.Transactional;
import org.example.docuementplease.domain.*;
import org.example.docuementplease.exceptionHandler.DocumentSaveException;
import org.example.docuementplease.exceptionHandler.LowerLevelExistException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final DocumentService documentService;

    private final PaymentService paymentService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, DocumentService documentService, PasswordEncoder passwordEncoder, PaymentService paymentService) {
        this.userRepository = userRepository;
        this.documentService = documentService;
        this.passwordEncoder = passwordEncoder;
        this.paymentService = paymentService;
    }

    public User registerNewUserAccount(User user) {
        List<Level> levels = new ArrayList<>(Arrays.asList(Level.values()));
        user.setLevels(levels);
        List<LikeLevel> likeLevels = new ArrayList<>(Arrays.asList(LikeLevel.values()));
        user.setLikeLevels(likeLevels);
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
            int tickets = user.get().getPaidTickets() - usedPaidTicketCount;
            if (tickets < 0) {
                throw new RuntimeException("잔여 티켓 수가 0보다 작습니다.");
            }
            user.get().setPaidTickets(tickets);
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
            int tickets = user.get().getFreeTickets() - usedFreeTicketCount;
            if (tickets < 0) {
                throw new RuntimeException("잔여 티켓 수가 0보다 작습니다.");
            }
            user.get().setFreeTickets(tickets);
            userSave(user.get());
            return tickets;
        }
    }

    public int returnFreeTickets(String userName) {
        Optional<User> user = findUserbyUsername(userName);
        if (user.isEmpty()) {
            throw new RuntimeException("User를 찾지 못하였습니다.");
        } else {
            int tickets = user.get().getFreeTickets();
            return tickets;
        }
    }

    public int returnPaidTickets(String userName) {
        Optional<User> user = findUserbyUsername(userName);
        if (user.isEmpty()) {
            throw new RuntimeException("user를 찾지 못하였습니다.");
        } else {
            int tickets = user.get().getPaidTickets();
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

    public Long savePayment(String username, int tickets, int price) {
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setTicket(tickets);
        paymentHistory.setPrice(price);

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            user.get().getPaymentHistory().add(paymentHistory);
            paymentHistory.setUser(user.get());
            paymentHistory = paymentService.paymentSave(paymentHistory);
            int paidTickets = user.get().getPaidTickets() + tickets;
            user.get().setPaidTickets(paidTickets);
            userSave(user.get());
            return paymentHistory.getId();
        } else {
            throw new DocumentSaveException("user를 찾지 못하였습니다.");
        }
    }

    public List<PaymentHistoryResponse> returnUserPayment(String username) {
        User user = findUserbyUsername(username)
                .orElseThrow(() -> new RuntimeException("user를 찾지 못하였습니다."));

        return paymentService.returnPaymentHistory(user.getId())
                .stream().map(history -> {
                    return new PaymentHistoryResponse(history.getPaidTime(), history.getTicket(), history.getPrice());
                }).toList();
    }

    public int getDocumentCreateCount(String user_name) {
        User user = userRepository.findByUsername(user_name).orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
        return user.getDocument_create_count();
    }

    public List<Boolean> getUserLevels(String user_name) {
        Optional<User> user = userRepository.findByUsername(user_name);
        if (user.isPresent()) {
            List<Boolean> isCleared = new ArrayList<>();
            for (Level level : Level.values()) {
                isCleared.add(user.get().getLevels().contains(level));
            }
            return isCleared;
        } else {
            throw new NotFoundException("유저가 존재하지 않습니다.");
        }
    }

    public List<Boolean> getUserLikeLevels(String user_name) {
        Optional<User> user = userRepository.findByUsername(user_name);
        if (user.isPresent()) {
            List<Boolean> isCleared = new ArrayList<>();
            for (LikeLevel level : LikeLevel.values()) {
                isCleared.add(user.get().getLikeLevels().contains(level));
            }
            return isCleared;
        } else {
            throw new NotFoundException("유저가 존재하지 않습니다.");
        }
    }

    @Transactional
    public void updateUserLevels(String user_name, Level levelToRemove) {
        User user = userRepository.findByUsername(user_name)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        if(!user.getLevels().contains(levelToRemove)) {
            throw new RuntimeException("이미 제거된 레벨입니다!");
        }

        for (Level level : Level.values()) {
            if (level.compareTo(levelToRemove) < 0 && user.getLevels().contains(level)) {
                throw new LowerLevelExistException("제거하려는 레벨보다 낮은 레벨이 존재합니다.");
            }
        }

        user.removeLevel(levelToRemove);
        userSave(user);
    }

    @Transactional
    public void updateUserLikeLevels(String user_name, LikeLevel likeLevelToRemove) {
        User user = userRepository.findByUsername(user_name)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        if(!user.getLikeLevels().contains(likeLevelToRemove)) {
            throw new RuntimeException("이미 제거된 레벨입니다!");
        }

        for (LikeLevel likeLevel : LikeLevel.values()) {
            if (likeLevel.compareTo(likeLevelToRemove) < 0 && user.getLevels().contains(likeLevel)) {
                throw new LowerLevelExistException("제거하려는 레벨보다 낮은 레벨이 존재합니다.");
            }
        }

        user.removeLikeLevel(likeLevelToRemove);
        userSave(user);
    }

    public int plusFreeTickets(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user를 찾지 못하였습니다."));

        int tickets = user.getFreeTickets() + 1;
        user.setFreeTickets(tickets);
        userSave(user);
        return tickets;
    }

    public Long getUserId(String user_name) {
        return userRepository.findByUsername(user_name).orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다.")).getId();
    }

    public void changeNickName(String user_name, String nick_name) {
        User user = userRepository.findByUsername(user_name)
                .orElseThrow(() -> new RuntimeException("user를 찾지 못하였습니다."));
        user.setNickName(nick_name);
        userRepository.save(user);
    }

    public String returnGetNickname(String nick_name) {
        Optional<User> user = findUserbyUsername(nick_name);
        if (user.isEmpty()) {
            throw new RuntimeException("닉네임을 설정해주세요.");
        } else {
            return user.get().getNickName();
        }
    }

    public void hasEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new RuntimeException("이메일이 이미 존재합니다.");
        }
    }

    public void putChangePassword(String user_name,String password, String new_password) {
        User user = userRepository.findByUsername(user_name)
                .orElseThrow(() -> new RuntimeException("user를 찾지 못하였습니다."));
        if (passwordEncoder.matches(password, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(new_password));
            userRepository.save(user);
        } else {
            throw new RuntimeException("기존 비밀번호가 일치하지 않습니다");
        }
    }

}


