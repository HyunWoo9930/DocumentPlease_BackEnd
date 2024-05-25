package org.example.docuementplease.repository;

import org.example.docuementplease.domain.Documents;
import org.example.docuementplease.domain.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findByUser_Id(Long user_id);
}
