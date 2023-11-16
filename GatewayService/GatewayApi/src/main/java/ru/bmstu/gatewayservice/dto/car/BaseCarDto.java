package ru.bmstu.gatewayservice.dto.car;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseCarDto {
    UUID carUid;
    String brand;
    String model;
    String registrationNumber;
}
