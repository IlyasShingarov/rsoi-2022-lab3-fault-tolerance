package ru.bmstu.gatewayservice.service;

import org.springframework.stereotype.Service;
import ru.bmstu.gatewayservice.dto.CarRentDto;
import ru.bmstu.gatewayservice.dto.car.CarDto;
import ru.bmstu.gatewayservice.dto.rental.RentalCreateDto;
import ru.bmstu.gatewayservice.dto.rental.RentalDto;
import ru.bmstu.gatewayservice.dto.wrapper.PageableWrapperDto;

import java.util.List;
import java.util.UUID;

@Service
public interface GatewayService {
    PageableWrapperDto<CarDto> getAllCars(boolean showAll, int page, int size);

    List<RentalDto> getRental(String username);

    RentalDto getRental(String username, UUID rentalUid);

    RentalCreateDto bookCar(String userName, CarRentDto carRentDto);

    void finishRental(String username, UUID rentalUid);

    void cancelRental(String username, UUID rentalUid);
}
