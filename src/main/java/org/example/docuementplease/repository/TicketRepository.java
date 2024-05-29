package org.example.docuementplease.repository;

import org.example.docuementplease.domain.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<TicketHistory, Long> {
    List<TicketHistory> findByUser_Id(Long user_id);
}
