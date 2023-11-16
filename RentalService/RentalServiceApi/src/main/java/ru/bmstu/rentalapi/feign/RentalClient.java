package ru.bmstu.rentalapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.rentalapi.constants.CustomHeaders;
import ru.bmstu.rentalapi.dto.RentalRequestDto;
import ru.bmstu.rentalapi.dto.RentalResponseDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "rental", url = "${feign.rental.url}")
public interface RentalClient {
    @PostMapping("${rental.client.url.base}")
    RentalResponseDto createRental(@RequestHeader(CustomHeaders.USERNAME_HEADER) String username,
                                                  @RequestBody RentalRequestDto rentalRequest);

    @GetMapping("${rental.client.url.base}")
    ResponseEntity<List<RentalResponseDto>> getRentals(@RequestHeader(CustomHeaders.USERNAME_HEADER) String username);

    @GetMapping("${rental.client.url.base}/{rentalUid}")
    ResponseEntity<RentalResponseDto> getRental(@PathVariable UUID rentalUid,
                                @RequestHeader(CustomHeaders.USERNAME_HEADER) String username);

    @DeleteMapping("${rental.client.url.base}/{rentalUid}")
    ResponseEntity<?> cancelRental(@PathVariable UUID rentalUid,
                      @RequestHeader(CustomHeaders.USERNAME_HEADER) String username);

    @PostMapping("${rental.client.url.base}/{rentalUid}/finish")
    ResponseEntity<?> finishRental(@PathVariable UUID rentalUid,
                      @RequestHeader(CustomHeaders.USERNAME_HEADER) String username);

}
