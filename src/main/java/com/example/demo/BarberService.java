package com.example.demo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BarberService {

    private final BarberRepository barberRepository;

    @Autowired
    public BarberService(BarberRepository barberRepository) {
        this.barberRepository = barberRepository;
    }

    public ResponseDto getBarberAvailability(String barberName) {
        List<Barber> availableBarbers = barberRepository.getAvailableBarbers();
        String str = "Barber ";

        for (Barber barber : availableBarbers) {
            if (barber.getName().equalsIgnoreCase(barberName)) {
                return new ResponseDto(str + barberName + " is available on: " + String.join(", ", barber.getWorkingDays()));
            }
        }
        return new ResponseDto(str + barberName + " is not found.");
    }
}
