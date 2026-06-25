package com.logopeda.billing.repository;

import com.logopeda.billing.enums.PaymentMethod;
import com.logopeda.billing.enums.PaymentStatus;
import com.logopeda.billing.model.Payment;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    @Query("""
            SELECT p FROM Payment p
            WHERE p.clinicId = :clinicId
              AND (:patientId IS NULL OR p.patientId = :patientId)
              AND (:fromDate IS NULL OR p.paymentDate >= :fromDate)
              AND (:toDate IS NULL OR p.paymentDate <= :toDate)
              AND (:method IS NULL OR p.method = :method)
              AND (:status IS NULL OR p.status = :status)
            ORDER BY p.paymentDate DESC, p.createdAt DESC
            """)
    List<Payment> search(@Param("clinicId") String clinicId,
                         @Param("patientId") String patientId,
                         @Param("fromDate") LocalDate fromDate,
                         @Param("toDate") LocalDate toDate,
                         @Param("method") PaymentMethod method,
                         @Param("status") PaymentStatus status);

    List<Payment> findByClinicIdAndStatus(String clinicId, PaymentStatus status);
}
