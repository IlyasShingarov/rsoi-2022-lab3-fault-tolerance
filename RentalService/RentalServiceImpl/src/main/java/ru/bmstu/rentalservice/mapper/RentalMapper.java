package ru.bmstu.rentalservice.mapper;

import org.mapstruct.Mapper;
import ru.bmstu.rentalapi.dto.RentalRequestDto;
import ru.bmstu.rentalapi.dto.RentalResponseDto;
import ru.bmstu.rentalservice.model.Rental;

import javax.swing.text.html.parser.Entity;

@Mapper(componentModel = "spring")
public interface RentalMapper {

    RentalResponseDto toResponse(Rental entity);

}
