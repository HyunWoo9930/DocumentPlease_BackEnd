package org.example.docuementplease.service;

import org.example.docuementplease.domain.Documents;
import org.example.docuementplease.domain.SharedDocuments;
import org.example.docuementplease.domain.User;
import org.example.docuementplease.repository.DocumentRepository;
import org.example.docuementplease.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    private final UserRepository userRepository;

    public DocumentService(DocumentRepository documentRepository, UserRepository userRepository) {
        this.documentRepository = documentRepository;
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
            return documents
                    .stream().map(document -> {
                        User user = userRepository.findById(document.getUser().getId())
                                .orElseThrow(() -> new RuntimeException("user를 찾지 못하였습니다."));
                        return new SharedDocuments(document.getName(), user.getUsername(), document.getContent(), document.getTarget(), document.getLike_count());
                    }).toList();
        }
    }

    public int updateLikeCount(String doc_name, String user_name) {
        Optional<User> user = userRepository.findByUsername(user_name);
        if (user.isPresent()) {
            Optional<Documents> document = documentRepository.findDocumentsByNameAndUser_Id(doc_name, user.get().getId());
            if (document.isPresent()) {
                document.get().setLike_count(document.get().getLike_count() + 1);
                documentSave(document.get());
                return document.get().getLike_count();
            }
            throw new NotFoundException("문서가 존재하지 않습니다.");
        } else {
            throw new NotFoundException("유저가 존재하지 않습니다.");
        }
    }

    public int getLikeCount(String doc_name, String user_name) {
        Optional<User> user = userRepository.findByUsername(user_name);
        if (user.isPresent()) {
            Optional<Documents> document = documentRepository.findDocumentsByNameAndUser_Id(doc_name, user.get().getId());
            if (document.isPresent()) {
                return document.get().getLike_count();
            }
            throw new NotFoundException("문서가 존재하지 않습니다.");
        } else {
            throw new NotFoundException("유저가 존재하지 않습니다.");
        }
    }
}



