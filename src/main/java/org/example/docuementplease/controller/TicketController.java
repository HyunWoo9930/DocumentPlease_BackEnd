package org.example.docuementplease.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.docuementplease.domain.TicketHistory;
import org.example.docuementplease.domain.TicketType;
import org.example.docuementplease.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/create_ticket_history")
    public ResponseEntity<?> createTicketHistory(
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "ticket_type") TicketType ticket_type
    ) {
        try {
            ticketService.createTicketHistory(user_name, ticket_type);
            return ResponseEntity.ok("성공적으로 저장하였습니다.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/get_ticket_history")
    public ResponseEntity<?> getTicketHistory(
            @RequestParam(value = "user_name") String user_name
    ) {
        try {
            List<TicketHistory> ticketHistory = ticketService.getTicketHistory(user_name);
            return ResponseEntity.ok(ticketHistory);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
