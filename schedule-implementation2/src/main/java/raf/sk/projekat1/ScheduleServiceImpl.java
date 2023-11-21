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

import static java.util.List.*;

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

        List<String> headers = new ArrayList<>(of("Place", "Start Date", "End Date", "Time", "Day"));
        //Pon
        //1/10/2023 - 10/10/2023
        //1/10/2023 - 10/10/2023 Pon
        //02/10/2023 stavice da se samo tog dana desava
        for(Appointment a : schedule.getAppointments()){
            if(a.getDay() != null && a.getStartDate() == null && a.getEndDate() == null){
                for(int i = 0; i < 7; i++) {
                    if(getSchedule().getInfo().getDayFormat().get(getSchedule().getStartDate().plusDays(i).getDayOfWeek().getValue()-1).equals(a.getDay())){
                        a.setStartDate(getSchedule().getStartDate().plusDays(i));
                    }
                    if(getSchedule().getInfo().getDayFormat().get(getSchedule().getEndDate().minusDays(i).getDayOfWeek().getValue()-1).equals(a.getDay())){
                        a.setEndDate(getSchedule().getEndDate().minusDays(i));
                    }
                }
            }
            else if(a.getDay() == null && a.getStartDate() != null && a.getEndDate() != null){
                a.setDay(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue()-1));
                if(!getSchedule().getInfo().getDayFormat().get(a.getEndDate().getDayOfWeek().getValue()-1).equals(a.getDay())){
                    for(int i = 0; i < 7; i++) {
                        if(getSchedule().getInfo().getDayFormat().get(a.getEndDate().minusDays(i).getDayOfWeek().getValue()-1).equals(a.getDay())){
                            a.setEndDate(a.getEndDate().minusDays(i));
                        }
                    }
                }
            }
            else if(a.getDay() == null && a.getStartDate() != null && a.getEndDate() == null){
                a.setDay(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue()-1));
                a.setEndDate(a.getStartDate());
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

            if(appointment.getDay() != null && appointment.getStartDate() == null && appointment.getEndDate() == null){
                for(int i = 0; i < 7; i++) {
                    if(getSchedule().getInfo().getDayFormat().get(getSchedule().getStartDate().plusDays(i).getDayOfWeek().getValue()-1).equals(appointment.getDay())){
                        appointment.setStartDate(getSchedule().getStartDate().plusDays(i));
                    }
                    if(getSchedule().getInfo().getDayFormat().get(getSchedule().getEndDate().minusDays(i).getDayOfWeek().getValue()-1).equals(appointment.getDay())){
                        appointment.setEndDate(getSchedule().getEndDate().minusDays(i));
                    }
                }
            }
            else if(appointment.getDay() == null && appointment.getStartDate() != null && appointment.getEndDate() != null){
                appointment.setDay(getSchedule().getInfo().getDayFormat().get(appointment.getStartDate().getDayOfWeek().getValue()-1));
                if(!getSchedule().getInfo().getDayFormat().get(appointment.getEndDate().getDayOfWeek().getValue()-1).equals(appointment.getDay())){
                    for(int i = 0; i < 7; i++) {
                        if(getSchedule().getInfo().getDayFormat().get(appointment.getEndDate().minusDays(i).getDayOfWeek().getValue()-1).equals(appointment.getDay())){
                            appointment.setEndDate(appointment.getEndDate().minusDays(i));
                        }
                    }
                }
            }
            else if(appointment.getDay() == null && appointment.getStartDate() != null && appointment.getEndDate() == null){
                appointment.setDay(getSchedule().getInfo().getDayFormat().get(appointment.getStartDate().getDayOfWeek().getValue()-1));
                appointment.setEndDate(appointment.getStartDate());
            }

            schedule.getAppointments().add(appointment);
        }
        sortAppointmentList();
    }

    @Override
    public boolean addAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return false;

        String[] split = time.split("-");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);
        if(startTime.isBefore(getSchedule().getStartTime()) || endTime.isAfter(getSchedule().getEndTime()))
            return false;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();
        List<Appointment> validAppointments = new ArrayList<>();

        if(repeat.equals(AppointmentRepeat.EVERY_DAY)){
            for(int i = 0; i <= diffDays; i++) {
                if((getSchedule().getNonWorkingDates().contains(sd.plusDays(i)) && getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)))){
                    continue;
                }

                for (Appointment a : getSchedule().getAppointments()) {
                    if(overlappingAppointments(a, startTime, endTime, sd.plusDays(i), place)){
                        return false;
                    }
                }

                Appointment newAppointment = new Appointment(startTime, endTime, sd.plusDays(i), sd.plusDays(i), getSchedule().getInfo().getDayFormat().get(sd.getDayOfWeek().getValue() - 1), additional);
                for (Places p : getSchedule().getPlaces()) {
                    if (p.getName().equals(place)) {
                        newAppointment.setPlace(p);
                    }
                }
                validAppointments.add(newAppointment);
            }

            for(Appointment a : validAppointments){
                getSchedule().getAppointments().add(a);
            }

            sortAppointmentList();
            return true;
        }else{
            switch (repeat){
                case EVERY_MONDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.MONDAY) && ed.getDayOfWeek().equals(DayOfWeek.MONDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.MONDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.MONDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_TUESDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.TUESDAY) && ed.getDayOfWeek().equals(DayOfWeek.TUESDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.TUESDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.TUESDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_WEDNESDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.WEDNESDAY) && ed.getDayOfWeek().equals(DayOfWeek.WEDNESDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.WEDNESDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.WEDNESDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_THURSDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.THURSDAY) && ed.getDayOfWeek().equals(DayOfWeek.THURSDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.THURSDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.THURSDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_FRIDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.FRIDAY) && ed.getDayOfWeek().equals(DayOfWeek.FRIDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.FRIDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.FRIDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_SATURDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.SATURDAY) && ed.getDayOfWeek().equals(DayOfWeek.SATURDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_SUNDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.SUNDAY) && ed.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.SUNDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.SUNDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_WEEK:
                    if(!sd.getDayOfWeek().equals(ed.getDayOfWeek())){
                        for(int i = 0; i <= diffDays; i++) {
                            if(ed.minusDays(i).getDayOfWeek().equals(sd.getDayOfWeek())){
                                ed = ed.minusDays(i);
                                break;
                            }
                        }
                    }
                    break;
            }
        }

        for(int i = 0; i <= diffDays; i+=7) {
            for (Appointment a : getSchedule().getAppointments()) {
                if(overlappingAppointments(a, startTime, endTime, sd.plusDays(i), place)){
                    return false;
                }
            }
        }

        Appointment newAppointment = new Appointment(startTime, endTime, sd, ed, getSchedule().getInfo().getDayFormat().get(sd.getDayOfWeek().getValue() - 1), additional);
        for (Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place)) {
                newAppointment.setPlace(p);
            }
        }
        validAppointments.add(newAppointment);

        for(Appointment a : validAppointments){
            getSchedule().getAppointments().add(a);
        }

        sortAppointmentList();
        return true;
    }

    @Override
    public boolean removeAppointment(String when, String place, String time) {
        LocalDate date = LocalDate.parse(when, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(date.isBefore(getSchedule().getStartDate()) || date.isAfter(getSchedule().getEndDate()))
            return false;

        String[] split = time.split("-");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);
        if(startTime.isBefore(getSchedule().getStartTime()) || endTime.isAfter(getSchedule().getEndTime()))
            return false;

        Appointment app = null;

        for(Appointment a : getSchedule().getAppointments()){
            if(a.equals(startTime, endTime, date, date, place)){
                app = a;
                break;
            }
        }

        if(app == null){
            return false;
        }

        // 02/10/2023 - 23/10/2023
        //ako brisu 02/10/2023 onda je 09/10/2023 - 23/10/2023
        //ako brisu 23/10/2023 onda je 02/10/2023 - 16/10/2023
        //ako brisu 09/10/2023 onda je 02/10/2023 - 02/10/2023 i 16/10/2023 - 23/10/2023

        if(date.equals(app.getStartDate()) && date.equals(app.getEndDate())){
            getSchedule().getAppointments().remove(app);
        }
        else if(date.equals(app.getStartDate()) && date.isBefore(app.getEndDate())){
            app.setStartDate(date.plusDays(7));
        }
        else if(date.isAfter(app.getStartDate()) && date.equals(app.getEndDate())){
            app.setEndDate(date.minusDays(7));
        }
        else if(date.isAfter(app.getStartDate()) && date.isBefore(app.getEndDate())){
//            int appDay = app.getStartDate().getDayOfWeek().getValue();
//            int dateDay = date.getDayOfWeek().getValue();
//            LocalDate newStartdate = date;
//            LocalDate newEndDate = date;
//
//            newStartdate = newStartdate.plusDays((7-dateDay+appDay)%7);
//            newEndDate = newEndDate.minusDays((7+dateDay-appDay)%7);

            Appointment newappointment = new Appointment(app.getStartTime(),app.getEndTime(),app.getStartDate(),date.minusDays(7),app.getDay(),app.getAdditional(), app.getPlace());

            app.setStartDate(date.plusDays(7));

            getSchedule().getAppointments().add(newappointment);
        }

        sortAppointmentList();
        return true;
    }
    @Override
    public boolean removeAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return false;

        String[] split = time.split("-");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);
        if(startTime.isBefore(getSchedule().getStartTime()) || endTime.isAfter(getSchedule().getEndTime()))
            return false;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        if(repeat.equals(AppointmentRepeat.EVERY_DAY)){
            List<Appointment> validAppointments = new ArrayList<>();
            int counter = (int) diffDays;

            for(int i = 0; i <= diffDays; i++) {
                if((getSchedule().getNonWorkingDates().contains(sd.plusDays(i)) && getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)))){
                    counter--;
                    continue;
                }

                for (Appointment a : getSchedule().getAppointments()) {
                    if(a.equals(startTime, endTime, sd.plusDays(i), sd.plusDays(i), place)){
                        validAppointments.add(a);
                    }
                }
            }

            if(validAppointments.size() != counter){
                return false;
            }

            for(Appointment a : validAppointments){
                getSchedule().getAppointments().remove(a);
            }

            sortAppointmentList();
            return true;
        }else{
            switch (repeat){
                case EVERY_MONDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.MONDAY) && ed.getDayOfWeek().equals(DayOfWeek.MONDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.MONDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.MONDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_TUESDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.TUESDAY) && ed.getDayOfWeek().equals(DayOfWeek.TUESDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.TUESDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.TUESDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_WEDNESDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.WEDNESDAY) && ed.getDayOfWeek().equals(DayOfWeek.WEDNESDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.WEDNESDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.WEDNESDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_THURSDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.THURSDAY) && ed.getDayOfWeek().equals(DayOfWeek.THURSDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.THURSDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.THURSDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_FRIDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.FRIDAY) && ed.getDayOfWeek().equals(DayOfWeek.FRIDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.FRIDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.FRIDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_SATURDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.SATURDAY) && ed.getDayOfWeek().equals(DayOfWeek.SATURDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_SUNDAY:
                    for(int i = 0; i <= diffDays; i++) {
                        if(sd.getDayOfWeek().equals(DayOfWeek.SUNDAY) && ed.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
                            break;
                        }
                        if(sd.plusDays(i).getDayOfWeek().equals(DayOfWeek.SUNDAY)){
                            sd = sd.plusDays(i);
                        }
                        if(ed.minusDays(i).getDayOfWeek().equals(DayOfWeek.SUNDAY)){
                            ed = ed.minusDays(i);
                        }
                    }
                    break;
                case EVERY_WEEK:
                    if(!sd.getDayOfWeek().equals(ed.getDayOfWeek())){
                        for(int i = 0; i <= diffDays; i++) {
                            if(ed.minusDays(i).getDayOfWeek().equals(sd.getDayOfWeek())){
                                ed = ed.minusDays(i);
                                break;
                            }
                        }
                    }
                    break;
            }
        }

        Appointment app = null;

        // 02/10/2023 - 23/10/2023
        // 02/10/2023 - 23/10/2023 ceo interval
        // 02/10/2023 - 09/10/2023 onda ide od 16/10/2023 - 23/10/2023 (menja se startDate)
        // 16/10/2023 - 23/10/2023 onda ide od 02/10/2023 - 09/10/2023 (menja se endDate)
        // 09/10/2023 - 16/10/2023 onda imamo dva app 02/10/2023 - 02/10/2023 i 23/10/2023 - 23/10/2023

        for(Appointment a : getSchedule().getAppointments()){
            if(a.equals(startTime, endTime, sd, ed, place)){
                app = a;
                break;
            }
        }

        if(app == null){
            return false;
        }

        if(sd.equals(app.getStartDate()) && ed.equals(app.getEndDate())){
            getSchedule().getAppointments().remove(app);
        }
        else if(sd.equals(app.getStartDate()) && ed.isBefore(app.getEndDate())){
            app.setStartDate(ed.plusDays(7));
        }
        else if(sd.isAfter(app.getStartDate()) && ed.equals(app.getEndDate())){
            app.setEndDate(sd.minusDays(7));
        }
        else if(sd.isAfter(app.getStartDate()) && ed.isBefore(app.getEndDate())){
            Appointment newappointment = new Appointment(app.getStartTime(),app.getEndTime(),app.getStartDate(),sd.minusDays(7),app.getDay(),app.getAdditional(), app.getPlace());

            app.setStartDate(ed.plusDays(7));

            getSchedule().getAppointments().add(newappointment);
        }

        sortAppointmentList();

        return true;
    }

    @Override
    public Appointment find(String when, String place, String time) {
        String[] dates = when.split("-");
        LocalDate startDate = LocalDate.parse(dates[0], DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate endDate = LocalDate.parse(dates[1], DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        String[] split = time.split("-");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);

        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
            if(a.equals(startTime, endTime, startDate, endDate, place)){
                foundAppointment = a;
                break;
            }
        }

        if(foundAppointment == null){
            return null;
        }

        return new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),startDate,endDate,foundAppointment.getDay(),foundAppointment.getAdditional(), foundAppointment.getPlace());
    }

    @Override
    public boolean updateAppointment(Appointment appointment, String date) {
        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
//            if( (appointment.getStartDate().isBefore(a.getEndDate()) || appointment.getStartDate().equals(a.getEndDate()) ) && ( appointment.getEndDate().isAfter(a.getStartDate()) || appointment.getEndDate().equals(a.getStartDate()) ) ) {
//                if( a.getPlace().getName().equals(appointment.getPlace().getName())  ) {
//                    if( appointment.getStartTime().isBefore(a.getEndTime()) && appointment.getEndTime().isAfter(a.getStartTime()) ) {
//                        if(appointment.getDay().equals(a.getDay())) {
//                            foundAppointment = a;
//                            break;
//                        }
//                    }
//                }
//            }
            if(a.equals(appointment.getStartTime(), appointment.getEndTime(), appointment.getStartDate(), appointment.getEndDate(), appointment.getPlace().getName())){
                foundAppointment = a;
                break;
            }
        }

        if(foundAppointment == null){
            return false;
        }

        String time = appointment.getStartTime() + "-" + appointment.getEndTime();
        String[] dates = date.split("-");

        if(addAppointment(dates[0], dates[1], time, appointment.getPlace().getName(), AppointmentRepeat.EVERY_WEEK, appointment.getAdditional())){
            int appDay = foundAppointment.getStartDate().getDayOfWeek().getValue();
            int sdDay = appointment.getStartDate().getDayOfWeek().getValue();
            int edDay = appointment.getEndDate().getDayOfWeek().getValue();

            LocalDate newStartdate = appointment.getEndDate();
            LocalDate newEndDate = appointment.getStartDate();

            newStartdate = newStartdate.plusDays((7-edDay+appDay)%7);
            newEndDate = newEndDate.minusDays((7+sdDay-appDay)%7);


            if(appointment.getStartDate().equals(foundAppointment.getStartDate()) && appointment.getEndDate().equals(foundAppointment.getEndDate())){
                getSchedule().getAppointments().remove(foundAppointment);
            }
            else if(appointment.getStartDate().equals(foundAppointment.getStartDate()) && appointment.getEndDate().isBefore(foundAppointment.getEndDate()) ){
                foundAppointment.setStartDate(newStartdate);
            }
            else if(appointment.getStartDate().isAfter(foundAppointment.getStartDate()) && appointment.getEndDate().equals(foundAppointment.getEndDate()) ){
                foundAppointment.setEndDate(newEndDate);
            }
            else if(appointment.getStartDate().isAfter(foundAppointment.getStartDate()) && appointment.getEndDate().isBefore(foundAppointment.getEndDate()) ){
                Appointment newappointment = new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),foundAppointment.getStartDate(),newEndDate.minusDays(7),foundAppointment.getDay(),foundAppointment.getAdditional());
                newappointment.setPlace(foundAppointment.getPlace());

                foundAppointment.setStartDate(newStartdate.plusDays(7));

                getSchedule().getAppointments().add(newappointment);
            }
            sortAppointmentList();
        }
        return true;
    }
    @Override
    public boolean updateAppointment(Appointment appointment, Places place) {
        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
            if(a.equals(appointment.getStartTime(), appointment.getEndTime(), appointment.getStartDate(), appointment.getEndDate(), appointment.getPlace().getName())){
                foundAppointment = a;
                break;
            }
        }

        if(foundAppointment == null){
            return false;
        }

        String time = appointment.getStartTime() + "-" + appointment.getEndTime();

        String startDate = appointment.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        String endDate = appointment.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        if(addAppointment(startDate, endDate, time, place.getName(), AppointmentRepeat.EVERY_WEEK, appointment.getAdditional())){
            int appDay = foundAppointment.getStartDate().getDayOfWeek().getValue();
            int sdDay = appointment.getStartDate().getDayOfWeek().getValue();
            int edDay = appointment.getEndDate().getDayOfWeek().getValue();

            LocalDate newStartdate = appointment.getEndDate();
            LocalDate newEndDate = appointment.getStartDate();

            newStartdate = newStartdate.plusDays((7-edDay+appDay)%7);
            newEndDate = newEndDate.minusDays((7+sdDay-appDay)%7);

            if(appointment.getStartDate().equals(foundAppointment.getStartDate()) && appointment.getEndDate().equals(foundAppointment.getEndDate())){
                getSchedule().getAppointments().remove(foundAppointment);
            }
            else if(appointment.getStartDate().equals(foundAppointment.getStartDate()) && appointment.getEndDate().isBefore(foundAppointment.getEndDate()) ){
                foundAppointment.setStartDate(newStartdate);
            }
            else if(appointment.getStartDate().isAfter(foundAppointment.getStartDate()) && appointment.getEndDate().equals(foundAppointment.getEndDate()) ){
                foundAppointment.setEndDate(newEndDate);
            }
            else if(appointment.getStartDate().isAfter(foundAppointment.getStartDate()) && appointment.getEndDate().isBefore(foundAppointment.getEndDate()) ){
                Appointment newappointment = new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),foundAppointment.getStartDate(),newEndDate.minusDays(7),foundAppointment.getDay(),foundAppointment.getAdditional());
                newappointment.setPlace(foundAppointment.getPlace());

                foundAppointment.setStartDate(newStartdate.plusDays(7));

                getSchedule().getAppointments().add(newappointment);
            }
            sortAppointmentList();
        }
        return true;
    }
    @Override
    public boolean updateAppointment(Appointment appointment, String startTime, String endTime) {
        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
            if(a.equals(appointment.getStartTime(), appointment.getEndTime(), appointment.getStartDate(), appointment.getEndDate(), appointment.getPlace().getName())){
                foundAppointment = a;
                break;
            }
        }

        if(foundAppointment == null){
            return false;
        }

        String time = startTime + "-" + endTime;

        String startDate = appointment.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        String endDate = appointment.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        if(addAppointment(startDate,endDate,time,appointment.getPlace().getName(),AppointmentRepeat.EVERY_WEEK,appointment.getAdditional())){
            int appDay = foundAppointment.getStartDate().getDayOfWeek().getValue();
            int sdDay = appointment.getStartDate().getDayOfWeek().getValue();
            int edDay = appointment.getEndDate().getDayOfWeek().getValue();

            LocalDate newStartdate = appointment.getEndDate();
            LocalDate newEndDate = appointment.getStartDate();

            newStartdate = newStartdate.plusDays((7-edDay+appDay)%7);
            newEndDate = newEndDate.minusDays((7+sdDay-appDay)%7);

            if(appointment.getStartDate().equals(foundAppointment.getStartDate()) && appointment.getEndDate().equals(foundAppointment.getEndDate())){
                getSchedule().getAppointments().remove(foundAppointment);
            }
            else if(appointment.getStartDate().equals(foundAppointment.getStartDate()) && appointment.getEndDate().isBefore(foundAppointment.getEndDate()) ){
                foundAppointment.setStartDate(newStartdate);
            }
            else if(appointment.getStartDate().isAfter(foundAppointment.getStartDate()) && appointment.getEndDate().equals(foundAppointment.getEndDate()) ){
                foundAppointment.setEndDate(newEndDate);
            }
            else if(appointment.getStartDate().isAfter(foundAppointment.getStartDate()) && appointment.getEndDate().isBefore(foundAppointment.getEndDate()) ){
                Appointment newappointment = new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),foundAppointment.getStartDate(),newEndDate.minusDays(7),foundAppointment.getDay(),foundAppointment.getAdditional());
                newappointment.setPlace(foundAppointment.getPlace());

                foundAppointment.setStartDate(newStartdate.plusDays(7));

                getSchedule().getAppointments().add(newappointment);
            }
            sortAppointmentList();
        }
        return true;
    }
    @Override
    public boolean updateAppointment(Appointment appointment, Map<String, String> additional) {
        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
            if(a.equals(appointment.getStartTime(), appointment.getEndTime(), appointment.getStartDate(), appointment.getEndDate(), appointment.getPlace().getName())){
                foundAppointment = a;
                break;
            }
        }

        if(foundAppointment == null){
            return false;
        }

        String time = appointment.getStartTime() + "-" + appointment.getEndTime();

        String startDate = appointment.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        String endDate = appointment.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        int appDay = foundAppointment.getStartDate().getDayOfWeek().getValue();
        int sdDay = appointment.getStartDate().getDayOfWeek().getValue();
        int edDay = appointment.getEndDate().getDayOfWeek().getValue();

        LocalDate newStartdate = appointment.getEndDate().plusDays(7);
        LocalDate newEndDate = appointment.getStartDate().minusDays(7);

        newStartdate = newStartdate.plusDays((7-edDay+appDay)%7);
        newEndDate = newEndDate.minusDays((7+sdDay-appDay)%7);

        if(appointment.getStartDate().equals(foundAppointment.getStartDate()) && appointment.getEndDate().equals(foundAppointment.getEndDate())){
            getSchedule().getAppointments().remove(foundAppointment);
        }
        else if(appointment.getStartDate().equals(foundAppointment.getStartDate()) && appointment.getEndDate().isBefore(foundAppointment.getEndDate()) ){
            foundAppointment.setStartDate(newStartdate);
        }
        else if(appointment.getStartDate().isAfter(foundAppointment.getStartDate()) && appointment.getEndDate().equals(foundAppointment.getEndDate()) ){
            foundAppointment.setEndDate(newEndDate);
        }
        else if(appointment.getStartDate().isAfter(foundAppointment.getStartDate()) && appointment.getEndDate().isBefore(foundAppointment.getEndDate()) ){
            Appointment newappointment = new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),foundAppointment.getStartDate(),newEndDate.minusDays(7),foundAppointment.getDay(),foundAppointment.getAdditional());
            newappointment.setPlace(foundAppointment.getPlace());

            foundAppointment.setStartDate(newStartdate.plusDays(7));

            getSchedule().getAppointments().add(newappointment);
        }
        sortAppointmentList();
        addAppointment(startDate,endDate,time,appointment.getPlace().getName(),AppointmentRepeat.EVERY_WEEK,additional);

        return true;
    }
    @Override
    public boolean updateAppointment(Appointment appointment, String date, String startTime, String endTime) {
        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
            if(a.equals(appointment.getStartTime(), appointment.getEndTime(), appointment.getStartDate(), appointment.getEndDate(), appointment.getPlace().getName())){
                foundAppointment = a;
                break;
            }
        }

        if(foundAppointment == null){
            return false;
        }

        String time = startTime + "-" + endTime;
        String[] dates = date.split("-");

        if(addAppointment(dates[0],dates[1],time,appointment.getPlace().getName(),AppointmentRepeat.EVERY_WEEK,appointment.getAdditional())){
            int appDay = foundAppointment.getStartDate().getDayOfWeek().getValue();
            int sdDay = appointment.getStartDate().getDayOfWeek().getValue();
            int edDay = appointment.getEndDate().getDayOfWeek().getValue();

            LocalDate newStartdate = appointment.getEndDate();
            LocalDate newEndDate = appointment.getStartDate();

            newStartdate = newStartdate.plusDays((7-edDay+appDay)%7);
            newEndDate = newEndDate.minusDays((7+sdDay-appDay)%7);

            if(appointment.getStartDate().equals(foundAppointment.getStartDate()) && appointment.getEndDate().equals(foundAppointment.getEndDate())){
                getSchedule().getAppointments().remove(foundAppointment);
            }
            else if(appointment.getStartDate().equals(foundAppointment.getStartDate()) && appointment.getEndDate().isBefore(foundAppointment.getEndDate()) ){
                foundAppointment.setStartDate(newStartdate);
            }
            else if(appointment.getStartDate().isAfter(foundAppointment.getStartDate()) && appointment.getEndDate().equals(foundAppointment.getEndDate()) ){
                foundAppointment.setEndDate(newEndDate);
            }
            else if(appointment.getStartDate().isAfter(foundAppointment.getStartDate()) && appointment.getEndDate().isBefore(foundAppointment.getEndDate()) ){
                Appointment newappointment = new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),foundAppointment.getStartDate(),newEndDate.minusDays(7),foundAppointment.getDay(),foundAppointment.getAdditional());
                newappointment.setPlace(foundAppointment.getPlace());

                foundAppointment.setStartDate(newStartdate.plusDays(7));

                getSchedule().getAppointments().add(newappointment);
            }
            sortAppointmentList();
        }
        return true;
    }
    @Override
    public boolean updateAppointment(Appointment appointment, String date, String startTime, String endTime, Places place) {
        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
            if(a.equals(appointment.getStartTime(), appointment.getEndTime(), appointment.getStartDate(), appointment.getEndDate(), appointment.getPlace().getName())){
                foundAppointment = a;
                break;
            }
        }

        if(foundAppointment == null){
            return false;
        }

        String time = startTime + "-" + endTime;
        String[] dates = date.split("-");

        if(addAppointment(dates[0],dates[1],time,place.getName(),AppointmentRepeat.EVERY_WEEK,appointment.getAdditional())){
            int appDay = foundAppointment.getStartDate().getDayOfWeek().getValue();
            int sdDay = appointment.getStartDate().getDayOfWeek().getValue();
            int edDay = appointment.getEndDate().getDayOfWeek().getValue();

            LocalDate newStartdate = appointment.getEndDate();
            LocalDate newEndDate = appointment.getStartDate();

            newStartdate = newStartdate.plusDays((7-edDay+appDay)%7);
            newEndDate = newEndDate.minusDays((7+sdDay-appDay)%7);

            if(appointment.getStartDate().equals(foundAppointment.getStartDate()) && appointment.getEndDate().equals(foundAppointment.getEndDate())){
                getSchedule().getAppointments().remove(foundAppointment);
            }
            else if(appointment.getStartDate().equals(foundAppointment.getStartDate()) && appointment.getEndDate().isBefore(foundAppointment.getEndDate()) ){
                foundAppointment.setStartDate(newStartdate);
            }
            else if(appointment.getStartDate().isAfter(foundAppointment.getStartDate()) && appointment.getEndDate().equals(foundAppointment.getEndDate()) ){
                foundAppointment.setEndDate(newEndDate);
            }
            else if(appointment.getStartDate().isAfter(foundAppointment.getStartDate()) && appointment.getEndDate().isBefore(foundAppointment.getEndDate()) ){
                Appointment newappointment = new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),foundAppointment.getStartDate(),newEndDate.minusDays(7),foundAppointment.getDay(),foundAppointment.getAdditional());
                newappointment.setPlace(foundAppointment.getPlace());

                foundAppointment.setStartDate(newStartdate.plusDays(7));

                getSchedule().getAppointments().add(newappointment);
            }
            sortAppointmentList();
        }

        return true;
    }

    @Override
    public List<Appointment> search(String startDate, String endDate) {
        List<Appointment> results = new ArrayList<>();

        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        // 02/10/2023 - 30/10/2023

        // 02/10/2023 - 30/10/2023 jednaki
        // 03/10/2023 - 30/10/2023 start unutar kraj jednak start i endDate
        // 02/10/2023 - 29/10/2023 pocetak jednak kraj unutar startDate i end

        // 02/10/2023 - 31/10/2023 start jednak kraj spolja startDate i endDate
        // 01/10/2023 - 30/10/2023 start spolja kraj jednak startDate i endDate

        // 01/10/2023 - 31/10/2023 oba spolja onda pisemo startDate endDate

        // 01/10/2023 - 29/10/2023 start spolja end unutra onda startDate i end
        // 03/10/2023 - 31/10/2023 start unutra end spolja onda start i endDate

        // 03/10/2023 - 29/10/2023 oba unutra onda start i end

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isBefore(sd) && a.getEndDate().isAfter(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().equals(ed))
            || (a.getStartDate().isBefore(sd) && a.getEndDate().equals(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().isAfter(ed))){
                results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, ed, a.getDay(), a.getAdditional(), a.getPlace()));
            }
            else if((a.getStartDate().isBefore(sd) || a.getStartDate().equals(sd)) && a.getEndDate().isBefore(ed)){
                results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, a.getEndDate(), a.getDay(), a.getAdditional(), a.getPlace()));
            }
            else if(a.getStartDate().isAfter(sd) && (a.getEndDate().isAfter(ed) || a.getEndDate().isEqual(ed))){
                results.add(new Appointment(a.getStartTime(), a.getEndTime(), a.getStartDate(), ed, a.getDay(), a.getAdditional(), a.getPlace()));
            }
            else if(a.getStartDate().isAfter(sd) && a.getEndDate().isBefore(ed)){
                results.add(a);
            }
        }

        return results;
    }
    @Override
    public List<Appointment> search(String startDate, String endDate, Map<String, String> additional) {
        List<Appointment> results = new ArrayList<>();

        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            int flag = 0;
            if((a.getStartDate().isBefore(sd) && a.getEndDate().isAfter(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().equals(ed))
                    || (a.getStartDate().isBefore(sd) && a.getEndDate().equals(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().isAfter(ed))){
                flag = 1;
            }
            else if((a.getStartDate().isBefore(sd) || a.getStartDate().equals(sd)) && a.getEndDate().isBefore(ed)){
                flag = 2;
            }
            else if(a.getStartDate().isAfter(sd) && (a.getEndDate().isAfter(ed) || a.getEndDate().isEqual(ed))){
                flag = 3;
            }
            else if(a.getStartDate().isAfter(sd) && a.getEndDate().isBefore(ed)){
                flag = 4;
            }

            if(flag > 0){
                int flagAdditional = additional.size();
                for(Map.Entry<String,String> entry : additional.entrySet()) {
                    if (a.getAdditional().containsValue(entry.getValue())) {
                        flagAdditional--;
                    }
                }
                if(flagAdditional == 0){
                    switch (flag){
                        case 1:
                            results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, ed, a.getDay(), a.getAdditional(), a.getPlace()));
                            break;
                        case 2:
                            results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, a.getEndDate(), a.getDay(), a.getAdditional(), a.getPlace()));
                            break;
                        case 3:
                            results.add(new Appointment(a.getStartTime(), a.getEndTime(), a.getStartDate(), ed, a.getDay(), a.getAdditional(), a.getPlace()));
                            break;
                        case 4:
                            results.add(a);
                            break;
                    }
                }
            }

        }

        return results;
    }
    @Override
    public List<Appointment> search(String startDate, String endDate, Places place) {
        List<Appointment> results = new ArrayList<>();

        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if(a.getPlace().getName().equals(place.getName())) {
                if ((a.getStartDate().isBefore(sd) && a.getEndDate().isAfter(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().equals(ed))
                        || (a.getStartDate().isBefore(sd) && a.getEndDate().equals(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().isAfter(ed))) {
                    results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, ed, a.getDay(), a.getAdditional(), a.getPlace()));
                } else if ((a.getStartDate().isBefore(sd) || a.getStartDate().equals(sd)) && a.getEndDate().isBefore(ed)) {
                    results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, a.getEndDate(), a.getDay(), a.getAdditional(), a.getPlace()));
                } else if (a.getStartDate().isAfter(sd) && (a.getEndDate().isAfter(ed) || a.getEndDate().isEqual(ed))) {
                    results.add(new Appointment(a.getStartTime(), a.getEndTime(), a.getStartDate(), ed, a.getDay(), a.getAdditional(), a.getPlace()));
                } else if (a.getStartDate().isAfter(sd) && a.getEndDate().isBefore(ed)) {
                    results.add(a);
                }
            }
        }

        return results;
    }
    @Override
    public List<Appointment> search(String startDate, String endDate, Places place, Map<String, String> additional) {
        List<Appointment> results = new ArrayList<>();

        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if(a.getPlace().getName().equals(place.getName())) {
                int flag = 0;
                if ((a.getStartDate().isBefore(sd) && a.getEndDate().isAfter(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().equals(ed))
                        || (a.getStartDate().isBefore(sd) && a.getEndDate().equals(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().isAfter(ed))) {
                    flag = 1;
                } else if ((a.getStartDate().isBefore(sd) || a.getStartDate().equals(sd)) && a.getEndDate().isBefore(ed)) {
                    flag = 2;
                } else if (a.getStartDate().isAfter(sd) && (a.getEndDate().isAfter(ed) || a.getEndDate().isEqual(ed))) {
                    flag = 3;
                } else if (a.getStartDate().isAfter(sd) && a.getEndDate().isBefore(ed)) {
                    flag = 4;
                }

                if (flag > 0) {
                    int flagAdditional = additional.size();
                    for (Map.Entry<String, String> entry : additional.entrySet()) {
                        if (a.getAdditional().containsValue(entry.getValue())) {
                            flagAdditional--;
                        }
                    }
                    if (flagAdditional == 0) {
                        switch (flag) {
                            case 1:
                                results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, ed, a.getDay(), a.getAdditional(), a.getPlace()));
                                break;
                            case 2:
                                results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, a.getEndDate(), a.getDay(), a.getAdditional(), a.getPlace()));
                                break;
                            case 3:
                                results.add(new Appointment(a.getStartTime(), a.getEndTime(), a.getStartDate(), ed, a.getDay(), a.getAdditional(), a.getPlace()));
                                break;
                            case 4:
                                results.add(a);
                                break;
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

        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if(a.getPlace().getName().equals(place.getName()) && a.getDay().equals(day)) {
                if ((a.getStartDate().isBefore(sd) && a.getEndDate().isAfter(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().equals(ed))
                        || (a.getStartDate().isBefore(sd) && a.getEndDate().equals(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().isAfter(ed))) {
                    results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, ed, a.getDay(), a.getAdditional(), a.getPlace()));
                } else if ((a.getStartDate().isBefore(sd) || a.getStartDate().equals(sd)) && a.getEndDate().isBefore(ed)) {
                    results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, a.getEndDate(), a.getDay(), a.getAdditional(), a.getPlace()));
                } else if (a.getStartDate().isAfter(sd) && (a.getEndDate().isAfter(ed) || a.getEndDate().isEqual(ed))) {
                    results.add(new Appointment(a.getStartTime(), a.getEndTime(), a.getStartDate(), ed, a.getDay(), a.getAdditional(), a.getPlace()));
                } else if (a.getStartDate().isAfter(sd) && a.getEndDate().isBefore(ed)) {
                    results.add(a);
                }
            }
        }

        return results;
    }
    @Override
    public List<Appointment> search(String day, String startDate, String endDate, Map<String, String> additional) {
        List<Appointment> results = new ArrayList<>();

        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if(a.getDay().equals(day)) {
                int flag = 0;
                if ((a.getStartDate().isBefore(sd) && a.getEndDate().isAfter(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().equals(ed))
                        || (a.getStartDate().isBefore(sd) && a.getEndDate().equals(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().isAfter(ed))) {
                    flag = 1;
                } else if ((a.getStartDate().isBefore(sd) || a.getStartDate().equals(sd)) && a.getEndDate().isBefore(ed)) {
                    flag = 2;
                } else if (a.getStartDate().isAfter(sd) && (a.getEndDate().isAfter(ed) || a.getEndDate().isEqual(ed))) {
                    flag = 3;
                } else if (a.getStartDate().isAfter(sd) && a.getEndDate().isBefore(ed)) {
                    flag = 4;
                }

                if (flag > 0) {
                    int flagAdditional = additional.size();
                    for (Map.Entry<String, String> entry : additional.entrySet()) {
                        if (a.getAdditional().containsValue(entry.getValue())) {
                            flagAdditional--;
                        }
                    }
                    if (flagAdditional == 0) {
                        switch (flag) {
                            case 1:
                                results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, ed, a.getDay(), a.getAdditional(), a.getPlace()));
                                break;
                            case 2:
                                results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, a.getEndDate(), a.getDay(), a.getAdditional(), a.getPlace()));
                                break;
                            case 3:
                                results.add(new Appointment(a.getStartTime(), a.getEndTime(), a.getStartDate(), ed, a.getDay(), a.getAdditional(), a.getPlace()));
                                break;
                            case 4:
                                results.add(a);
                                break;
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

        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if(a.getPlace().getName().equals(place.getName()) && a.getDay().equals(day)) {
                int flag = 0;
                if ((a.getStartDate().isBefore(sd) && a.getEndDate().isAfter(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().equals(ed))
                        || (a.getStartDate().isBefore(sd) && a.getEndDate().equals(ed)) || (a.getStartDate().equals(sd) && a.getEndDate().isAfter(ed))) {
                    flag = 1;
                } else if ((a.getStartDate().isBefore(sd) || a.getStartDate().equals(sd)) && a.getEndDate().isBefore(ed)) {
                    flag = 2;
                } else if (a.getStartDate().isAfter(sd) && (a.getEndDate().isAfter(ed) || a.getEndDate().isEqual(ed))) {
                    flag = 3;
                } else if (a.getStartDate().isAfter(sd) && a.getEndDate().isBefore(ed)) {
                    flag = 4;
                }

                if (flag > 0) {
                    int flagAdditional = additional.size();
                    for (Map.Entry<String, String> entry : additional.entrySet()) {
                        if (a.getAdditional().containsValue(entry.getValue())) {
                            flagAdditional--;
                        }
                    }
                    if (flagAdditional == 0) {
                        switch (flag) {
                            case 1:
                                results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, ed, a.getDay(), a.getAdditional(), a.getPlace()));
                                break;
                            case 2:
                                results.add(new Appointment(a.getStartTime(), a.getEndTime(), sd, a.getEndDate(), a.getDay(), a.getAdditional(), a.getPlace()));
                                break;
                            case 3:
                                results.add(new Appointment(a.getStartTime(), a.getEndTime(), a.getStartDate(), ed, a.getDay(), a.getAdditional(), a.getPlace()));
                                break;
                            case 4:
                                results.add(a);
                                break;
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

        return check(diffDays, sd, ed, getSchedule().getStartTime(), getSchedule().getEndTime(), getSchedule().getPlaces(), null);
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

        return check(diffDays, sd, ed, getSchedule().getStartTime(), getSchedule().getEndTime(), validPlaces, null);
    }
    @Override
    public List<String> check(String startDate, String endDate, String day) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        return check(diffDays, sd, ed, getSchedule().getStartTime(), getSchedule().getEndTime(), getSchedule().getPlaces(), day);
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

        for(Places p : getSchedule().getPlaces()) {
            for (Map.Entry<String, String> entry : p.getAdditional().entrySet()) {
                if (additional.containsValue(entry.getValue())) {
                    validPlaces.add(p);
                }
            }
        }

        return check(diffDays, sd, ed, getSchedule().getStartTime(), getSchedule().getEndTime(), validPlaces, day);
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

        return check(diffDays, sd, ed, getSchedule().getStartTime(), getSchedule().getEndTime(), validPlaces, null);
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

        return check(diffDays, sd, ed, getSchedule().getStartTime(), getSchedule().getEndTime(), validPlaces, day);
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

        return check(diffDays, sd, ed, st, et, getSchedule().getPlaces(), null);
    }
    @Override
    public List<String> check(String startTime, String endTime, String startDate, String endDate, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        List<Places> validPlaces = new ArrayList<>();

        for(Places p : getSchedule().getPlaces()) {
            for (Map.Entry<String, String> entry : p.getAdditional().entrySet()) {
                if (additional.containsValue(entry.getValue())) {
                    validPlaces.add(p);
                }
            }
        }

        return check(diffDays, sd, ed, st, et, validPlaces, null);
    }
    @Override
    public List<String> check(String startTime, String endTime, String startDate, String endDate, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        List<Places> validPlaces = new ArrayList<>();

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
            }
        }

        return check(diffDays, sd, ed, st, et, validPlaces, null);
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

        return check(diffDays, sd, ed, st, et, getSchedule().getPlaces(), day);
    }
    @Override
    public List<String> check(String startTime, String endTime, String day, String startDate, String endDate, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        List<Places> validPlaces = new ArrayList<>();

        for(Places p : getSchedule().getPlaces()) {
            for (Map.Entry<String, String> entry : p.getAdditional().entrySet()) {
                if (additional.containsValue(entry.getValue())) {
                    validPlaces.add(p);
                }
            }
        }

        return check(diffDays, sd, ed, st, et, validPlaces, day);
    }
    @Override
    public List<String> check(String startTime, String endTime, String day, String startDate, String endDate, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return null;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        List<Places> validPlaces = new ArrayList<>();

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
            }
        }

        return check(diffDays, sd, ed, st, et, validPlaces, day);
    }

    @Override
    public void printAppointments(List<Appointment> appointments) {
        for(Appointment a : appointments){
            String result = a.getPlace().getName() + ", " + a.getDay() + " " + a.getStartTime() + "-" + a.getEndTime() + " ";
            result += a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + "-";
            result += a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
            System.out.println(result);
        }
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

        getSchedule().getAppointments().sort(new Comparator<Appointment>() {
            final List<String> abc = of("a", "b", "c", "d", "e", "f", "g");

            @Override
            public int compare(Appointment o1, Appointment o2) {
                return abc.get(getSchedule().getInfo().getDayFormat().indexOf(o1.getDay())).compareTo(abc.get(getSchedule().getInfo().getDayFormat().indexOf(o2.getDay())));
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
    public boolean overlappingAppointments(Appointment a, LocalTime sTime, LocalTime eTime, LocalDate date, String place){
        return (a.getStartDate().equals(date) || a.getEndDate().equals(date) || checkAppointmentForDate(a, date)) && (a.getStartTime().equals(sTime) || a.getEndTime().equals(eTime) || (a.getStartTime().isBefore(sTime) && a.getEndTime().isAfter(sTime)) || (a.getStartTime().isBefore(eTime)
                && a.getEndTime().isAfter(eTime))) && a.getPlace().getName().equals(place);
    }

    private boolean checkAppointmentForDate(Appointment a, LocalDate date){
        Duration diff = Duration.between(a.getStartDate().atStartOfDay(), a.getEndDate().atStartOfDay());
        long diffDays = diff.toDays();

        for(int i = 0; i <= diffDays; i += 7){
            if(a.getStartDate().plusDays(i).isEqual(date)){
                return true;
            }
            else if(a.getStartDate().plusDays(i).isAfter(date)) {
                return false;
            }
        }

        return false;
    }

    private List<String> check(long diffDays, LocalDate sd, LocalDate ed, LocalTime startTime, LocalTime endTime, List<Places> places, String day){
        List<String> results = new ArrayList<>();
        List<String> validDays = new ArrayList<>();
        List<Appointment> appointments;

        if(day == null) {
            if(diffDays >= 7)
                validDays = getSchedule().getInfo().getDayFormat();
            else {
                int k = sd.getDayOfWeek().getValue() - 1;
                int i = 0;

                while(i < diffDays){
                    validDays.add(getSchedule().getInfo().getDayFormat().get(k % 6));
                    k++;
                    i++;
                }
            }
        } else {
            validDays = of(day);
        }

        for(String d : validDays){
            if(!getSchedule().getNonWorkingDaysOfTheWeek().contains(d)) {
                for (Places p : places) {
                    appointments = search(d, sd.format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())), ed.format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())), p);
                    if (!appointments.isEmpty()) {
                        StringBuilder result = new StringBuilder(p.getName() + ", " + d + " ");
                        LocalTime start = startTime;
                        int j = 0;

                        while (j < appointments.size()) {
                            if (!appointments.get(j).getStartTime().equals(start) && appointments.get(j).getStartTime().isAfter(start) && start.isBefore(endTime)) {
                                if(appointments.get(j).getStartTime().isBefore(endTime)){
                                    result.append(start).append("-").append(appointments.get(j).getStartTime()).append(" ");
                                }
                                else{
                                    result.append(start).append("-").append(endTime).append(" ");
                                }
                            }
                            start = appointments.get(j).getEndTime();

                            j++;
                        }
                        if (!start.equals(endTime) && start.isBefore(endTime))
                            result.append(start).append("-").append(endTime).append(" ");

                        result.append(sd.format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()))).append("-");
                        result.append(ed.format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));

                        results.add(result.toString());
                    } else {
                        String result = p.getName() + ", " + d + " " + startTime + "-" + endTime + " ";
                        result += sd.format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + "-";
                        result += ed.format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
                        results.add(result);
                    }
                }
            }
        }

        return results;
    }
}
