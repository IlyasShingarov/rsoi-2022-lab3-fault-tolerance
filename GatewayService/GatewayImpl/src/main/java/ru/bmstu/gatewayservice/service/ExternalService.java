package ru.bmstu.gatewayservice.service;

import org.springframework.data.domain.Page;
import ru.bmstu.carservice.dto.CarResponseDto;
import ru.bmstu.gatewayservice.dto.car.CarDto;
import ru.bmstu.gatewayservice.wrapper.FallbackWrapper;
import ru.bmstu.paymentserivce.dto.PaymentResponseDto;
import ru.bmstu.rentalapi.dto.RentalRequestDto;
import ru.bmstu.rentalapi.dto.RentalResponseDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ExternalService {
    Page<CarDto> getCars(boolean showAll, int page, int size);

    CarResponseDto getCar(UUID carUid);

    boolean changeCarAvailability(UUID carId, boolean availability);

    FallbackWrapper<PaymentResponseDto> createPayment(int totalPrice);

    FallbackWrapper<RentalResponseDto> createRental(String username, RentalRequestDto rentalInDto);

    boolean cancelRental(String username, UUID rentalUid);

    List<RentalResponseDto> getRentals(String username);

    FallbackWrapper<RentalResponseDto> getRental(String username, UUID rentalUid);

    boolean cancelPayment(UUID paymentsUid);

    Map<UUID, PaymentResponseDto> getPayments(List<UUID> pyamentsUids);

    Map<UUID, CarResponseDto> getCars(List<UUID> carUids);

    PaymentResponseDto getPayment(UUID paymentUid);

    boolean finishRental(UUID rentalUid, String username);
}
