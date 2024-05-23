package org.example.docuementplease.repository;

import jakarta.transaction.Transactional;
import org.example.docuementplease.domain.Documents;
import org.example.docuementplease.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository  extends JpaRepository<Documents, Long> {
    List<Documents> findDocumentsByUser_IdAndType(Long user_id, String type);
    List<Documents> findAllByUser_Id(Long user_id);

    @Transactional
    void deleteDocumentsByUser_IdAndName(Long id, String name);
}
