package raf.sk.projekat1.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
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

    public Appointment(LocalTime startTime, LocalTime endTime, LocalDate startDate, LocalDate endDate, String day, Map<String, String> additional, Places place) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.day = day;
        this.additional = additional;
        this.place = place;
    }

    public Appointment(LocalTime startTime, LocalTime endTime, LocalDate startDate, String day, Map<String, String> additional) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.day = day;
        this.additional = additional;
    }

    public boolean equals(LocalTime sTime, LocalTime eTime, LocalDate date, String place) {
        return this.startTime.equals(sTime) && this.endTime.equals(eTime) && this.startDate.equals(date) && this.getPlace().getName().equals(place);
    }

    public boolean equals(LocalTime sTime, LocalTime eTime, LocalDate sDate, LocalDate eDate, String place) {
        if(this.startTime.equals(sTime) && this.endTime.equals(eTime) && this.startDate.equals(sDate) && this.endDate.equals(eDate) && this.getPlace().getName().equals(place)){
            return true;
        }
        else if(this.startTime.equals(sTime) && this.endTime.equals(eTime) && this.getPlace().getName().equals(place)){
            if(this.startDate.equals(sDate) && this.endDate.isAfter(eDate))
                return true;
            else if(this.startDate.isBefore(sDate) && this.endDate.isEqual(eDate))
                return true;
            else if(this.startDate.isBefore(sDate) && this.endDate.isAfter(eDate)){
                Duration diff = Duration.between(this.getStartDate().atStartOfDay(), sDate.atStartOfDay());
                long diffDays = diff.toDays();

                return diffDays % 7 == 0;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Appointment that = (Appointment) obj;
        return Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && Objects.equals(startDate, that.startDate)
                && Objects.equals(endDate, that.endDate) && Objects.equals(place, that.place);
    }
}
