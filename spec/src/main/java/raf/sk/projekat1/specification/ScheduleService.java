package raf.sk.projekat1.specification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import raf.sk.projekat1.model.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public abstract class ScheduleService {
    protected Schedule schedule;
    public ScheduleService(Schedule schedule) {
        this.schedule = schedule;
    }

    public abstract void exportCSV(String filepath) throws IOException;
    public abstract void exportJSON(String filepath);
    //I - 13/10/2023 11:00-12:00
    //II - 13/10/2023 11:00-12:00
    public abstract boolean addAppointment(String when, String place, String time, Map<String, String> additional);
    //I - 13/10/2023 11:00-12:00 1 16/10/2023
    //II - 13/10/2023 11:00-12:00 everyWeek 30/10/2023
    public abstract boolean addAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat, Map<String, String> additional);

    public abstract boolean removeAppointment(String when, String place, String time);
    public abstract boolean removeAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat);

    public abstract Appointment find(String when, String place, String time);

    //da ima boolean da li hoce i za sve do kraja isto da odredi?
    public abstract void updateAppointment(Appointment appointment, String when);
    public abstract void updateAppointment(Appointment appointment, Places place);
    public abstract void updateAppointment(Appointment appointment, String startTime, String endTime);
    public abstract void updateAppointment(Appointment appointment, Map<String, String> additional);
    public abstract void updateAppointment(Appointment appointment, String when, String startTime, String endTime);
    public abstract void updateAppointment(Appointment appointment, String when, String startTime, String endTime, Places place);

    public abstract void search();
    public abstract void search(Places place);
    public abstract void search(Map<String, String> additional);
    public abstract void search(String startDate, String endDate);
    public abstract void search(String startDate, String endDate, Map<String, String> additional);
    public abstract void search(String startDate, String endDate, Places place);
    public abstract void search(String startDate, String endDate, Places place, Map<String, String> additional);
    public abstract void search(String day, String startDate, String endDate, Places place);
    public abstract void search(String day, String startDate, String endDate, Map<String, String> additional);
    public abstract void search(String day, String startDate, String endDate, Places place, Map<String, String> additional);

    public abstract void check(String startDate, String endDate);
    public abstract void check(String startDate, String endDate, Map<String, String> additional);
    public abstract void check(String startDate, String endDate, String day);
    public abstract void check(String startDate, String endDate, String day, Map<String, String> additional);
    public abstract void check(String startDate, String endDate, Places place);
    public abstract void check(String startDate, String endDate, String day, Places place);
    public abstract void check(String startTime, String endTime, String startDate, String endDate);
    public abstract void check(String startTime, String endTime, String startDate, String endDate, Map<String, String> additional);
    public abstract void check(String startTime, String endTime, String startDate, String endDate, Places place);
    public abstract void check(String startTime, String endTime, String day, String startDate, String endDate);
    public abstract void check(String startTime, String endTime, String day, String startDate, String endDate, Map<String, String> additional);
    public abstract void check(String startTime, String endTime, String day, String startDate, String endDate, Places place);

    public abstract void loadJSON(String filepath) throws IOException;

    public void loadCSV(String filepath) throws IOException {
        Reader in = new FileReader(filepath);
        CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader();
        CSVParser parser = new CSVParser(in, format);
        List<CSVRecord> records = parser.getRecords();

        Set<String> headers = records.iterator().next().toMap().keySet();
        List<String> stringsList = new ArrayList<>(headers);
//        schedule.getInfo().setHeaders(stringsList);

        for(CSVRecord record : records){
            Appointment appointment = new Appointment();

            for(int i = 0; i < headers.size(); i++){
                if(i == schedule.getInfo().getPlace()){
                    Places place = new Places(record.get(i));
                    int flag =0;

                    for(Places p : schedule.getPlaces()){
                        if(p.getName().equals(place.getName())){
                            appointment.setPlace(p);
                            flag=1;
                        }
                    }

                    if(flag == 0){
                        schedule.getPlaces().add(place);
                        appointment.setPlace(place);
                    }
                }else if(i == schedule.getInfo().getStartDate()){
                    appointment.setStartDate(LocalDate.parse(record.get(i), DateTimeFormatter.ofPattern(schedule.getInfo().getDateFormat())));
                }else if(i == schedule.getInfo().getEndDate()){
                    appointment.setEndDate(LocalDate.parse(record.get(i), DateTimeFormatter.ofPattern(schedule.getInfo().getDateFormat())));
                } else if(i == schedule.getInfo().getDay()){
                    appointment.setDay(record.get(i));
                }else if(i == schedule.getInfo().getTime()){
                    String[] time = record.get(i).split("-");
                    appointment.setStartTime(LocalTime.parse(time[0]));
                    appointment.setEndTime(LocalTime.parse(time[1]));
                }else{
                    appointment.getAdditional().put(stringsList.get(i),record.get(i));
                }
            }

            if(appointment.getDay() == null && appointment.getStartDate() != null && appointment.getEndDate() == null){
                appointment.setDay(getSchedule().getInfo().getDayFormat().get(appointment.getStartDate().getDayOfWeek().getValue()-1));
            }else if(appointment.getDay() != null && appointment.getStartDate() == null && appointment.getEndDate() == null){
                appointment.setStartDate(getSchedule().getStartDate());
                appointment.setEndDate(getSchedule().getEndDate());
            }else if(appointment.getDay() == null && appointment.getStartDate() != null && appointment.getEndDate() != null){
                appointment.setDay(getSchedule().getInfo().getDayFormat().get(appointment.getStartDate().getDayOfWeek().getValue()-1));
            }
            schedule.getAppointments().add(appointment);
        }
    }
    public void loadPlacesCSV(String filepath) throws IOException {
        Reader in = new FileReader(filepath);
        CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader();
        CSVParser parser = new CSVParser(in, format);
        List<CSVRecord> records = parser.getRecords();

        Set<String> headers = records.iterator().next().toMap().keySet();
        List<String> stringsList = new ArrayList<>(headers);

        for(CSVRecord record : records){
            Places place = new Places();

            for(int i = 0; i < headers.size(); i++){
                if(i == 0){
                    place.setName(record.get(i));
                }else{
                    place.getAdditional().put(stringsList.get(i),record.get(i));
                }
            }
            schedule.getPlaces().add(place);
        }
    }

}

