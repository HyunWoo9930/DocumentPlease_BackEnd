package org.example.docuementplease.service;

import org.example.docuementplease.domain.TicketHistory;
import org.example.docuementplease.domain.TicketType;
import org.example.docuementplease.domain.User;
import org.example.docuementplease.repository.TicketRepository;
import org.example.docuementplease.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public void createTicketHistory(String user_name, TicketType ticket_type) {
        User user = userRepository.findByUsername(user_name).orElseThrow(() -> new NotFoundException("유저가 없습니다."));
        TicketHistory ticketHistory = new TicketHistory();
        ticketHistory.setTicketType(ticket_type);
        ticketHistory.setUser(user);
        ticketHistorySave(ticketHistory);
    }

    public List<TicketHistory> getTicketHistory(String user_name) {
        User user = userRepository.findByUsername(user_name).orElseThrow(() -> new NotFoundException("유저가 없습니다."));
        return ticketRepository.findByUser_Id(user.getId());
    }

    public void ticketHistorySave(TicketHistory ticketHistory) {
        ticketRepository.save(ticketHistory);
    }


}
