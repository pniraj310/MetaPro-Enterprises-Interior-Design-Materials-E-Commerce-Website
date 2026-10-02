package com.metapro.payment.repository;

import com.metapro.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByOrderId(String orderId);

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    List<Payment> findByStatusOrderByCreatedAtDesc(String status);

    List<Payment> findAllByOrderByCreatedAtDesc();

    @Query("SELECT p FROM Payment p WHERE " +
           "(:status IS NULL OR LOWER(p.status) = LOWER(:status)) AND " +
           "(:search IS NULL OR " +
           "LOWER(p.id) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.orderId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.razorpayPaymentId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "p.customerPhone LIKE CONCAT('%', :search, '%')) " +
           "ORDER BY p.createdAt DESC")
    List<Payment> searchPayments(@Param("status") String status, @Param("search") String search);
}
