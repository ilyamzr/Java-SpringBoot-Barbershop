package com.example.barbershop.repository;

import com.example.barbershop.model.Barber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BarberRepository extends JpaRepository<Barber, Long> {

    @Query("SELECT b FROM Barber b WHERE b.location.name = :locationName")
    List<Barber> findBarbersByLocationName(@Param("locationName") String locationName);
}