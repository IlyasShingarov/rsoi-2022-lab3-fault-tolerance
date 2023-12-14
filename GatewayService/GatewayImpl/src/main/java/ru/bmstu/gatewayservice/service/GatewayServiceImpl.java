package ru.bmstu.gatewayservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.bmstu.carservice.dto.CarResponseDto;
import ru.bmstu.gatewayservice.availability.AvailabilityService;
import ru.bmstu.gatewayservice.dto.CarRentDto;
import ru.bmstu.gatewayservice.dto.car.BaseCarDto;
import ru.bmstu.gatewayservice.dto.car.CarDto;
import ru.bmstu.gatewayservice.dto.payment.PaymentDto;
import ru.bmstu.gatewayservice.dto.rental.RentalCreateDto;
import ru.bmstu.gatewayservice.dto.rental.RentalDto;
import ru.bmstu.gatewayservice.dto.wrapper.PageableWrapperDto;
import ru.bmstu.gatewayservice.exception.InternalServiceException;
import ru.bmstu.gatewayservice.exception.InvalidOperationException;
import ru.bmstu.gatewayservice.exception.ServiceUnavailableException;
import ru.bmstu.gatewayservice.wrapper.FallbackWrapper;
import ru.bmstu.paymentserivce.dto.PaymentResponseDto;
import ru.bmstu.rentalapi.dto.RentalRequestDto;
import ru.bmstu.rentalapi.dto.RentalResponseDto;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayServiceImpl implements GatewayService {

    private final ExternalService externalService;
    private final RetryService breaker;
    private final ModelMapper modelMapper;
    private final AvailabilityService availabilityService;

    @PostConstruct
    void startCircuitBreaker() {
        Thread thread = new Thread(() -> {
            try {
                breaker.resendRequests();
            } catch (InterruptedException ignored) {
            }
        });

        thread.start();
        log.debug("Circuit breaker initialized");
    }

    @Override
    public PageableWrapperDto<CarDto> getAllCars(boolean showAll, int page, int size) {
        return buildPageCollectionOutDto(externalService.getCars(showAll, page, size));
    }

    @Override
    public List<RentalDto> getRental(String username) {
        List<RentalResponseDto> rentals = externalService.getRentals(username);

        List<UUID> paymentsUids = new LinkedList<>();
        List<UUID> carUids = new LinkedList<>();
        rentals.forEach(rentalOutDto -> {
            paymentsUids.add(rentalOutDto.getPaymentUid());
            carUids.add(rentalOutDto.getCarUid());
        });

        Map<UUID, PaymentResponseDto> payments = externalService.getPayments(paymentsUids);
        Map<UUID, CarResponseDto> cars = externalService.getCars(carUids);

        return rentals.stream()
                .map(rentalOutDto -> buildOutDto(rentalOutDto, payments, cars))
                .toList();
    }

    @Override
    public RentalDto getRental(String username, UUID rentalUid) {
        FallbackWrapper<RentalResponseDto> rental = externalService.getRental(username, rentalUid);
        if (!rental.isValidResponse()) {
            throw new InternalServiceException("Unable to get info from Rental service.");
        }

        CarResponseDto car = externalService.getCar(rental.getValue().getCarUid());
        PaymentResponseDto payment = externalService.getPayment(rental.getValue().getPaymentUid());

        RentalDto mappedRental = modelMapper.map(rental.getValue(), RentalDto.class);
        mappedRental.setCar(modelMapper.map(car, BaseCarDto.class));
        mappedRental.setPayment(modelMapper.map(payment, PaymentDto.class));

        log.debug("RentalDto: {}", mappedRental);

        return mappedRental;
    }

    @Override
    public RentalCreateDto bookCar(String userName, CarRentDto carRentDto) {
        CarResponseDto car = externalService.getCar(carRentDto.getCarUid());

        if (!car.available()) {
            log.error("Trying to book not available car {}", car.carUid());
            throw new InvalidOperationException("Car %s is not available".formatted(car.carUid()));
        }

        changeCarAvailabilitySafety(car.carUid(), false);

        FallbackWrapper<PaymentResponseDto> payment = createPayment(carRentDto, car.price());
        if (!payment.isValidResponse()) {
            log.error("Unable to create payment for user [{}] order {}. Rollback changes", userName, carRentDto);
            changeCarAvailabilitySafety(car.carUid(), true);
            throw new ServiceUnavailableException("Payment Service unavailable");
        }

        FallbackWrapper<RentalResponseDto> rental = createRental(userName, carRentDto, payment.getValue());
        if (!rental.isValidResponse()) {
            log.error("Unable to create rental for user [{}] order [{}]. Rollback changes", userName, carRentDto);
            changeCarAvailabilitySafety(car.carUid(), true);
            externalService.cancelPayment(payment.getValue().paymentUid());
            throw new ServiceUnavailableException("Rental Service unavailable");
        }

        RentalCreateDto rentalCreationOutDto = modelMapper.map(rental.getValue(), RentalCreateDto.class);
        rentalCreationOutDto.setPayment(modelMapper.map(payment.getValue(), PaymentDto.class));
        rentalCreationOutDto.setCarUid(carRentDto.getCarUid());

        log.debug("RentalDto after booking car {}", rentalCreationOutDto);

        return rentalCreationOutDto;
    }

    private FallbackWrapper<PaymentResponseDto> createPayment(CarRentDto carBookDto, int carRentalPrice) {
        int amountRentalDays = (int) calculateAmountRentalDays(carBookDto);
        int totalPrice = amountRentalDays * carRentalPrice;

        return externalService.createPayment(totalPrice);
    }

    private FallbackWrapper<RentalResponseDto> createRental(String username, CarRentDto carRentDto, PaymentResponseDto payment) {
        RentalRequestDto rentalInDto = new RentalRequestDto(
                carRentDto.getCarUid(), payment.paymentUid(), carRentDto.getDateFrom(), carRentDto.getDateTo());

        return externalService.createRental(username, rentalInDto);
    }

    @Override
    public boolean finishRental(String username, UUID rentalUid) {
        boolean isAvailable = availabilityService.checkAvailability("rental");
        FallbackWrapper<RentalResponseDto> rental = isAvailable ? externalService.getRental(username, rentalUid)
                : new FallbackWrapper<>(null, false);

        if (rental.isValidResponse()) {
            UUID carUid = rental.getValue().getCarUid();
            changeCarAvailabilitySafety(carUid, true);

            if (!externalService.finishRental(rentalUid, username)) {
                log.error("Unable to finish rental [{}] of user [{}]. Request will be resend", rentalUid, username);
                breaker.addRequest("rental", buildHash(username, rentalUid),
                        () -> externalService.finishRental(rentalUid, username));
            }
        } else {
            log.error("Unable to get rental [{}] of user [{}] from rental service. Request will be resend",
                    rentalUid, username);
            breaker.addRequest("rental", buildHash(username, rentalUid),
                    () -> this.finishRental(username, rentalUid));
            return false;
        }

        return true;
    }

    private void changeCarAvailabilitySafety(UUID carUid, boolean availability) {
        boolean isChanged = false;
        if (availabilityService.checkAvailability("cars")) {
            isChanged = externalService.changeCarAvailability(carUid, availability);
        }

        if (!isChanged) {
            log.error("Unable to change car [{}] availability to [{}]. Request will be resend",
                    carUid, availability);
            availabilityService.updateErrorCount("cars");
            breaker.addRequest("cars", buildHash(carUid, availability),
                    () -> externalService.changeCarAvailability(carUid, availability));
        } else {
            availabilityService.setClosed("cars");
        }
    }

    private Integer buildHash(String username, UUID rentalUid) {
        return (username + rentalUid).hashCode();
    }

    private Integer buildHash(UUID carUid, boolean availability) {
        return (carUid.toString() + availability).hashCode();
    }

    @Override
    public boolean cancelRental(String username, UUID rentalUid) {
        boolean isRentalAvailable = availabilityService.checkAvailability("rental");

        FallbackWrapper<RentalResponseDto> rental = isRentalAvailable ? externalService.getRental(username, rentalUid)
                : new FallbackWrapper<>(null, false);

        if (rental.isValidResponse()) {
            changeCarAvailabilitySafety(rental.getValue().getCarUid(), true);

            boolean isRentalCancelled = isRentalAvailable && externalService.cancelRental(username, rentalUid);
            if (!isRentalCancelled) {
                breaker.addRequest("rental", buildHash(username, rentalUid),
                        () -> externalService.cancelRental(username, rentalUid));
            }

            boolean isPaymentAvaliable = availabilityService.checkAvailability("payment");
            boolean isPaymentCancelled = isPaymentAvaliable &&
                    externalService.cancelPayment(rental.getValue().getPaymentUid());
            if (!isPaymentCancelled) {
                breaker.addRequest("payment", buildHash(username, rentalUid),
                        () -> externalService.cancelPayment(rental.getValue().getPaymentUid()));
            }
        } else {
            breaker.addRequest("rental", buildHash(username, rentalUid),
                    () -> this.cancelRental(username, rentalUid));
            return false;
        }

        return true;
    }

    private long calculateAmountRentalDays(CarRentDto carRentDto) {
        long totalRentalDays = DAYS.between(carRentDto.getDateFrom(), carRentDto.getDateTo());

        if (totalRentalDays < 0) {
            log.error("Trying to create rental with invalid dates DateFrom {}, DateTo {}",
                    carRentDto.getDateFrom(),
                    carRentDto.getDateTo());
            throw new InvalidOperationException("Invalid car rental dates. DateTo should be after DateFrom");
        }

        return totalRentalDays;
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

    private <T> PageableWrapperDto<T> buildPageCollectionOutDto(Page<T> page) {
        return new PageableWrapperDto<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalPages());
    }

}
