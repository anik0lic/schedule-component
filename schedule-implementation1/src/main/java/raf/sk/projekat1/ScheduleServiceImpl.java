package raf.sk.projekat1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import raf.sk.projekat1.model.*;
import raf.sk.projekat1.specification.ScheduleService;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import java.awt.*;
import java.io.*;
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
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

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
    @Override
    public void sortAppointmentList(){
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
    @Override
    public boolean overlappingAppointments(Appointment a, LocalTime sTime, LocalTime eTime, LocalDate date, String place){
        return a.getStartDate().equals(date) && (a.getStartTime().equals(sTime) || a.getEndTime().equals(eTime) || (a.getStartTime().isBefore(sTime) && a.getEndTime().isAfter(sTime)) || (a.getStartTime().isBefore(eTime)
                && a.getEndTime().isAfter(eTime))) && a.getPlace().getName().equals(place);
    }

//    @Override
//    public void exportPDF(String filepath, List<Appointment> appointments) throws IOException, DocumentException {
//        Document document = new Document();
//        PdfWriter.getInstance(document, new FileOutputStream(filepath));
//
//        document.open();
//        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);
//        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
//
//        PdfPTable table = new PdfPTable(getSchedule().getInfo().getHeaders().size());
//
//        for (int j = 0; j < getSchedule().getInfo().getHeaders().size(); j++) {
//            Phrase phrase = new Phrase(getSchedule().getInfo().getHeaders().get(j), headerFont);
//            PdfPCell cell = new PdfPCell(phrase);
//            cell.setBackgroundColor(new BaseColor(Color.lightGray.getRGB()));
//            table.addCell(cell);
//        }
//
//        for(Appointment a : appointments){
//            for(int i = 0; i < getSchedule().getInfo().getHeaders().size(); i++){
//                if(a.getAdditional().containsKey(getSchedule().getInfo().getHeaders().get(i))){
//                    table.addCell(new Phrase(a.getAdditional().get(getSchedule().getInfo().getHeaders().get(i)), bodyFont));
//                }
//                else{
//                    if(i == getSchedule().getInfo().getPlace() || getSchedule().getInfo().getHeaders().get(i).equals("Place")){
//                        table.addCell(new Phrase(a.getPlace().getName(), bodyFont));
//                    }
//                    else if(i == getSchedule().getInfo().getTime() || getSchedule().getInfo().getHeaders().get(i).equals("Time")){
//                        table.addCell(new Phrase(a.getStartTime() + "-" + a.getEndTime(), bodyFont));
//                    }
//                    else if(i == getSchedule().getInfo().getStartDate() || getSchedule().getInfo().getHeaders().get(i).equals("Start Date")){
//                        table.addCell(new Phrase(a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())), bodyFont));
//                    }
//                    else if(i == getSchedule().getInfo().getDay() || getSchedule().getInfo().getHeaders().get(i).equals("Day")){
//                        table.addCell(new Phrase(a.getDay(), bodyFont));
//                    }
//                    else if(i == getSchedule().getInfo().getEndDate() || getSchedule().getInfo().getHeaders().get(i).equals("End Date")){
//                        table.addCell(new Phrase(a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())), bodyFont));
//                    }
//                }
//            }
//        }
//
//        table.setWidthPercentage(100);
//
//        document.add(table);
//        document.close();
//    }

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
                            LocalTime start = startTime;

                            while (j < appointments.size()) {
                                Appointment a2 = appointments.get(j);
                                if (a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())) {
                                    if (!a2.getStartTime().equals(start)) {
                                        if(a2.getStartTime().isAfter(endTime)){
                                            result += " " + start + "-" + endTime;
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
                            }
                            result += " " + a.getPlace().getName();
                            results.add(result);
                        }
                        else{
                            String result = getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1);
                            result += ", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
                            result += " " + startTime + "-" + endTime;
                            result += " " + p.getName();
                            results.add(result);
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
                    }
                }
                i++;
            }
        }

        return results;
    }
}
