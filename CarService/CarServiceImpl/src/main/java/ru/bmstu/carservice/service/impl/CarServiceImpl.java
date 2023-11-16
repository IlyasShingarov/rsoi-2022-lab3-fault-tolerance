package ru.bmstu.carservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.bmstu.carservice.dto.CarResponseDto;
import ru.bmstu.carservice.entity.Car;
import ru.bmstu.carservice.mapper.CarMapper;
import ru.bmstu.carservice.repository.CarRepository;
import ru.bmstu.carservice.service.CarService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public Page<CarResponseDto> getCars(boolean showAll, Pageable pageable) {
        Page<Car> cars = showAll
                ? carRepository.findAll(pageable)
                : carRepository.findAllByAvailabilityIsTrue(pageable);
        log.trace("Got from DB: {}", cars);
        return cars.map(carMapper::toResponse);
    }

    @Override
    public void changeAvailability(UUID carId) {
        log.debug("Changing availability for car {}", carId);
        Car car = carRepository.findByCarUid(carId).orElseThrow(() ->
                new EntityNotFoundException("There's no car with id: %s".formatted(carId)));
        car.setAvailability(!car.isAvailability());
        carRepository.save(car);
        log.debug("Changed availability for car {} to {}", carId, car.isAvailability());
    }

    @Override
    public List<CarResponseDto> getCars(Set<UUID> carUids) {
        return carRepository.findAllByCarUidIn(carUids)
                .stream()
                .map(carMapper::toResponse)
                .toList();
    }

    @Override
    public CarResponseDto getCar(UUID carId) {
        return carMapper.toResponse(carRepository.findByCarUid(carId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "There's no car with id: '%s'".formatted(carId)))
        );
    }
}
