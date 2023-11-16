package ru.bmstu.paymentserivce.dto;

import ru.bmstu.paymentserivce.constants.PaymentStatus;

import java.util.UUID;

public record PaymentResponseDto(
        UUID paymentUid,
        PaymentStatus status,
        int price
) {
}
