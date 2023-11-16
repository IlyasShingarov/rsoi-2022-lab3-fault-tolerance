package ru.bmstu.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;
import ru.bmstu.paymentserivce.constants.PaymentStatus;

import java.util.UUID;

@Entity(name = "payment")
@Getter @Setter @ToString @EqualsAndHashCode
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private UUID paymentUid;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private int price;
}
