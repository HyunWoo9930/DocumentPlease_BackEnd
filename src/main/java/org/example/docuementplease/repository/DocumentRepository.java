package org.example.docuementplease.repository;

import org.example.docuementplease.domain.Documents;
import org.example.docuementplease.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository  extends JpaRepository<Documents, Long> {
    List<Documents> findDocumentsByUser_IdAndType(Long user_id, String type);
}
