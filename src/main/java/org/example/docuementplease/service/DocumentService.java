package org.example.docuementplease.service;

import lombok.extern.slf4j.Slf4j;
import org.example.docuementplease.domain.*;
import org.example.docuementplease.repository.DocumentRepository;
import org.example.docuementplease.repository.RefundedDocumentRepository;
import org.example.docuementplease.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DocumentService {
    private final DocumentRepository documentRepository;

    private final RefundedDocumentRepository refundedDocumentRepository;

    private final UserRepository userRepository;

    public DocumentService(DocumentRepository documentRepository, RefundedDocumentRepository refundedDocumentRepository, UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.refundedDocumentRepository = refundedDocumentRepository;
        this.userRepository = userRepository;
    }

    public Documents documentSave(Documents documents) {
        return documentRepository.save(documents);
    }

    public List<Documents> returncat(Long id, String type) {
        return documentRepository.findDocumentsByUser_IdAndType(id, type);
    }

    public Optional<Documents> findDocumentsById(Long id) {
        return documentRepository.findById(id);
    }

    public List<Documents> findDocumentsByUserId(Long user_id) {
        return documentRepository.findAllByUser_Id(user_id);
    }

    public void deleteDoc(Long id, String name) {
        documentRepository.deleteDocumentsByUser_IdAndName(id, name);
    }

    public Optional<Documents> findDocumentByNameAndUserId(String name, Long id) {
        return documentRepository.findDocumentsByNameAndUser_Id(name, id);
    }

    public void updateIsShared(Long user_id, String document_name) {
        Optional<Documents> document = documentRepository.findDocumentsByNameAndUser_Id(document_name, user_id);
        if (document.isPresent()) {
            document.get().setIsShared(!document.get().getIsShared());
            documentSave(document.get());
        } else {
            throw new NotFoundException("문서가 존재하지 않습니다.");
        }
    }

    public List<SharedDocuments> sharedDocuments() {
        List<Documents> documents = documentRepository.findDocumentsByIsShared(true);
        if (documents.isEmpty()) {
            throw new RuntimeException("공유 가능한 문서를 찾지 못하였습니다.");
        } else {
            return documents.stream().map(document -> {
                User user = userRepository.findById(document.getUser().getId()).orElseThrow(() -> new RuntimeException("user를 찾지 못하였습니다."));
                return new SharedDocuments(document.getName(), user.getUsername(), document.getContent(), document.getTarget(), document.getLikeCount(), document.getType(), document.getComments());
            }).toList();
        }
    }

    public int updateLikeCount(String doc_name, String user_name) {
        User user = userRepository.findByUsername(user_name).orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
        Documents document = documentRepository.findDocumentsByNameAndUser_Id(doc_name, user.getId()).orElseThrow(() -> new NotFoundException("문서가 존재하지 않습니다."));
        int count = document.getLikeCount() + 1;
        document.setLikeCount(count);
        documentSave(document);
        return document.getLikeCount();
    }

    public int getLikeCount(String doc_name, String user_name) {
        Optional<User> user = userRepository.findByUsername(user_name);
        if (user.isPresent()) {
            Optional<Documents> document = documentRepository.findDocumentsByNameAndUser_Id(doc_name, user.get().getId());
            if (document.isPresent()) {
                return document.get().getLikeCount();
            }
            throw new NotFoundException("문서가 존재하지 않습니다.");
        } else {
            throw new NotFoundException("유저가 존재하지 않습니다.");
        }
    }

    public DocumentInputResponse getDocumentInput(Long doc_id) {
        Documents document = documentRepository.findById(doc_id).orElseThrow(() -> new NotFoundException("문서가 존재하지 않습니다."));
        return new DocumentInputResponse(document.getType(), document.getTarget(), document.getAmount(), document.getText());
    }

    public void updateDocumentName(String doc_name, String user_name, String new_doc_name) {
        User user = userRepository.findByUsername(user_name).orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
        Documents documents = documentRepository.findDocumentsByNameAndUser_Id(doc_name, user.getId()).orElseThrow(() -> new NotFoundException("문서가 존재하지 않습니다."));
        documents.setName(new_doc_name);
        documentSave(documents);
    }

    public void getAllTotalLikes() {
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            int totalLikes = returnTotalLikes(user);
            user.setTotalLikes(totalLikes);
            userRepository.save(user);
        }
    }

    public int returnTotalLikes(User user) {
        int likes = 0;
        for (Documents documents : user.getDocuments()) {
            likes += documents.getLikeCount();
        }
        return likes;
    }

    public int likeScore(User user) {
        int score = 1;
        List<User> all = userRepository.findAll();
        for (User users : all) {
            if (users.getTotalLikes() > user.getTotalLikes()) {
                score++;
            }
        }
        return score;
    }

    public String getTotalLikes(String user_name) {
        getAllTotalLikes();
        User user = userRepository.findByUsername(user_name).orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
        int score = likeScore(user);
        int total = userRepository.findAll().size();
        return user.getTotalLikes() + " \n" + score + "/" +total;
    }

    public void saveRefundedDoc(String content, String sendContent, String user_name) {
        User user = userRepository.findByUsername(user_name).orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
        RefundedDocument refundedDocument = new RefundedDocument();
        refundedDocument.setUser(user);
        refundedDocument.setContent(content);
        refundedDocument.setSendContent(sendContent);
        RefundedDocument save = refundedDocumentRepository.save(refundedDocument);
        if (refundedDocumentRepository.findById(save.getId()).isEmpty()) {
            throw new RuntimeException("저장실패");
        }
    }

    public List<RefundedDocument> getRefundedDocument() {
        return refundedDocumentRepository.findAll();
    }

    public long getDocId(String user_name, String doc_name) {
        User user = userRepository.findByUsername(user_name).orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
        Documents documents = documentRepository.findDocumentsByNameAndUser_Id(doc_name, user.getId()).orElseThrow(() -> new NotFoundException("문서가 존재하지 않습니다."));
        return documents.getId();

    }
}



