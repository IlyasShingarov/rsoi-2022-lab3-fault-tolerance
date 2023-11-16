package ru.bmstu.rentalservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bmstu.rentalapi.constants.RentalStatus;
import ru.bmstu.rentalapi.dto.RentalRequestDto;
import ru.bmstu.rentalapi.dto.RentalResponseDto;
import ru.bmstu.rentalservice.mapper.RentalMapper;
import ru.bmstu.rentalservice.model.Rental;
import ru.bmstu.rentalservice.repository.RentalRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService{

    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;

    @Override
    public RentalResponseDto createRental(String username, RentalRequestDto request) {
        Rental rental = makeRentalFromRequest(username, request);
        Rental savedRental = rentalRepository.save(rental);
        log.info("Rental was created {}", savedRental);
        return rentalMapper.toResponse(savedRental);
    }

    private Rental makeRentalFromRequest(String username, RentalRequestDto request) {
        return Rental.builder()
                .carUid(request.carUid())
                .paymentUid(request.paymentUid())
                .dateFrom(request.dateFrom())
                .dateTo(request.dateTo())
                .rentalUid(UUID.randomUUID())
                .username(username)
                .status(RentalStatus.IN_PROGRESS)
                .build();
    }

    @Override
    public List<RentalResponseDto> getRentals(String username) {
        var rentals = rentalRepository.findAllByUsername(username);
        log.info("Get {} rentals for username {}", rentals.size(), username);
        return rentals.stream()
                .map(rentalMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<RentalResponseDto> getRental(UUID rentalUid, String username) {
        var rental = rentalRepository.findByUsernameAndRentalUid(username, rentalUid);
        rental.ifPresent(value -> log.info("Get rental: {}", value));

        return rental.map(optionalRental -> rentalMapper.toResponse(rental.get()));
    }

    @Override
    public void cancelRental(UUID rentalUid, String username) {
        changeRentalStatus(username, rentalUid, RentalStatus.CANCELED);
    }

    @Override
    public void finishRental(UUID rentalUid, String username) {
        changeRentalStatus(username, rentalUid, RentalStatus.FINISHED);
    }

    private void changeRentalStatus(String username, UUID rentalUid, RentalStatus finished) {
        Optional<Rental> rental = rentalRepository.findByUsernameAndRentalUid(username, rentalUid);

        if (rental.isEmpty()) {
            log.error("Requesting rental {} for user {} doesn't exist", rentalUid, username);
            throw new EntityNotFoundException("There is no rental %s for user %s".formatted(rentalUid, username));
        }

        Rental unpackedRental = rental.get();
        unpackedRental.setStatus(finished);

        Rental updatedRental = rentalRepository.save(unpackedRental);
        log.info("Rental {} status was changed. New status: {}", rentalUid, updatedRental.getStatus());
    }

}
