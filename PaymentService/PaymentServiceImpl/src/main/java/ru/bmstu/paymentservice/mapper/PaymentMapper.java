package ru.bmstu.paymentservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bmstu.paymentserivce.dto.PaymentResponseDto;
import ru.bmstu.paymentservice.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentResponseDto toResponse(Payment entity);
}

