package ru.bmstu.gatewayservice.dto.payment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.bmstu.paymentserivce.constants.PaymentStatus;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDto {
    UUID paymentUid;
    PaymentStatus status;
    int price;
}
