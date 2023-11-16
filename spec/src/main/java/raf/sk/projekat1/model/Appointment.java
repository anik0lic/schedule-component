package raf.sk.projekat1.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    public Appointment(LocalTime startTime, LocalTime endTime, LocalDate startDate, LocalDate endDate, String day, Map<String, String> additional) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.day = day;
        this.additional = additional;
    }

    public Appointment(LocalTime startTime, LocalTime endTime, LocalDate startDate, String day, Map<String, String> additional) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.day = day;
        this.additional = additional;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Appointment that = (Appointment) obj;
        return Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && Objects.equals(startDate, that.startDate)
                && Objects.equals(endDate, that.endDate) && Objects.equals(place, that.place);
    }

    //napravi toString metodu za obe implementacije
    //ovaj appointment je abstract?
}
