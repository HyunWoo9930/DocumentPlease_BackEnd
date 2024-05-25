package org.example.docuementplease.service;

import org.example.docuementplease.domain.Documents;
import org.example.docuementplease.domain.PaymentHistory;
import org.example.docuementplease.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentHistory paymentSave(PaymentHistory paymentHistory) {
        return paymentRepository.save(paymentHistory);
    }

    public List<PaymentHistory> returnPaymentHistory(Long user_id) {
        return paymentRepository.findByUser_Id(user_id);
    }


}
