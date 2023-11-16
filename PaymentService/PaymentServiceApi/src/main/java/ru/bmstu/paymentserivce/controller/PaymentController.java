package ru.bmstu.paymentserivce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.paymentserivce.dto.PaymentResponseDto;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("${paymentapi.url.base}")
public interface PaymentController {

    @GetMapping
    ResponseEntity<?> getPayments(@RequestParam List<UUID> paymentUids);

    @GetMapping("/{paymentUid}")
    ResponseEntity<?> getPayment(@PathVariable UUID paymentUid);

    @PostMapping
    ResponseEntity<PaymentResponseDto> createPayment(@RequestBody int price);

    @DeleteMapping("/{paymentUid}")
    ResponseEntity<?> cancelPayment(@PathVariable UUID paymentUid);

}
