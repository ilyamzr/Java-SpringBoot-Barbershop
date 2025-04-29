package com.example.barbershop.repository;

import com.example.barbershop.model.Offering;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfferingRepository extends JpaRepository<Offering, Long> {

    @Query("SELECT o FROM Offering o JOIN o.barbers b WHERE b.barberId = :barberId")
    List<Offering> findByBarberId(@Param("barberId") Long barberId);
}