package ru.bmstu.paymentservice.service;

import org.springframework.stereotype.Service;
import ru.bmstu.paymentserivce.dto.PaymentResponseDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface PaymentService {
    PaymentResponseDto createPayment(int price);

    List<PaymentResponseDto> getPayments(List<UUID> paymentsUids);

    void cancelPayment(UUID paymentUid);

    Optional<PaymentResponseDto> getPayment(UUID paymentUid);
}
