package ru.bmstu.rentalservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bmstu.rentalservice.model.Rental;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
    List<Rental> findAllByUsername(String username);

    Optional<Rental> findByUsernameAndRentalUid(String username, UUID rentalUid);
}
