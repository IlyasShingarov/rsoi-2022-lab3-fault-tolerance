package ru.bmstu.carservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.bmstu.carservice.constants.CarType;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "car_uid", nullable = false)
    private UUID carUid;

    @Size(max = 80)
    @NotNull
    @Column(name = "brand", nullable = false, length = 80)
    private String brand;

    @Size(max = 80)
    @NotNull
    @Column(name = "model", nullable = false, length = 80)
    private String model;

    @Size(max = 20)
    @NotNull
    @Column(name = "registration_number", nullable = false, length = 20)
    private String registrationNumber;

    @Column(name = "power")
    private Integer power;

    @NotNull
    @Column(name = "price", nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    private CarType type;

    @Column(name = "availability", nullable = false)
    private boolean availability = false;
}