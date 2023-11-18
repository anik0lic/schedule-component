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
//    public abstract boolean addAppointment(String when, String place, String time, Map<String, String> additional);
    public boolean addAppointment(String when, String place, String time, Map<String, String> additional) {
        LocalDate date = LocalDate.parse(when, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(date.isBefore(getSchedule().getStartDate()) || date.isAfter(getSchedule().getEndDate())
                || (getSchedule().getNonWorkingDates().contains(date) && getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(date.getDayOfWeek().getValue()-1))))
            return false;

        String[] split = time.split("-");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);
        if(startTime.isBefore(getSchedule().getStartTime()) || endTime.isAfter(getSchedule().getEndTime()))
            return false;

        for(Appointment a : getSchedule().getAppointments()){
//            if((a.getStartDate().equals(date) && (a.getStartTime().equals(startTime) || a.getEndTime().equals(endTime) || (a.getStartTime().isBefore(startTime) && a.getEndTime().isAfter(startTime)) || (a.getStartTime().isBefore(endTime)
//                    && a.getEndTime().isAfter(endTime))) && a.getPlace().getName().equals(place))){
            if(overlappingAppointments(a, startTime, endTime, date, place)){
                return false;
            }
        }

        Appointment newAppointment = new Appointment(startTime, endTime, date, date, getSchedule().getInfo().getDayFormat().get(date.getDayOfWeek().getValue()-1), additional);

        for(Places p : getSchedule().getPlaces()){
            if(p.getName().equals(place)){
                newAppointment.setPlace(p);
            }
        }
        getSchedule().getAppointments().add(newAppointment);
        sortAppointmentList();

        return true;
    }
    //I - 13/10/2023 11:00-12:00 1 16/10/2023
    //II - 13/10/2023 11:00-12:00 everyWeek 30/10/2023
    public abstract boolean addAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat, Map<String, String> additional);

    public abstract boolean removeAppointment(String when, String place, String time);
    public abstract boolean removeAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat);

    public abstract Appointment find(String when, String place, String time);

    //da ima boolean da li hoce i za sve do kraja isto da odredi?
    public abstract boolean updateAppointment(Appointment appointment, String when);
    public abstract boolean updateAppointment(Appointment appointment, Places place);
    public abstract boolean updateAppointment(Appointment appointment, String startTime, String endTime);
    public abstract boolean updateAppointment(Appointment appointment, Map<String, String> additional);
    public abstract boolean updateAppointment(Appointment appointment, String when, String startTime, String endTime);
    public abstract boolean updateAppointment(Appointment appointment, String when, String startTime, String endTime, Places place);

    public List<Appointment> search(){
        return new ArrayList<>(getSchedule().getAppointments());
    }
    public List<Appointment> search(Places place){
        List<Appointment> results = new ArrayList<>();

        for(Appointment a : getSchedule().getAppointments()){
            if(a.getPlace().getName().equals(place.getName())) {
                results.add(a);
            }
        }
        return results;
    }
    public List<Appointment> search(Map<String, String> additional){
        List<Appointment> results = new ArrayList<>();

        for(Appointment a : getSchedule().getAppointments()){
            int flag = additional.size();
            for(Map.Entry<String,String> entry : additional.entrySet()){
                if(a.getAdditional().containsValue(entry.getValue())){
                    flag--;
                }
            }
            if(flag == 0){
                results.add(a);
            }
        }
        return results;
    }
    public abstract List<Appointment> search(String startDate, String endDate);
    public abstract List<Appointment> search(String startDate, String endDate, Map<String, String> additional);
    public abstract List<Appointment> search(String startDate, String endDate, Places place);
    public abstract List<Appointment> search(String startDate, String endDate, Places place, Map<String, String> additional);
    public abstract List<Appointment> search(String day, String startDate, String endDate, Places place);
    public abstract List<Appointment> search(String day, String startDate, String endDate, Map<String, String> additional);
    public abstract List<Appointment> search(String day, String startDate, String endDate, Places place, Map<String, String> additional);

    public abstract List<String> check(String startDate, String endDate);
    public abstract List<String> check(String startDate, String endDate, Map<String, String> additional);
    public abstract List<String> check(String startDate, String endDate, String day);
    public abstract List<String> check(String startDate, String endDate, String day, Map<String, String> additional);
    public abstract List<String> check(String startDate, String endDate, Places place);
    public abstract List<String> check(String startDate, String endDate, String day, Places place);
    public abstract List<String> check(String startTime, String endTime, String startDate, String endDate);
    public abstract List<String> check(String startTime, String endTime, String startDate, String endDate, Map<String, String> additional);
    public abstract List<String> check(String startTime, String endTime, String startDate, String endDate, Places place);
    public abstract List<String> check(String startTime, String endTime, String day, String startDate, String endDate);
    public abstract List<String> check(String startTime, String endTime, String day, String startDate, String endDate, Map<String, String> additional);
    public abstract List<String> check(String startTime, String endTime, String day, String startDate, String endDate, Places place);

    public abstract void printAppointments(List<Appointment> appointments);
    public abstract boolean overlappingAppointments(Appointment a, LocalTime sTime, LocalTime eTime, LocalDate date, String place);
    public abstract void sortAppointmentList();

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

