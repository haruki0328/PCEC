package com.pcparts.ec.repository;

import com.pcparts.ec.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_IdOrderByCreatedAtDesc(Long userId);
    Optional<Order> findByIdAndUser_Id(Long id, Long userId);
    Optional<Order> findByStripeSessionId(String stripeSessionId);
}
