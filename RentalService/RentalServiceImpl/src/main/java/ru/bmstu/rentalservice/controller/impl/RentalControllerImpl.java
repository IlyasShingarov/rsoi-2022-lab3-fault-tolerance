package ru.bmstu.rentalservice.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.bmstu.rentalapi.controller.RentalController;
import ru.bmstu.rentalapi.dto.RentalRequestDto;
import ru.bmstu.rentalapi.dto.RentalResponseDto;
import ru.bmstu.rentalservice.service.RentalService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RentalControllerImpl implements RentalController {

    private final RentalService rentalService;

    @Override
    public RentalResponseDto createRental(String username, RentalRequestDto request) {
        log.info("Request for creating rental for user '{}'", username);
        return rentalService.createRental(username, request);
    }

    @Override
    public ResponseEntity<List<RentalResponseDto>> getRentals(String username) {
        log.info("Request for reading all rental of user {}", username);
        return ResponseEntity.ok(rentalService.getRentals(username));
    }

    @Override
    public ResponseEntity<RentalResponseDto> getRental(UUID rentalUid, String username) {
        log.info("Request for reading rental {} of user {}", rentalUid, username);
        var response = rentalService.getRental(rentalUid, username);
        response.ifPresent(res -> log.debug(res.toString()));
        return response
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @Override
    public ResponseEntity<?> cancelRental(UUID rentalUid, String username) {
        log.info("Request for cancelling rental '{}' for user '{}'", rentalUid, username);
        rentalService.cancelRental(rentalUid, username);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> finishRental(UUID rentalUid, String username) {
        log.info("Request for finish rental '{}' for user '{}'", rentalUid, username);
        rentalService.finishRental(rentalUid, username);
        return ResponseEntity.noContent().build();
    }
}
