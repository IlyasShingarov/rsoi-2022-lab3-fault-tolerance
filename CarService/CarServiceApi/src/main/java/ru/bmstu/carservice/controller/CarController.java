package ru.bmstu.carservice.controller;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.carservice.dto.CarResponseDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("${carsapi.url.base}")
public interface CarController {
    @GetMapping
    Page<CarResponseDto> getCars(@RequestParam(defaultValue = "false") boolean showAll,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "100") int size);

    @GetMapping("/{carId}")
    CarResponseDto getCar(@PathVariable UUID carId);

    @PostMapping
    List<CarResponseDto> getCars(@RequestBody Set<UUID> carUids);

    @PatchMapping("/{carId}")
    ResponseEntity<?> changeAvailability(@PathVariable UUID carId);
}
