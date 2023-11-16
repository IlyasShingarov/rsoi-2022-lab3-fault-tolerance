package ru.bmstu.carservice.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.carservice.entity.Car;

import java.util.*;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    Page<Car> findAllByAvailabilityIsTrue(Pageable pageable);

    Optional<Car> findByCarUid(@NotNull UUID carUid);
    List<Car> findAllByCarUidIn(Set<@NotNull UUID> carUid);
}
