package raf.sk.projekat1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import raf.sk.projekat1.model.*;
import raf.sk.projekat1.specification.ScheduleService;

import java.io.*;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ScheduleServiceImpl extends ScheduleService {
    public ScheduleServiceImpl(Schedule schedule) {
        super(schedule);
    }

    public ScheduleServiceImpl(){}

    @Override
    public void loadJSON(String filepath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configOverride(LocalDate.class).setFormat(JsonFormat.Value.forPattern(schedule.getInfo().getDateFormat()));
        objectMapper.configOverride(LocalTime.class).setFormat(JsonFormat.Value.forPattern("HH:mm"));
        Info info = schedule.getInfo();
        schedule = objectMapper.readValue(new File(filepath), Schedule.class);
        schedule.setInfo(info);

        List<String> headers = new ArrayList<>(List.of("Place", "Start Date", "Time", "Day"));

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

            for (Map.Entry<String,String> entry : a.getAdditional().entrySet()){
                if(!headers.contains(entry.getKey())){
                    headers.add(entry.getKey());
                }
            }
        }

        getSchedule().getInfo().setHeaders(headers);

        sortAppointmentList();
    }
    @Override
    public void loadCSV(String filepath) throws IOException {
        Reader in = new FileReader(filepath);
        CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader();
        CSVParser parser = new CSVParser(in, format);
        List<CSVRecord> records = parser.getRecords();

        Set<String> headers = records.iterator().next().toMap().keySet();
        List<String> stringsList = new ArrayList<>(headers);
        schedule.getInfo().setHeaders(stringsList);

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
                }else if(i == schedule.getInfo().getDay()){
                    appointment.setDay(record.get(i));
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
        sortAppointmentList();
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
            return false;
        }
        return true;
    }
    @Override
    public boolean updateAppointment(Appointment appointment, String startTime, String endTime) {
        String time = startTime + "-" + endTime;
        String date = appointment.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        Appointment deleted = appointment;
        getSchedule().getAppointments().remove(deleted);
        if(addAppointment(date, appointment.getPlace().getName(), time, appointment.getAdditional())){
            getSchedule().getAppointments().remove(appointment);
        }else{
            getSchedule().getAppointments().add(deleted);
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

        return check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), getSchedule().getPlaces(), null);
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

        for(Places p : getSchedule().getPlaces()) {
            for (Map.Entry<String, String> entry : p.getAdditional().entrySet()) {
                if (additional.containsValue(entry.getValue())) {
                    validPlaces.add(p);
                }
            }
        }
        return check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), validPlaces, null);
    }
    @Override
    public List<String> check(String startDate, String endDate, String day) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        return check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), getSchedule().getPlaces(), day);
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

        return check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), validPlaces, day);
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

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
            }
        }

        return check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), validPlaces, null);
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

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
            }
        }

        return check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), validPlaces, day);
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

        return check(diffDays, sd, st, et, getSchedule().getPlaces(), null);
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

        for(Places p : getSchedule().getPlaces()) {
            for (Map.Entry<String, String> entry : p.getAdditional().entrySet()) {
                if (additional.containsValue(entry.getValue())) {
                    validPlaces.add(p);
                }
            }
        }

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        return check(diffDays, sd, st, et, validPlaces, null);
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

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
            }
        }

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        return check(diffDays, sd, st, et, validPlaces, null);
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

        return check(diffDays, sd, st, et, getSchedule().getPlaces(), day);
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

        for(Places p : getSchedule().getPlaces()) {
            for (Map.Entry<String, String> entry : p.getAdditional().entrySet()) {
                if (additional.containsValue(entry.getValue())) {
                    validPlaces.add(p);
                }
            }
        }

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        return check(diffDays, sd, st, et, validPlaces, day);
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

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
            }
        }

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        return check(diffDays, sd, st, et, validPlaces, day);
    }

    @Override
    public List<String> printAppointments(List<Appointment> appointments) {
        List<String> results = new ArrayList<>();

        for(Appointment a : appointments){
            StringBuilder result = new StringBuilder(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
            result.append(" ").append(a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
            result.append(" ").append(a.getStartTime()).append("-").append(a.getEndTime()).append(" ").append(a.getPlace().getName()).append(" ");
            for(Map.Entry<String,String> entry : a.getAdditional().entrySet()) {
                result.append(entry.getValue()).append(" ");
            }
            results.add(result.toString());
        }

        return results;
    }
    @Override
    public void sortAppointmentList(){
        getSchedule().getAppointments().sort(new Comparator<Appointment>() {
            @Override
            public int compare(Appointment o1, Appointment o2) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });

        getSchedule().getAppointments().sort(new Comparator<Appointment>() {
            @Override
            public int compare(Appointment o1, Appointment o2) {
                return o1.getPlace().getName().compareTo(o2.getPlace().getName());
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
    @Override
    public boolean overlappingAppointments(Appointment appointment, LocalTime sTime, LocalTime eTime, LocalDate date, String place){
        return appointment.getStartDate().equals(date) && (appointment.getStartTime().equals(sTime) || appointment.getEndTime().equals(eTime) || (appointment.getStartTime().isBefore(sTime) && appointment.getEndTime().isAfter(sTime)) || (appointment.getStartTime().isBefore(eTime)
                && appointment.getEndTime().isAfter(eTime))) && appointment.getPlace().getName().equals(place);
    }

    private List<String> check(long diffDays, LocalDate sd, LocalTime startTime, LocalTime endTime, List<Places> places, String day){
        List<String> results = new ArrayList<>();
        List<Appointment> appointments;
        int i = 0;

        while(i <= diffDays) {
            if (!(getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)) || getSchedule().getNonWorkingDates().contains(sd.plusDays(i)))){
                if(!(day != null && !(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1).equals(day)))) {
                    for (Places p : places) {
                        appointments = search(sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())), sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())), p);
                        if (!appointments.isEmpty()) {
                            StringBuilder result = new StringBuilder(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1) + " " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + " ");
                            LocalTime start = startTime;
                            int j = 0;

                            while (j < appointments.size()) {
                                if (!appointments.get(j).getStartTime().equals(start) && appointments.get(j).getStartTime().isAfter(start) && start.isBefore(endTime)) {
                                    if (appointments.get(j).getStartTime().isBefore(endTime)) {
                                        result.append(start).append("-").append(appointments.get(j).getStartTime()).append(" ");
                                    } else {
                                        result.append(start).append("-").append(endTime).append(" ");
                                    }
                                }
                                start = appointments.get(j).getEndTime();

                                j++;
                            }
                            if (!start.equals(endTime) && start.isBefore(endTime))
                                result.append(start).append("-").append(endTime).append(" ");

                            result.append(p.getName());

                            results.add(result.toString());
                        } else {
                            StringBuilder result = new StringBuilder(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1) + " " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + " ");
                            result.append(startTime).append("-").append(endTime);
                            result.append(p.getName());
                            results.add(String.valueOf(result));
                        }
                    }
                }
            }
            i++;
        }

//        while(true){
//            if(j >= appointments.size() || i > diffDays)
//                break;
//
//            Appointment a = appointments.get(j);
//            if(!(getSchedule().getNonWorkingDates().contains(sd.plusDays(i)) || getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)) || (day != null && !(day.equals(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)))))){
//                if (sd.plusDays(i).equals(a.getStartDate())) {
//                    for (Places p : places) {
//                        if (a.getPlace().getName().equals(p.getName())) {
//                            String result = getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1);
//                            result += ", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
//                            LocalTime start = startTime;
//
//                            while (j < appointments.size()) {
//                                Appointment a2 = appointments.get(j);
//                                if (a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())) {
//                                    if (!a2.getStartTime().equals(start) && appointments.get(j).getStartTime().isAfter(start) && start.isBefore(endTime)) {
//                                        if(a2.getStartTime().isAfter(endTime)){
//                                            result += " " + start + "-" + endTime;
//                                        }else{
//                                            result += " " + start + "-" + a2.getStartTime();
//                                        }
//                                    }
//                                    start = a2.getEndTime();
//                                    j++;
//                                }
//                                else {
//                                    break;
//                                }
//                            }
//                            if (!start.equals(endTime) && start.isBefore(endTime)){
//                                result += " " + start + "-" + endTime;
//                            }
//                            result += " " + a.getPlace().getName();
//                            results.add(result);
//                        }
//                        else{
//                            String result = getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1);
//                            result += ", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
//                            result += " " + startTime + "-" + endTime;
//                            result += " " + p.getName();
//                            results.add(result);
//                        }
//                    }
//                    i++;
//                }
//                else if(sd.plusDays(i).isAfter(a.getStartDate())){
//                    j++;
//                }
//                else{
//                    for (Places p : places) {
//                        String result = getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1);
//                        result += ", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
//                        result += " " + startTime + "-" + endTime;
//                        result += " " + p.getName();
//                        results.add(result);
//                    }
//                    i++;
//                }
//            }
//            else{
//                i++;
//            }
//        }
//
//        if(i != diffDays){
//            while(i <= diffDays) {
//                if(!(getSchedule().getNonWorkingDates().contains(sd.plusDays(i)) || getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)) || (day != null && !(day.equals(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)))))){
//                    for (Places p : places) {
//                        String result = getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1);
//                        result += ", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
//                        result += " " + startTime + "-" + endTime;
//                        result += " " + p.getName();
//                        results.add(result);
//                    }
//                }
//                i++;
//            }
//        }

        return results;
    }
}
