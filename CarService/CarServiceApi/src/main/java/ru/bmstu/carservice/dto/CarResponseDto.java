package ru.bmstu.carservice.dto;

import ru.bmstu.carservice.constants.CarType;

import java.util.UUID;

public record CarResponseDto(
        UUID carUid,
        String brand,
        String model,
        String registrationNumber,
        int power,
        CarType type,
        int price,
        boolean available
) {
}
