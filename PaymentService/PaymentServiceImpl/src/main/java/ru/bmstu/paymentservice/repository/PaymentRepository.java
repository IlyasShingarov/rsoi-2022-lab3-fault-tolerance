package ru.bmstu.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.paymentservice.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByPaymentUid(UUID paymentUid);
    List<Payment> findByPaymentUidIn(List<UUID> paymentUids);
}
