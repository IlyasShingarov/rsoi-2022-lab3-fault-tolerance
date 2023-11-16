package ru.bmstu.carservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bmstu.carservice.dto.CarResponseDto;
import ru.bmstu.carservice.entity.Car;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(target = "available", source = "availability")
    CarResponseDto toResponse(Car entity);

}
