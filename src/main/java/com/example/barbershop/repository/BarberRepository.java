package com.example.barbershop.repository;

import com.example.barbershop.model.Barber;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BarberRepository extends JpaRepository<Barber, Long> {

    @Query("SELECT b FROM Barber b WHERE b.location.name = :locationName")
    List<Barber> findBarbersByLocationName(@Param("locationName") String locationName);

    @Query("SELECT b FROM Barber b JOIN b.offerings o WHERE o.offeringId = :offeringId")
    List<Barber> findBarbersByOfferingId(@Param("offeringId") Long offeringId);
}