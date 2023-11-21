package raf.sk.projekat1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(value = { "info" })
public class Schedule {
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected LocalTime startTime;
    protected LocalTime endTime;
    protected List<LocalDate> nonWorkingDates;
    protected List<String> nonWorkingDaysOfTheWeek;
    protected Info info;
    protected List<Appointment> appointments;
    protected List<Places> places;

    public Schedule(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, List<LocalDate> nonWorkingDates, List<String> nonWorkingDaysOfTheWeek, Info info) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.nonWorkingDates = nonWorkingDates;
        this.nonWorkingDaysOfTheWeek = nonWorkingDaysOfTheWeek;
        this.info = info;
        this.places = new ArrayList<>();
        this.appointments = new ArrayList<>();
    }

    public Schedule(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, Info info) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.nonWorkingDates = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.places = new ArrayList<>();
        this.info = info;
        this.nonWorkingDaysOfTheWeek = new ArrayList<>();
    }

    public Schedule(Info info) {
        this.appointments = new ArrayList<>();
        this.nonWorkingDates = new ArrayList<>();
        this.places = new ArrayList<>();
        this.info = info;
        this.nonWorkingDaysOfTheWeek = new ArrayList<>();
    }

    public Schedule() {
        this.appointments = new ArrayList<>();
        this.nonWorkingDates = new ArrayList<>();
        this.places = new ArrayList<>();
        this.nonWorkingDaysOfTheWeek = new ArrayList<>();
    }

}
