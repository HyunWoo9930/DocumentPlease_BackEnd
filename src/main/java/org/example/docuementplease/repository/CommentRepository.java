package org.example.docuementplease.repository;

import org.example.docuementplease.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDocumentsId(Long doc_id);
}
