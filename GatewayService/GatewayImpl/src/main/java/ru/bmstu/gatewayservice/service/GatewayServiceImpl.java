package ru.bmstu.gatewayservice.service;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.bmstu.carservice.dto.CarResponseDto;
import ru.bmstu.carservice.feign.CarClient;
import ru.bmstu.gatewayservice.dto.CarRentDto;
import ru.bmstu.gatewayservice.dto.car.BaseCarDto;
import ru.bmstu.gatewayservice.dto.car.CarDto;
import ru.bmstu.gatewayservice.dto.payment.PaymentDto;
import ru.bmstu.gatewayservice.dto.rental.RentalCreateDto;
import ru.bmstu.gatewayservice.dto.rental.RentalDto;
import ru.bmstu.gatewayservice.dto.wrapper.PageableWrapperDto;
import ru.bmstu.gatewayservice.exception.InvalidOperationException;
import ru.bmstu.paymentserivce.dto.PaymentResponseDto;
import ru.bmstu.paymentserivce.feign.PaymentClient;
import ru.bmstu.rentalapi.dto.RentalRequestDto;
import ru.bmstu.rentalapi.dto.RentalResponseDto;
import ru.bmstu.rentalapi.feign.RentalClient;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayServiceImpl implements GatewayService {
    private final CarClient carClient;
    private final RentalClient rentalClient;
    private final PaymentClient paymentClient;
    private final ModelMapper modelMapper;

    @Override
    public PageableWrapperDto<CarDto> getAllCars(boolean showAll, int page, int size) {
        Page<CarResponseDto> carOutDtos = carClient.getCars(showAll, page, size);

        log.info("Received {} entities from car service", carOutDtos.getTotalElements());

        return mapToPageCollectionOutDto(carOutDtos, CarDto.class);
    }

    @Override
    public List<RentalDto> getRental(String username) {
        List<RentalResponseDto> rentals = rentalClient.getRentals(username).getBody();

        List<UUID> paymentsUids = new LinkedList<>();
        List<UUID> carUids = new LinkedList<>();
        rentals.forEach(rentalOutDto -> {
            paymentsUids.add(rentalOutDto.getPaymentUid());
            carUids.add(rentalOutDto.getCarUid());
        });

        Map<UUID, PaymentResponseDto> payments = paymentClient.getPayments(paymentsUids)
                .stream()
                .collect(Collectors.toMap(PaymentResponseDto::paymentUid, Function.identity()));

        Map<UUID, CarResponseDto> cars = carClient.getCars(carUids)
                .stream()
                .collect(Collectors.toMap(CarResponseDto::carUid, Function.identity()));

        return rentals.stream()
                .map(rentalOutDto -> buildOutDto(rentalOutDto, payments, cars))
                .toList();
    }

    @Override
    public RentalDto getRental(String username, UUID rentalUid) {
        RentalResponseDto rental = rentalClient.getRental(rentalUid, username).getBody();
        log.info("Get rental form rental service: {}", rental);

        CarResponseDto car = carClient.getCar(rental.getCarUid());
        PaymentResponseDto payment = paymentClient.getPayment(rental.getPaymentUid());

        RentalDto mappedRental = modelMapper.map(rental, RentalDto.class);
        mappedRental.setCar(modelMapper.map(car, BaseCarDto.class));
        mappedRental.setPayment(modelMapper.map(payment, PaymentDto.class));

        return mappedRental;
    }

    @Override
    public RentalCreateDto bookCar(String userName, CarRentDto carRentDto) {
        CarResponseDto car = carClient.getCar(carRentDto.getCarUid());

        if (!car.available()) {
            log.error("Trying to book not available car {}", car.carUid());
            throw new InvalidOperationException("Car %s is not available".formatted(car.carUid()));
        }

        changeCarAvailability(carRentDto.getCarUid());
        PaymentResponseDto payment = createPayment(carRentDto, car.price());
        RentalResponseDto rental = createRental(userName, carRentDto, payment);

        RentalCreateDto rentalCreationOutDto = modelMapper.map(rental, RentalCreateDto.class);
        rentalCreationOutDto.setPayment(modelMapper.map(payment, PaymentDto.class));
        rentalCreationOutDto.setCarUid(carRentDto.getCarUid());

        return rentalCreationOutDto;
    }

    private RentalResponseDto createRental(String username, CarRentDto carRentDto, PaymentResponseDto payment) {
        RentalRequestDto rentalInDto = new RentalRequestDto(
                carRentDto.getCarUid(), payment.paymentUid(), carRentDto.getDateFrom(), carRentDto.getDateTo());

        return rentalClient.createRental(username, rentalInDto);
    }

    @Override
    public void finishRental(String username, UUID rentalUid) {
        try {
            rentalClient.finishRental(rentalUid, username);
        } catch (FeignException.NotFound e) {
            log.info("Trying to finish non-existing rental: username = {}, rentalUid = {}", username, rentalUid);

            throw new EntityNotFoundException(e.getMessage());
        }
    }

    private PaymentResponseDto createPayment(CarRentDto carRentDto, int carRentalPrice) {
        int amountRentalDays = (int) calculateAmountRentalDays(carRentDto);
        int totalPrice = amountRentalDays * carRentalPrice;

        return paymentClient.createPayment(totalPrice);
    }

    private long calculateAmountRentalDays(CarRentDto carRentDto) {
        long totalRentalDays =

        DAYS.between(carRentDto.getDateFrom(), carRentDto.getDateTo());

        if (totalRentalDays < 0) {
            log.error("Trying to create rental with invalid dates DateFrom {}, DateTo {}",
                    carRentDto.getDateFrom(),
                    carRentDto.getDateTo());
            throw new InvalidOperationException("Invalid car rental dates. DateTo should be after DateFrom");
        }

        return totalRentalDays;
    }

    @Override
    public void cancelRental(String username, UUID rentalUid) {
        try {
            RentalResponseDto rental = rentalClient.getRental(rentalUid, username).getBody();

            rentalClient.cancelRental(rentalUid, username);
            paymentClient.cancelPayment(rental.getPaymentUid());
            carClient.changeAvailability(rental.getCarUid());
        } catch (FeignException.NotFound e) {
            log.info("Trying to cancel non-existing rental: username = {}, rentalUid = {}", username, rentalUid);

            throw new EntityNotFoundException(e.getMessage());
        }
    }

    private void changeCarAvailability(UUID carId) {
        try {
            carClient.changeAvailability(carId);
        } catch (FeignException.NotFound e) {
            log.info("Trying to change availability for non existing car {}", carId);
            throw new EntityNotFoundException("There is no car with id = %s".formatted(carId));
        }
    }

    private RentalDto buildOutDto(RentalResponseDto rentalOutDto, Map<UUID, PaymentResponseDto> payments, Map<UUID, CarResponseDto> cars) {
        RentalDto rental = modelMapper.map(rentalOutDto, RentalDto.class);

        PaymentResponseDto payment = payments.get(rentalOutDto.getPaymentUid());
        PaymentDto paymentDto = modelMapper.map(payment, PaymentDto.class);

        CarResponseDto car = cars.get(rentalOutDto.getCarUid());
        CarDto carDto = modelMapper.map(car, CarDto.class);

        rental.setPayment(paymentDto);
        rental.setCar(carDto);

        return rental;
    }


    private <T> PageableWrapperDto<T> mapToPageCollectionOutDto(Page<CarResponseDto> page, Class<T> destinationClass) {
        Page<T> mappedPage = page.map(car -> modelMapper.map(car, destinationClass));

        return buildPageCollectionOutDto(mappedPage);
    }

    private <T> PageableWrapperDto<T> buildPageCollectionOutDto(Page<T> page) {
        return new PageableWrapperDto<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalPages());
    }

}
