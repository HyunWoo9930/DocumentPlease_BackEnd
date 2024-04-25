package org.example.docuementplease.service;

import org.example.docuementplease.domain.Documents;
import org.example.docuementplease.repository.DocumentRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void documentSave(Documents documents) {
        documentRepository.save(documents);
    }
}
