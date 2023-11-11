package raf.sk.projekat1.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Appointment {
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private String day;
    private Map<String,String> additional;
    private Places place;

    public Appointment() {
        additional = new HashMap<>();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
