package ru.bmstu.gatewayservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.gatewayservice.dto.CarRentDto;
import ru.bmstu.gatewayservice.dto.car.CarDto;
import ru.bmstu.gatewayservice.dto.rental.RentalCreateDto;
import ru.bmstu.gatewayservice.dto.rental.RentalDto;
import ru.bmstu.gatewayservice.dto.wrapper.PageableWrapperDto;

import java.util.List;
import java.util.UUID;

import static ru.bmstu.gatewayservice.constants.CustomHeaders.USERNAME_HEADER;

@RestController
public interface GatewayConroller {
    @GetMapping("${services.cars.url}")
    PageableWrapperDto<CarDto> getAllCars(@RequestParam boolean showAll,
                                          @RequestParam int page,
                                          @RequestParam int size);

    @GetMapping("${services.rental.url}")
    ResponseEntity<List<RentalDto>> getRental(@RequestHeader(USERNAME_HEADER) String username);

    @GetMapping("${services.rental.url}/{rentalUid}")
    ResponseEntity<RentalDto> getRental(@RequestHeader(USERNAME_HEADER) String username,
                                        @PathVariable UUID rentalUid);

    @PostMapping("${services.rental.url}")
    ResponseEntity<RentalCreateDto> bookCar(@RequestHeader(USERNAME_HEADER) String userName,
                                            @RequestBody @Valid CarRentDto carBookDto);

    @PostMapping("${services.rental.url}/{rentalUid}/finish")
    ResponseEntity<?> finishRental(@RequestHeader(USERNAME_HEADER) String username,
                                   @PathVariable UUID rentalUid);

    @DeleteMapping("${services.rental.url}/{rentalUid}")
    ResponseEntity<?> cancelRental(@RequestHeader(USERNAME_HEADER) String username,
                                   @PathVariable UUID rentalUid);
}
