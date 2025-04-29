package com.example.barbershop.repository;

import com.example.barbershop.model.Order;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.barber.barberId = :barberId AND "
            + "o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findByBarberIdAndDateRange(
            @Param("barberId") Long barberId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}