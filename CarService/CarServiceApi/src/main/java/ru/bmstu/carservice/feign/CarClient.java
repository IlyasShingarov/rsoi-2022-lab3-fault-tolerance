package ru.bmstu.carservice.feign;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.carservice.dto.CarResponseDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "cars", url = "${feign.cars.url}")
public interface CarClient {
    @GetMapping("${carsapi.url.base}")
    Page<CarResponseDto> getCars(@RequestParam @DefaultValue("false") boolean showAll,
                                 @RequestParam @DefaultValue("0") int page,
                                 @RequestParam @DefaultValue("100") int size);

    @GetMapping("${carsapi.url.base}/{carId}")
    CarResponseDto getCar(@PathVariable UUID carId);

    @PostMapping("${carsapi.url.base}")
    List<CarResponseDto> getCars(List<UUID> carUids);

    @PatchMapping("${carsapi.url.base}/{carId}")
    ResponseEntity<?> changeAvailability(@PathVariable UUID carId);

}
