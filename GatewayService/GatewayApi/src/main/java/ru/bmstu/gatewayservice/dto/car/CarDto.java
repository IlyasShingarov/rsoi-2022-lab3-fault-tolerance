package ru.bmstu.gatewayservice.dto.car;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarDto extends BaseCarDto {
    int power;
    String type;
    int price;
    boolean available;
}
