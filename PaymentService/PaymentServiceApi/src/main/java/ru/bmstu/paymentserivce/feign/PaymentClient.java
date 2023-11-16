package ru.bmstu.paymentserivce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.paymentserivce.dto.PaymentResponseDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "payment", url = "${feign.payment.url}")
public interface PaymentClient {
    @GetMapping(path = "${paymentapi.url.base}")
    List<PaymentResponseDto> getPayments(@RequestParam List<UUID> paymentsUids);

    @GetMapping(path = "${paymentapi.url.base}/{paymentUid}")
    PaymentResponseDto getPayment(@PathVariable UUID paymentUid);

    @PostMapping(path = "${paymentapi.url.base}")
    PaymentResponseDto createPayment(@RequestBody int price);

    @DeleteMapping(path = "${paymentapi.url.base}/{paymentUid}")
    void cancelPayment(@PathVariable UUID paymentUid);
}
