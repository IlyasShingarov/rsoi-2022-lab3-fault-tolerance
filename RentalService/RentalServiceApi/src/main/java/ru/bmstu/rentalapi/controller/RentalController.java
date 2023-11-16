package ru.bmstu.rentalapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.rentalapi.constants.CustomHeaders;
import ru.bmstu.rentalapi.dto.RentalRequestDto;
import ru.bmstu.rentalapi.dto.RentalResponseDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rentalapi.url.base}")
public interface RentalController {
    @PostMapping
    RentalResponseDto createRental(@RequestHeader(CustomHeaders.USERNAME_HEADER) String username,
                                                   @RequestBody RentalRequestDto rentalInDto);

    @GetMapping
    ResponseEntity<List<RentalResponseDto>> getRentals(@RequestHeader(CustomHeaders.USERNAME_HEADER) String username);

    @GetMapping(path = "/{rentalUid}")
    ResponseEntity<RentalResponseDto> getRental(@PathVariable UUID rentalUid,
                                                @RequestHeader(CustomHeaders.USERNAME_HEADER) String username);

    @DeleteMapping(path = "/{rentalUid}")
    ResponseEntity<?> cancelRental(@PathVariable UUID rentalUid,
                                   @RequestHeader(CustomHeaders.USERNAME_HEADER) String username);

    @PostMapping(path = "/{rentalUid}/finish")
    ResponseEntity<?> finishRental(@PathVariable UUID rentalUid,
                                   @RequestHeader(CustomHeaders.USERNAME_HEADER) String username);
}
