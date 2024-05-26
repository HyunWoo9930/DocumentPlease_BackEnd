package org.example.docuementplease.service;

import org.example.docuementplease.domain.Documents;
import org.example.docuementplease.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
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
        if(document.isPresent()) {
            document.get().setIsShared(!document.get().getIsShared());
            documentSave(document.get());
        } else {
            throw new NotFoundException("문서가 존재하지 않습니다.");
        }
    }
}



