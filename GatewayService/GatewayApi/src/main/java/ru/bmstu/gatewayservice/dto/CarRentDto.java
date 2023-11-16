package ru.bmstu.gatewayservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarRentDto {

    @NotNull(message = "CarUid should not be null")
    UUID carUid;

    @NotNull(message = "DateFrom should not be null")
    private LocalDate dateTo;

    @NotNull(message = "DateTo should not be null")
    private LocalDate dateFrom;
}
