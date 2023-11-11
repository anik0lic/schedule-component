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
    protected LocalTime startTime;
    protected LocalTime endTime;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected String day;
    protected Map<String,String> additional;
    protected Places place;

    public Appointment() {
        additional = new HashMap<>();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
