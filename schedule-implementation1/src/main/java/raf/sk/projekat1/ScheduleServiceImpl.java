package raf.sk.projekat1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import raf.sk.projekat1.model.*;
import raf.sk.projekat1.specification.ScheduleService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ScheduleServiceImpl extends ScheduleService {
    public ScheduleServiceImpl(Schedule schedule) {
        super(schedule);
    }

    public ScheduleServiceImpl(){}

    @Override
    public void exportCSV(String filepath) throws IOException {

//        try (
//                BufferedWriter writer = Files.newBufferedWriter(Paths.get(filepath));
//
//                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader((ResultSet) getSchedule().getInfo().getHeaders()))
//
//        ) {
//
//            for(Appointment a: getSchedule().getAppointments()){
//                csvPrinter.printRecord(a.getPlace().getName(),a.getStartDate(),a.getStartTime() + "-" + a.getEndTime());
//            }
//
//
//            csvPrinter.flush();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        try (
//                BufferedWriter writer = Files.newBufferedWriter(Paths.get(filepath));
//
//                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
//                        .withHeader("ID", "Name", "Designation", "Company"));
//        ) {
//            csvPrinter.printRecord("1", "Sundar Pichai ♥", "CEO", "Google");
//            csvPrinter.printRecord("2", "Satya Nadella", "CEO", "Microsoft");
//            csvPrinter.printRecord("3", "Tim cook", "CEO", "Apple");
//
//            csvPrinter.printRecord(Arrays.asList("4", "Mark Zuckerberg", "CEO", "Facebook"));
//
//            csvPrinter.flush();
//        }


        FileWriter fileWriter = new FileWriter(filepath);
        CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

        for (Appointment appointment : getSchedule().getAppointments()) {
            csvPrinter.printRecord(
                    appointment.getPlace().getName(),
                    appointment.getStartDate(),
                    appointment.getStartTime() + "-" + appointment.getEndTime()
            );
        }

        csvPrinter.close();
        fileWriter.close();


    }
    @Override
    public void exportJSON(String filepath) {
    }

    @Override
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

        Appointment newAppointment = new Appointment(startTime, endTime, date, getSchedule().getInfo().getDayFormat().get(date.getDayOfWeek().getValue()-1), additional);

        for(Places p : getSchedule().getPlaces()){
            if(p.getName().equals(place)){
                newAppointment.setPlace(p);
            }
        }
        getSchedule().getAppointments().add(newAppointment);
        sortAppointmentList();

        return true;
    }
    @Override
    public boolean addAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat, Map<String, String> additional) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return false;

        String[] split = time.split("-");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);
        if(startTime.isBefore(getSchedule().getStartTime()) || endTime.isAfter(getSchedule().getEndTime()))
            return false;

        Duration diff = Duration.between(start.atStartOfDay(), end.atStartOfDay());
        long diffDays = diff.toDays();
        int j;

        if(repeat.equals(AppointmentRepeat.EVERY_DAY)){
            j = 1;
        }else if(repeat.equals(AppointmentRepeat.EVERY_WEEK)){
            j = 7;
        }else{
            j = 7;
            switch (repeat){
                case EVERY_MONDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.MONDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
                case EVERY_TUESDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.TUESDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
                case EVERY_WEDNESDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.WEDNESDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
                case EVERY_THURSDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.THURSDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
                case EVERY_FRIDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.FRIDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
                case EVERY_SATURDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
                case EVERY_SUNDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.SUNDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
            }
        }

        List<Appointment> validAppointments = new ArrayList<>();
        diff = Duration.between(start.atStartOfDay(), end.atStartOfDay());
        diffDays = diff.toDays();

        for(int i = 0; i <= diffDays; i+=j) {
            for (Appointment a : getSchedule().getAppointments()) {
//                if((a.getStartDate().equals(start.plusDays(i)) && (a.getStartTime().equals(startTime) || a.getEndTime().equals(endTime) || (a.getStartTime().isBefore(startTime) && a.getEndTime().isAfter(startTime)) || (a.getStartTime().isBefore(endTime)
//                        && a.getEndTime().isAfter(endTime))) && a.getPlace().getName().equals(place))){
                if(overlappingAppointments(a, startTime, endTime, start.plusDays(i), place)){
                    return false;
                }
            }

            if (!(getSchedule().getNonWorkingDates().contains(start.plusDays(i)) || getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(start.plusDays(i).getDayOfWeek().getValue() - 1)))) {
                Appointment newAppointment = new Appointment(startTime, endTime, start.plusDays(i), getSchedule().getInfo().getDayFormat().get(start.plusDays(i).getDayOfWeek().getValue() - 1), additional);
                for (Places p : getSchedule().getPlaces()) {
                    if (p.getName().equals(place)) {
                        newAppointment.setPlace(p);
                    }
                }
                validAppointments.add(newAppointment);
            }
        }

        for(Appointment a : validAppointments){
            getSchedule().getAppointments().add(a);
        }

        sortAppointmentList();
        return true;
    }

    @Override
    public boolean removeAppointment(String when, String place, String time) {
        LocalDate date = LocalDate.parse(when, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        String[] split = time.split("-");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);
        Appointment appointment = null;

        for(Appointment a : getSchedule().getAppointments()){
//            if(a.getStartDate().equals(date) && a.getStartTime().equals(startTime) && a.getEndTime().equals(endTime) && a.getPlace().getName().equals(place)){
            if(a.equals(startTime, endTime, date, place)){
                appointment = a;
                break;
            }
        }

        if(!(appointment == null)) {
            getSchedule().getAppointments().remove(appointment);
            return true;
        }
        return false;
    }
    @Override
    public boolean removeAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        String[] split = time.split("-");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);

        Duration diff = Duration.between(start.atStartOfDay(), end.atStartOfDay());
        long diffDays = diff.toDays();
        int k;

        if(repeat.equals(AppointmentRepeat.EVERY_DAY)){
            k = 1;
        }else if(repeat.equals(AppointmentRepeat.EVERY_WEEK)){
            k = 7;
        }else{
            k = 7;
            switch (repeat){
                case EVERY_MONDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.MONDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
                case EVERY_TUESDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.TUESDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
                case EVERY_WEDNESDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.WEDNESDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
                case EVERY_THURSDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.THURSDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
                case EVERY_FRIDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.FRIDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
                case EVERY_SATURDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
                case EVERY_SUNDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(start.plusDays(i).getDayOfWeek().equals(DayOfWeek.SUNDAY)){
                            start = start.plusDays(i);
                            break;
                        }
                    }
                    break;
            }
        }

        List<Appointment> validAppointments = new ArrayList<>();
        diff = Duration.between(start.atStartOfDay(), end.atStartOfDay());
        diffDays = diff.toDays();
        int i = 0, j = 0, counter = 0;

        while(i <= diffDays) {
            if (!(getSchedule().getNonWorkingDates().contains(start.plusDays(i)) || getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(start.plusDays(i).getDayOfWeek().getValue() - 1)))) {
                while (j < getSchedule().getAppointments().size()) {
                    Appointment a = getSchedule().getAppointments().get(j);
//                        if (a.getStartDate().equals(start.plusDays(i)) && a.getStartTime().equals(startTime) && a.getEndTime().equals(endTime) && a.getPlace().getName().equals(place)) {
                    if(a.equals(startTime, endTime, start.plusDays(i), place)){
                        validAppointments.add(a);
                        counter++;
                        i+=k;
                        j++;
                        break;
                    }
                    j++;
                }
            }else{
                counter++;
                i+=k;
            }
        }

        if(!(counter == diffDays/k+1)) {
            return false;
        }

        for(Appointment a : validAppointments){
            getSchedule().getAppointments().remove(a);
        }

        sortAppointmentList();
        return true;
    }

    @Override
    public Appointment find(String when, String place, String time) {
        LocalDate date = LocalDate.parse(when, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        String[] split = time.split("-");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);

        for(Appointment a : getSchedule().getAppointments()){
//            if(a.getStartDate().equals(date) && a.getStartTime().equals(startTime) && a.getEndTime().equals(endTime) && a.getPlace().getName().equals(place)){
            if(a.equals(startTime, endTime, date, place)) {
                return a;
            }
        }
        return null;
    }

    @Override
    public boolean updateAppointment(Appointment appointment, String when) {
        String time = appointment.getStartTime() + "-" + appointment.getEndTime();

        if(addAppointment(when, appointment.getPlace().getName(), time, appointment.getAdditional())){
            getSchedule().getAppointments().remove(appointment);
        }else{
            System.out.println("greska");
            return false;
        }
        return true;
    }
    @Override
    public boolean updateAppointment(Appointment appointment, Places place) {
        String time = appointment.getStartTime() + "-" + appointment.getEndTime();
        String date = appointment.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        if(addAppointment(date, place.getName(), time, appointment.getAdditional())){
            getSchedule().getAppointments().remove(appointment);
        }else{
            System.out.println("greska");
            return false;
        }
        return true;
    }
    @Override
    public boolean updateAppointment(Appointment appointment, String startTime, String endTime) {
        String time = startTime + "-" + endTime;
        String date = appointment.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        if(addAppointment(date, appointment.getPlace().getName(), time, appointment.getAdditional())){
            getSchedule().getAppointments().remove(appointment);
        }else{
            System.out.println("greska");
            return false;
        }
        return true;
    }
    @Override
    public boolean updateAppointment(Appointment appointment, Map<String, String> additional) {
        for (Map.Entry<String, String> entry : additional.entrySet()) {
            appointment.getAdditional().put(entry.getKey(), entry.getValue());
        }
        return true;
    }
    @Override
    public boolean updateAppointment(Appointment appointment, String when, String startTime, String endTime) {
        String time = startTime + "-" + endTime;

        if(addAppointment(when, appointment.getPlace().getName(), time, appointment.getAdditional())){
            getSchedule().getAppointments().remove(appointment);
        }else{
            System.out.println("greska");
            return false;
        }
        return true;
    }
    @Override
    public boolean updateAppointment(Appointment appointment, String when, String startTime, String endTime, Places place) {
        String time = startTime + "-" + endTime;

        if(addAppointment(when, place.getName(), time, appointment.getAdditional())){
            getSchedule().getAppointments().remove(appointment);
        }else{
            System.out.println("greska");
            return false;
        }
        return true;
    }

//            String result = getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue()-1);
//            result += ", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
//            result += " " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName();

    @Override
    public List<Appointment> search(String startDate, String endDate) {
        List<Appointment> results = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return results;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                results.add(a);
            }
        }
        return results;
    }
    @Override
    public List<Appointment> search(String startDate, String endDate, Map<String, String> additional) {
        List<Appointment> results = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return results;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                for(Map.Entry<String,String> entry : additional.entrySet()) {
                    if (a.getAdditional().containsValue(entry.getValue())) {
                        results.add(a);
                    }
                    break;
                }
            }
        }

        return results;
    }
    @Override
    public List<Appointment> search(String startDate, String endDate, Places place) {
        List<Appointment> results = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return results;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                if(a.getPlace().getName().equals(place.getName())) {
                    results.add(a);
                }
            }
        }
        return  results;
    }
    @Override
    public List<Appointment> search(String startDate, String endDate, Places place, Map<String, String> additional) {
        List<Appointment> results = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return results;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                for(Map.Entry<String,String> entry : additional.entrySet()) {
                    if (a.getAdditional().containsValue(entry.getValue())) {
                        if(a.getPlace().getName().equals(place.getName())) {
                            results.add(a);
                        }
                    }
                }
            }
        }
        return results;
    }
    @Override
    public List<Appointment> search(String day, String startDate, String endDate, Places place) {
        List<Appointment> results = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return results;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                if(a.getPlace().getName().equals(place.getName())) {
                    if(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1).equals(day)) {
                        results.add(a);
                    }
                }
            }
        }
        return results;
    }
    @Override
    public List<Appointment> search(String day, String startDate, String endDate, Map<String, String> additional) {
        List<Appointment> results = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return results;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                for(Map.Entry<String,String> entry : additional.entrySet()) {
                    if (a.getAdditional().containsValue(entry.getValue())) {
                        if (getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1).equals(day)) {
                            results.add(a);
                        }
                    }
                }
            }
        }
        return results;
    }
    @Override
    public List<Appointment> search(String day, String startDate, String endDate, Places place, Map<String, String> additional) {
        List<Appointment> results = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return results;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                for(Map.Entry<String,String> entry : additional.entrySet()) {
                    if (a.getAdditional().containsValue(entry.getValue())) {
                        if (getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1).equals(day)) {
                            if(a.getPlace().getName().equals(place.getName())) {
                                results.add(a);
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    @Override
    public List<String> check(String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        return check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), getSchedule().getAppointments(), getSchedule().getPlaces(), null);
    }
    @Override
    public List<String> check(String startDate, String endDate, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        List<Places> validPlaces = new ArrayList<>();
        List<Appointment> validAppointments = new ArrayList<>();

        for(Places p : getSchedule().getPlaces()) {
            for (Map.Entry<String, String> entry : p.getAdditional().entrySet()) {
                if (additional.containsValue(entry.getValue())) {
                    validPlaces.add(p);
                }
            }
        }

        for(Appointment a : getSchedule().getAppointments()){
            for(Places p : validPlaces){
                if(a.getPlace().getName().equals(p.getName())){
                    validAppointments.add(a);
                }
            }
        }

        return check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), validAppointments, validPlaces, null);
    }
    @Override
    public List<String> check(String startDate, String endDate, String day) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        return check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), getSchedule().getAppointments(), getSchedule().getPlaces(), day);
    }
    @Override
    public List<String> check(String startDate, String endDate, String day, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        List<Places> validPlaces = new ArrayList<>();
        List<Appointment> validAppointments = new ArrayList<>();

        for(Places p : getSchedule().getPlaces()) {
            for (Map.Entry<String, String> entry : p.getAdditional().entrySet()) {
                if (additional.containsValue(entry.getValue())) {
                    validPlaces.add(p);
                }
            }
        }

        for(Appointment a : getSchedule().getAppointments()){
            for(Places p : validPlaces){
                if(a.getPlace().getName().equals(p.getName())){
                    validAppointments.add(a);
                }
            }
        }

        return check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), validAppointments, validPlaces, day);
    }
    @Override
    public List<String> check(String startDate, String endDate, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        List<Places> validPlaces = new ArrayList<>();
        List<Appointment> validAppointments = search(place);

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
            }
        }

//        for(Appointment a : getSchedule().getAppointments()){
//            for(Places p : validPlaces){
//                if(a.getPlace().getName().equals(p.getName())){
//                    validAppointments.add(a);
//                }
//            }
//        }

        return check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), validAppointments, validPlaces, null);
    }
    @Override
    public List<String> check(String startDate, String endDate, String day, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        List<Places> validPlaces = new ArrayList<>();
        List<Appointment> validAppointments = search(place);

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
            }
        }

//        for(Appointment a : getSchedule().getAppointments()){
//            for(Places p : validPlaces){
//                if(a.getPlace().getName().equals(p.getName())){
//                    validAppointments.add(a);
//                }
//            }
//        }

        return check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), validAppointments, validPlaces, day);
    }
    @Override
    public List<String> check(String startTime, String endTime, String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        return check(diffDays, sd, st, et, getSchedule().getAppointments(), getSchedule().getPlaces(), null);
    }
    @Override
    public List<String> check(String startTime, String endTime, String startDate, String endDate, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        List<Places> validPlaces = new ArrayList<>();
        List<Appointment> validAppointments = new ArrayList<>();

        for(Places p : getSchedule().getPlaces()) {
            for (Map.Entry<String, String> entry : p.getAdditional().entrySet()) {
                if (additional.containsValue(entry.getValue())) {
                    validPlaces.add(p);
                }
            }
        }

        for(Appointment a : getSchedule().getAppointments()){
            for(Places p : validPlaces){
                if(a.getPlace().getName().equals(p.getName())){
                    validAppointments.add(a);
                }
            }
        }

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        return check(diffDays, sd, st, et, validAppointments, validPlaces, null);
    }
    @Override
    public List<String> check(String startTime, String endTime, String startDate, String endDate, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        List<Places> validPlaces = new ArrayList<>();
        List<Appointment> validAppointments = search(place);

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
            }
        }

//        for(Appointment a : getSchedule().getAppointments()){
//            for(Places p : validPlaces){
//                if(a.getPlace().getName().equals(p.getName())){
//                    validAppointments.add(a);
//                }
//            }
//        }

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        return check(diffDays, sd, st, et, validAppointments, validPlaces, null);
    }
    @Override
    public List<String> check(String startTime, String endTime, String day, String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        return check(diffDays, sd, st, et, getSchedule().getAppointments(), getSchedule().getPlaces(), day);
    }
    @Override
    public List<String> check(String startTime, String endTime, String day, String startDate, String endDate, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        List<Places> validPlaces = new ArrayList<>();
        List<Appointment> validAppointments = new ArrayList<>();

        for(Places p : getSchedule().getPlaces()) {
            for (Map.Entry<String, String> entry : p.getAdditional().entrySet()) {
                if (additional.containsValue(entry.getValue())) {
                    validPlaces.add(p);
                }
            }
        }

        for(Appointment a : getSchedule().getAppointments()){
            for(Places p : validPlaces){
                if(a.getPlace().getName().equals(p.getName())){
                    validAppointments.add(a);
                }
            }
        }

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        return check(diffDays, sd, st, et, validAppointments, validPlaces, day);
    }
    @Override
    public List<String> check(String startTime, String endTime, String day, String startDate, String endDate, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        List<Places> validPlaces = new ArrayList<>();
        List<Appointment> validAppointments = search(place);

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
            }
        }

//        for(Appointment a : getSchedule().getAppointments()){
//            for(Places p : validPlaces){
//                if(a.getPlace().getName().equals(p.getName())){
//                    validAppointments.add(a);
//                }
//            }
//        }

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        return check(diffDays, sd, st, et, validAppointments, validPlaces, day);
    }

    @Override
    public void printAppointments(List<Appointment> appointments) {
        for(Appointment a : appointments){
            String result = getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue()-1);
            result += ", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
            result += " " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName();
            System.out.println(result);
        }
    }

    private List<String> check(long diffDays, LocalDate sd, LocalTime startTime, LocalTime endTime, List<Appointment> appointments, List<Places> places, String day){
        List<String> results = new ArrayList<>();
        int i = 0, j = 0;

        while(true){
            if(j >= appointments.size() || i >= diffDays)
                break;

            Appointment a = appointments.get(j);
            if(!(getSchedule().getNonWorkingDates().contains(sd.plusDays(i)) || getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)) || (day != null && !(day.equals(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)))))){
                if (sd.plusDays(i).equals(a.getStartDate())) {
                    for (Places p : places) {
                        if (a.getPlace().getName().equals(p.getName())) {
                            String result = getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1);
                            result += ", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
//                            System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
//                            System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                            LocalTime start = startTime;

                            while (j < appointments.size()) {
                                Appointment a2 = appointments.get(j);
                                if (a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())) {
                                    if (!a2.getStartTime().equals(start)) {
                                        if(a2.getStartTime().isAfter(endTime)){
                                            result += " " + start + "-" + endTime;
//                                            System.out.print(" " + start + "-" + endTime);
                                        }else{
                                            result += " " + start + "-" + a2.getStartTime();
                                            System.out.print(" " + start + "-" + a2.getStartTime());
                                        }
                                    }
                                    start = a2.getEndTime();
                                    j++;
                                }
                                else {
                                    break;
                                }
                            }
                            if (!start.equals(endTime)){
                                result += " " + start + "-" + endTime;
//                                System.out.print(" " + start + "-" + endTime);
                            }
                            result += " " + a.getPlace().getName();
//                            System.out.println(" " + a.getPlace().getName());
                            results.add(result);
                        }
                        else{
                            String result = getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1);
                            result += ", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
                            result += " " + startTime + "-" + endTime;
                            result += " " + p.getName();
                            results.add(result);
//                            System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
//                            System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
//                            System.out.print(" " + startTime + "-" + endTime);
//                            System.out.println(" " + p.getName());
                        }
                    }
                    i++;
                }
                else if(sd.plusDays(i).isAfter(a.getStartDate())){
                    j++;
                }
                else{
                    for (Places p : places) {
                        String result = getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1);
                        result += ", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
                        result += " " + startTime + "-" + endTime;
                        result += " " + p.getName();
                        results.add(result);
//                        System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
//                        System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
//                        System.out.print(" " + startTime + "-" + endTime);
//                        System.out.println(" " + p.getName());
                    }
                    i++;
                }
            }
            else{
                i++;
            }
        }

        if(i != diffDays){
            while(i <= diffDays) {
                if(!(getSchedule().getNonWorkingDates().contains(sd.plusDays(i)) || getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)) || (day != null && !(day.equals(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)))))){
                    for (Places p : places) {
                        String result = getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1);
                        result += ", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
                        result += " " + startTime + "-" + endTime;
                        result += " " + p.getName();
                        results.add(result);
//                        System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
//                        System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
//                        System.out.print(" " + startTime + "-" + endTime);
//                        System.out.println(" " + p.getName());
                    }
                }
                i++;
            }
        }

        return results;
    }

    @Override
    public void loadJSON(String filepath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configOverride(LocalDate.class).setFormat(JsonFormat.Value.forPattern(schedule.getInfo().getDateFormat()));
        objectMapper.configOverride(LocalTime.class).setFormat(JsonFormat.Value.forPattern("HH:mm"));
        Info info = schedule.getInfo();
        schedule = objectMapper.readValue(new File(filepath), Schedule.class);
        schedule.setInfo(info);

        //3/10/2023
        //3/10/2023 Uto
        for(Appointment a : schedule.getAppointments()){
            if(a.getDay() == null){
                a.setDay(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue()-1));
            }

            if(!getSchedule().getPlaces().contains(a.getPlace())){
                for(Places p : getSchedule().getPlaces()){
                    if(a.getPlace().getName().equals(p.getName())){
                        a.setPlace(p);
                    }
                }
            }
        }


        List<String> headers = new ArrayList<>();
        headers.add("Place");
        headers.add("Date");
        headers.add("Time");


//        getSchedule().getInfo().setHeaders();

//        for(Appointment a: appointments){
//            String time = a.getStartTime() + "-" + a.getEndTime();
//            if(!a.getDay().equals(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue()-1))){
//                Duration diff = Duration.between(a.getStartDate().atStartOfDay(), a.getEndDate().atStartOfDay());
//                long diffDays = diff.toDays();
//                LocalDate start = a.getStartDate();
//
//
//
//                for(int i = 0; i <= diffDays; i++) {
//
//                    if(getSchedule().getInfo().getDayFormat().get(start.plusDays(i).getDayOfWeek().getValue()-1).equals(a.getDay())){
//                        a.setStartDate(start.plusDays(i));
//                        break;
//                    }
//                }
//            }

//            String startDate = a.getStartDate().plusDays(7).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
//            String endDate = a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
//
//            addAppointment(startDate,endDate,time,a.getPlace().getName(),AppointmentRepeat.EVERY_WEEK,a.getAdditional());
//        }
        sortAppointmentList();
    }

    private void sortAppointmentList(){
        getSchedule().getAppointments().sort(new Comparator<Appointment>() {
            @Override
            public int compare(Appointment o1, Appointment o2) {
                return o1.getPlace().getName().compareTo(o2.getPlace().getName());
            }
        });

        getSchedule().getAppointments().sort(new Comparator<Appointment>() {
            @Override
            public int compare(Appointment o1, Appointment o2) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });

        getSchedule().getAppointments().sort(new Comparator<Appointment>() {
            @Override
            public int compare(Appointment o1, Appointment o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });

        getSchedule().getPlaces().sort(new Comparator<Places>() {
            @Override
            public int compare(Places o1, Places o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    private boolean overlappingAppointments(Appointment appointment, LocalTime sTime, LocalTime eTime, LocalDate date, String place){
        return !appointment.getStartDate().equals(date) || (!appointment.getStartTime().equals(sTime) && !appointment.getEndTime().equals(eTime) && (!appointment.getStartTime().isBefore(sTime) || !appointment.getEndTime().isAfter(sTime)) && (!appointment.getStartTime().isBefore(eTime)
                || !appointment.getEndTime().isAfter(eTime))) || !appointment.getPlace().getName().equals(place);
    }
}
