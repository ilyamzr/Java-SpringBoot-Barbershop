package com.example.demo;

import java.util.List;

public class Barber {
    private String name;
    private List<String> workingDays;

    public Barber(String name, List<String> workingDays) {
        this.name = name;
        this.workingDays = workingDays;
    }

    public String getName() {
        return name;
    }

    public List<String> getWorkingDays() {
        return workingDays;
    }
}