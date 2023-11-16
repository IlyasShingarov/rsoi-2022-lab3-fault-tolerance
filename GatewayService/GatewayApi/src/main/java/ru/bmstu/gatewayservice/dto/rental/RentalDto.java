package ru.bmstu.gatewayservice.dto.rental;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.bmstu.gatewayservice.dto.car.BaseCarDto;
import ru.bmstu.gatewayservice.dto.payment.PaymentDto;
import ru.bmstu.rentalapi.constants.RentalStatus;

import java.time.LocalDate;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentalDto {
    UUID rentalUid;
    RentalStatus status;
    LocalDate dateFrom;
    LocalDate dateTo;
    BaseCarDto car;
    PaymentDto payment;
}
