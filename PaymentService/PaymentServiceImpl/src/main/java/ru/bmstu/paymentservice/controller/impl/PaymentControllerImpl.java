package ru.bmstu.paymentservice.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.bmstu.paymentserivce.controller.PaymentController;
import ru.bmstu.paymentserivce.dto.PaymentResponseDto;
import ru.bmstu.paymentservice.service.PaymentService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentControllerImpl implements PaymentController {
    private final PaymentService paymentService;

    @Override
    public ResponseEntity<?> getPayments(List<UUID> paymentsUids) {
        log.info("Request for reading payments with uuids {}", paymentsUids);

        return ResponseEntity.ok(paymentService.getPayments(paymentsUids));
    }

    @Override
    public ResponseEntity<?> getPayment(UUID paymentUid) {
        log.info("Request for reading payment {}", paymentUid);

        Optional<PaymentResponseDto> payment = paymentService.getPayment(paymentUid);

        return payment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<PaymentResponseDto> createPayment(int price) {
        log.info("Request for creating payment with price {}", price);

        return ResponseEntity.ok(paymentService.createPayment(price));
    }

    @Override
    public ResponseEntity<?> cancelPayment(UUID paymentUid) {
        log.info("Request for cancelling payment {}", paymentUid);

        paymentService.cancelPayment(paymentUid);
        return ResponseEntity.noContent().build();
    }
}
