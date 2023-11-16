package ru.bmstu.carservice.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.bmstu.carservice.controller.CarController;
import ru.bmstu.carservice.dto.CarResponseDto;
import ru.bmstu.carservice.service.CarService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CarControllerImpl implements CarController {

    private final CarService carService;

    @Override
    public Page<CarResponseDto> getCars(boolean showAll, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        log.debug("Request for reading cars. showAll = {}. page request = {}", showAll, pageRequest);
        return carService.getCars(showAll, pageRequest);
    }

    @Override
    public CarResponseDto getCar(UUID carId) {
        return carService.getCar(carId);
    }

    @Override
    public List<CarResponseDto> getCars(Set<UUID> carUids) {
        return carService.getCars(carUids);
    }

    @Override
    public ResponseEntity<?> changeAvailability(UUID carId) {
        carService.changeAvailability(carId);
        return ResponseEntity.ok().build();
    }
}
