package raf.sk.projekat1.specification;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    public abstract void exportCSV(String filepath);
    public abstract void exportJSON(String filepath);
    //I - 13/10/2023 11:00-12:00
    //II - 13/10/2023 11:00-12:00
    public abstract void addAppointment(String when, String place, String time, Map<String, String> additional);
    //I - 13/10/2023 11:00-12:00 1 16/10/2023
    //II - 13/10/2023 11:00-12:00 everyWeek 30/10/2023
    public abstract void addAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat, Map<String, String> additional);

    public abstract void removeAppointment(String when, String place, String time);
    public abstract void removeAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat);

    public abstract void updateAppointment();

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

    public void loadCSV(String filepath) throws IOException {
        Reader in = new FileReader(filepath);
        CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader();
        CSVParser parser = new CSVParser(in, format);
        List<CSVRecord> records = parser.getRecords();

        Set<String> headers = records.iterator().next().toMap().keySet();
        List<String> stringsList = new ArrayList<>(headers);

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
                }else if(i == schedule.getInfo().getDate()){
                    appointment.setStartDate(LocalDate.parse(record.get(i), DateTimeFormatter.ofPattern(schedule.getInfo().getDateFormat())));
                    appointment.setDay(getSchedule().getInfo().getDayFormat().get(LocalDate.parse(record.get(i)).getDayOfWeek().getValue()-1));
                }else if(i == schedule.getInfo().getDay()){
                    appointment.setDay(record.get(i));
                    appointment.setStartDate(schedule.getStartDate());
                    appointment.setEndDate(schedule.getEndDate());
                }else if(i == schedule.getInfo().getTime()){
                    String[] time = record.get(i).split("-");
                    appointment.setStartTime(LocalTime.parse(time[0]));
                    appointment.setEndTime(LocalTime.parse(time[1]));
                }else{
                    appointment.getAdditional().put(stringsList.get(i),record.get(i));
                }
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

            for(int i=0;i<headers.size();i++){
                if(i == 0){
                    place.setName(record.get(i));
                }else{
                    place.getAdditional().put(stringsList.get(i),record.get(i));
                }
            }
            schedule.getPlaces().add(place);
        }
    }
    public void loadJSON(String filepath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configOverride(LocalDate.class).setFormat(JsonFormat.Value.forPattern(schedule.getInfo().getDateFormat()));
        objectMapper.configOverride(LocalTime.class).setFormat(JsonFormat.Value.forPattern("HH:mm"));
        Info info = schedule.getInfo();
        schedule = objectMapper.readValue(new File(filepath), Schedule.class);
        schedule.setInfo(info);

        //impl2
        for(Appointment a : schedule.getAppointments()){
            if(a.getStartDate() == null) {
                a.setStartDate(schedule.getStartDate());
                a.setEndDate(schedule.getEndDate());
            }

            if(!getSchedule().getPlaces().contains(a.getPlace())){
                for(Places p : getSchedule().getPlaces()){
                    if(a.getPlace().getName().equals(p.getName())){
                        a.setPlace(p);
                    }
                }
            }
        }

        sortAppointmentList();

    }

    protected void sortAppointmentList(){
        Collections.sort(getSchedule().getAppointments(), new Comparator<Appointment>(){
            @Override
            public int compare(Appointment o1, Appointment o2) {
                return o1.getPlace().getName().compareTo(o2.getPlace().getName());
            }
        });

        Collections.sort(getSchedule().getAppointments(), new Comparator<Appointment>(){
            @Override
            public int compare(Appointment o1, Appointment o2) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });

        Collections.sort(getSchedule().getAppointments(), new Comparator<Appointment>(){
            @Override
            public int compare(Appointment o1, Appointment o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });

        Collections.sort(getSchedule().getPlaces(), new Comparator<Places>(){
            @Override
            public int compare(Places o1, Places o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

}

