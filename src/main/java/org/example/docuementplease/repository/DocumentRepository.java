package org.example.docuementplease.repository;

import org.example.docuementplease.domain.Documents;
import org.example.docuementplease.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository  extends JpaRepository<Documents, Long> {

}
