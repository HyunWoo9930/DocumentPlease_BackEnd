package org.example.docuementplease.repository;

import org.example.docuementplease.domain.Documents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository  extends JpaRepository<Documents, Long> {

}
