package ru.bmstu.rentalservice.service;

import org.springframework.stereotype.Service;
import ru.bmstu.rentalapi.dto.RentalRequestDto;
import ru.bmstu.rentalapi.dto.RentalResponseDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface RentalService {
    RentalResponseDto createRental(String username, RentalRequestDto request);

    List<RentalResponseDto> getRentals(String username);

    Optional<RentalResponseDto> getRental(UUID rentalUid, String username);

    void cancelRental(UUID rentalUid, String username);

    void finishRental(UUID rentalUid, String username);
}