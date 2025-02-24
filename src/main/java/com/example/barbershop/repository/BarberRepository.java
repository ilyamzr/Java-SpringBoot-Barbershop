package com.example.barbershop.repository;

import com.example.barbershop.model.Barber;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class BarberRepository {

    public List<Barber> getAvailableBarbers() {
        return Arrays.asList(
                new Barber("John", Arrays.asList("Monday", "Wednesday", "Friday")),
                new Barber("Mike", Arrays.asList("Tuesday", "Thursday", "Saturday")),
                new Barber("Steve", Arrays.asList("Monday", "Thursday")),
                new Barber("Anna", Arrays.asList("Wednesday", "Saturday"))
        );
    }
}
