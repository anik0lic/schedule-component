package raf.sk.projekat1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import raf.sk.projekat1.model.*;
import raf.sk.projekat1.specification.ScheduleService;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduleServiceImpl extends ScheduleService {
    public ScheduleServiceImpl(Schedule schedule) {
        super(schedule);
    }

    public ScheduleServiceImpl(){}

    @Override
    public void exportCSV(String filepath) {

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
            if((a.getStartDate().equals(date) && (a.getStartTime().equals(startTime) || a.getEndTime().equals(endTime) || (a.getStartTime().isBefore(startTime) && a.getEndTime().isAfter(startTime)) || (a.getStartTime().isBefore(endTime)
                    && a.getEndTime().isAfter(endTime))) && a.getPlace().getName().equals(place))){
                System.out.println("greska");
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
        List<Appointment> validAppointments = new ArrayList<>();

        if(repeat.equals(AppointmentRepeat.EVERY_DAY)){
            for(int i = 0; i <= diffDays; i++) {
                for (Appointment a : getSchedule().getAppointments()) {
                    if((a.getStartDate().equals(start.plusDays(i)) && (a.getStartTime().equals(startTime) || a.getEndTime().equals(endTime) || (a.getStartTime().isBefore(startTime) && a.getEndTime().isAfter(startTime)) || (a.getStartTime().isBefore(endTime)
                            && a.getEndTime().isAfter(endTime))) && a.getPlace().getName().equals(place))){
                        System.out.println("greska");
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
        }else if(repeat.equals(AppointmentRepeat.EVERY_WEEK)){
            for(int i = 0; i <= diffDays; i+=7) {
                for (Appointment a : getSchedule().getAppointments()) {
                    if((a.getStartDate().equals(start.plusDays(i)) && (a.getStartTime().equals(startTime) || a.getEndTime().equals(endTime) || (a.getStartTime().isBefore(startTime) && a.getEndTime().isAfter(startTime)) || (a.getStartTime().isBefore(endTime)
                            && a.getEndTime().isAfter(endTime))) && a.getPlace().getName().equals(place))){
                        System.out.println("greska");
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
        }else{
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
            diff = Duration.between(start.atStartOfDay(), end.atStartOfDay());
            diffDays = diff.toDays();

            for(int i = 0; i <= diffDays; i+=7) {
                for (Appointment a : getSchedule().getAppointments()) {
                    if((a.getStartDate().equals(start.plusDays(i)) && (a.getStartTime().equals(startTime) || a.getEndTime().equals(endTime) || (a.getStartTime().isBefore(startTime) && a.getEndTime().isAfter(startTime)) || (a.getStartTime().isBefore(endTime)
                            && a.getEndTime().isAfter(endTime))) && a.getPlace().getName().equals(place))){
                        System.out.println("greska");
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
            if(a.getStartDate().equals(date) && a.getStartTime().equals(startTime) && a.getEndTime().equals(endTime) && a.getPlace().getName().equals(place)){
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
        List<Appointment> validAppointments = new ArrayList<>();
        int counter = 0;

        if(repeat.equals(AppointmentRepeat.EVERY_DAY)){
            int i = 0;
            int j = 0;
            while(i <= diffDays) {
                if (!(getSchedule().getNonWorkingDates().contains(start.plusDays(i)) || getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(start.plusDays(i).getDayOfWeek().getValue() - 1)))) {
                    while (j < getSchedule().getAppointments().size()) {
                        Appointment a = getSchedule().getAppointments().get(j);
                        if (a.getStartDate().equals(start.plusDays(i)) && a.getStartTime().equals(startTime) && a.getEndTime().equals(endTime) && a.getPlace().getName().equals(place)) {
                            validAppointments.add(a);
                            counter++;
                            i++;
                            j++;
                            break;
                        }
                        j++;
                    }
                }else{
                    counter++;
                    i++;
                }
            }

            if(!(counter == diffDays+1)) {
                System.out.println("greska");
                return false;
            }
        }else if(repeat.equals(AppointmentRepeat.EVERY_WEEK)){
            int i = 0;
            int j = 0;

            while(i <= diffDays) {
                if (!(getSchedule().getNonWorkingDates().contains(start.plusDays(i)) || getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(start.plusDays(i).getDayOfWeek().getValue() - 1)))) {
                    while (j < getSchedule().getAppointments().size()) {
                        Appointment a = getSchedule().getAppointments().get(j);
                        if (a.getStartDate().equals(start.plusDays(i)) && a.getStartTime().equals(startTime) && a.getEndTime().equals(endTime) && a.getPlace().getName().equals(place)) {
                            validAppointments.add(a);
                            counter++;
                            i+=7;
                            j++;
                            break;
                        }
                        j++;
                    }
                }else{
                    counter++;
                    i+=7;
                }
            }

            if(!(counter == diffDays/7+1)) {
                System.out.println("greska");
                return false;
            }
        }else{
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
            diff = Duration.between(start.atStartOfDay(), end.atStartOfDay());
            diffDays = diff.toDays();
            int i = 0;
            int j = 0;

            while(i <= diffDays) {
                if (!(getSchedule().getNonWorkingDates().contains(start.plusDays(i)) || getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(start.plusDays(i).getDayOfWeek().getValue() - 1)))) {
                    while (j < getSchedule().getAppointments().size()) {
                        Appointment a = getSchedule().getAppointments().get(j);
                        if (a.getStartDate().equals(start.plusDays(i)) && a.getStartTime().equals(startTime) && a.getEndTime().equals(endTime) && a.getPlace().getName().equals(place)) {
                            validAppointments.add(a);
                            counter++;
                            i+=7;
                            j++;
                            break;
                        }
                        j++;
                    }
                }else{
                    counter++;
                    i+=7;
                }
            }

            if(!(counter == diffDays/7+1)) {
                System.out.println("greska");
                return false;
            }
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
            if(a.getStartDate().equals(date) && a.getStartTime().equals(startTime) && a.getEndTime().equals(endTime) && a.getPlace().getName().equals(place)){
                return a;
            }
        }
        return null;
    }

    @Override
    public void updateAppointment(Appointment appointment, String when) {
        String time = appointment.getStartTime() + "-" + appointment.getEndTime();

        if(addAppointment(when, appointment.getPlace().getName(), time, appointment.getAdditional())){
            getSchedule().getAppointments().remove(appointment);
        }else{
            System.out.println("greska");
        }
    }
    @Override
    public void updateAppointment(Appointment appointment, Places place) {
        String time = appointment.getStartTime() + "-" + appointment.getEndTime();
        String date = appointment.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        if(addAppointment(date, place.getName(), time, appointment.getAdditional())){
            getSchedule().getAppointments().remove(appointment);
        }else{
            System.out.println("greska");
        }
    }
    @Override
    public void updateAppointment(Appointment appointment, String startTime, String endTime) {
        String time = startTime + "-" + endTime;
        String date = appointment.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        if(addAppointment(date, appointment.getPlace().getName(), time, appointment.getAdditional())){
            getSchedule().getAppointments().remove(appointment);
        }else{
            System.out.println("greska");
        }
    }
    @Override
    public void updateAppointment(Appointment appointment, Map<String, String> additional) {
        for (Map.Entry<String, String> entry : additional.entrySet()) {
            appointment.getAdditional().put(entry.getKey(), entry.getValue());
        }
    }
    @Override
    public void updateAppointment(Appointment appointment, String when, String startTime, String endTime) {
        String time = startTime + "-" + endTime;

        if(addAppointment(when, appointment.getPlace().getName(), time, appointment.getAdditional())){
            getSchedule().getAppointments().remove(appointment);
        }else{
            System.out.println("greska");
        }
    }
    @Override
    public void updateAppointment(Appointment appointment, String when, String startTime, String endTime, Places place) {
        String time = startTime + "-" + endTime;

        if(addAppointment(when, place.getName(), time, appointment.getAdditional())){
            getSchedule().getAppointments().remove(appointment);
        }else{
            System.out.println("greska");
        }
    }

    @Override
    public void search() {
        for(Appointment a : getSchedule().getAppointments()){
            System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue()-1));
            System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
            System.out.print(" " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName() + "\n");
        }
    }
    @Override
    public void search(Places place) {
        for(Appointment a : getSchedule().getAppointments()){
            if(a.getPlace().getName().equals(place.getName())) {
                System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
                System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                System.out.print(" " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName() + "\n");
            }
        }
    }
    @Override
    public void search(Map<String, String> additional) {
        for(Appointment a : getSchedule().getAppointments()){
            int flag = additional.size();
            for(Map.Entry<String,String> entry : additional.entrySet()){
                if(a.getAdditional().containsValue(entry.getValue())){
                    flag--;
                }
            }
            if(flag == 0){
                System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue()-1));
                System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                System.out.print(" " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName() + "\n");
            }
        }
    }
    @Override
    public void search(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
                System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                System.out.print(" " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName() + "\n");
            }
        }
    }
    @Override
    public void search(String startDate, String endDate, Map<String, String> additional) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                for(Map.Entry<String,String> entry : additional.entrySet()) {
                    if (a.getAdditional().containsValue(entry.getValue())) {
                        System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
                        System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                        System.out.print(" " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName() + "\n");
                    }
                    break;
                }
            }
        }
    }
    @Override
    public void search(String startDate, String endDate, Places place) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                if(a.getPlace().getName().equals(place.getName())) {
                    System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
                    System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                    System.out.print(" " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName() + "\n");
                }
            }
        }
    }
    @Override
    public void search(String startDate, String endDate, Places place, Map<String, String> additional) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                for(Map.Entry<String,String> entry : additional.entrySet()) {
                    if (a.getAdditional().containsValue(entry.getValue())) {
                        if(a.getPlace().getName().equals(place.getName())) {
                            System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
                            System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                            System.out.print(" " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName() + "\n");
                        }
                    }
                }
            }
        }
    }
    @Override
    public void search(String day, String startDate, String endDate, Places place) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                if(a.getPlace().getName().equals(place.getName())) {
                    if(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1).equals(day)) {
                        System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
                        System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                        System.out.print(" " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName() + "\n");
                    }
                }
            }
        }
    }
    @Override
    public void search(String day, String startDate, String endDate, Map<String, String> additional) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                for(Map.Entry<String,String> entry : additional.entrySet()) {
                    if (a.getAdditional().containsValue(entry.getValue())) {
                        if (getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1).equals(day)) {
                            System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
                            System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                            System.out.print(" " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName() + "\n");
                        }
                    }
                }
            }
        }
    }
    @Override
    public void search(String day, String startDate, String endDate, Places place, Map<String, String> additional) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(start) && a.getStartDate().isBefore(end)) || a.getStartDate().isEqual(start) || a.getStartDate().isEqual(end)) {
                for(Map.Entry<String,String> entry : additional.entrySet()) {
                    if (a.getAdditional().containsValue(entry.getValue())) {
                        if (getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1).equals(day)) {
                            if(a.getPlace().getName().equals(place.getName())) {
                                System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
                                System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                                System.out.print(" " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName() + "\n");
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void check(String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), getSchedule().getAppointments(), getSchedule().getPlaces(), null);
    }
    @Override
    public void check(String startDate, String endDate, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return;

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

        check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), validAppointments, validPlaces, null);
    }
    @Override
    public void check(String startDate, String endDate, String day) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), getSchedule().getAppointments(), getSchedule().getPlaces(), day);
    }
    @Override
    public void check(String startDate, String endDate, String day, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return;

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

        check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), validAppointments, validPlaces, day);
    }
    @Override
    public void check(String startDate, String endDate, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        List<Places> validPlaces = new ArrayList<>();
        List<Appointment> validAppointments = new ArrayList<>();

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
            }
        }

        for(Appointment a : getSchedule().getAppointments()){
            for(Places p : validPlaces){
                if(a.getPlace().getName().equals(p.getName())){
                    validAppointments.add(a);
                }
            }
        }

        check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), validAppointments, validPlaces, null);
    }
    @Override
    public void check(String startDate, String endDate, String day, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        List<Places> validPlaces = new ArrayList<>();
        List<Appointment> validAppointments = new ArrayList<>();

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
            }
        }

        for(Appointment a : getSchedule().getAppointments()){
            for(Places p : validPlaces){
                if(a.getPlace().getName().equals(p.getName())){
                    validAppointments.add(a);
                }
            }
        }

        check(diffDays, sd, getSchedule().getStartTime(), getSchedule().getEndTime(), validAppointments, validPlaces, day);
    }
    @Override
    public void check(String startTime, String endTime, String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        check(diffDays, sd, st, et, getSchedule().getAppointments(), getSchedule().getPlaces(), null);
    }
    @Override
    public void check(String startTime, String endTime, String startDate, String endDate, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return;

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

        check(diffDays, sd, st, et, validAppointments, validPlaces, null);
    }
    @Override
    public void check(String startTime, String endTime, String startDate, String endDate, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        List<Places> validPlaces = new ArrayList<>();
        List<Appointment> validAppointments = new ArrayList<>();

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
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

        check(diffDays, sd, st, et, validAppointments, validPlaces, null);
    }
    @Override
    public void check(String startTime, String endTime, String day, String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);

        check(diffDays, sd, st, et, getSchedule().getAppointments(), getSchedule().getPlaces(), day);
    }
    @Override
    public void check(String startTime, String endTime, String day, String startDate, String endDate, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return;

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

        check(diffDays, sd, st, et, validAppointments, validPlaces, day);
    }
    @Override
    public void check(String startTime, String endTime, String day, String startDate, String endDate, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return;

        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        List<Places> validPlaces = new ArrayList<>();
        List<Appointment> validAppointments = new ArrayList<>();

        for(Places p : getSchedule().getPlaces()) {
            if (p.getName().equals(place.getName())){
                validPlaces.add(p);
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

        check(diffDays, sd, st, et, validAppointments, validPlaces, day);
    }

    private void check(long diffDays, LocalDate sd, LocalTime startTime, LocalTime endTime, List<Appointment> appointments, List<Places> places, String day){
        int i = 0, j = 0;

        while(true){
            if(j >= appointments.size() || i >= diffDays)
                break;

            Appointment a = appointments.get(j);
            if(!(getSchedule().getNonWorkingDates().contains(sd.plusDays(i)) || getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)) || (day != null && !(day.equals(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)))))){
                if (sd.plusDays(i).equals(a.getStartDate())) {
                    for (Places p : places) {
                        if (a.getPlace().getName().equals(p.getName())) {
                            System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                            System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                            LocalTime start = startTime;

                            while (j < appointments.size()) {
                                Appointment a2 = appointments.get(j);
                                if (a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())) {
                                    if (!a2.getStartTime().equals(start)) {
                                        if(a2.getStartTime().isAfter(endTime)){
                                            System.out.print(" " + start + "-" + endTime);
                                        }else
                                            System.out.print(" " + start + "-" + a2.getStartTime());
                                    }
                                    start = a2.getEndTime();
                                    j++;
                                }
                                else {
                                    break;
                                }
                            }
                            if (!start.equals(endTime))
                                System.out.print(" " + start + "-" + endTime);
                            System.out.println(" " + a.getPlace().getName());
                        }
                        else{
                            System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                            System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                            System.out.print(" " + startTime + "-" + endTime);
                            System.out.println(" " + p.getName());
                        }
                    }
                    i++;
                }
                else if(sd.plusDays(i).isAfter(a.getStartDate())){
                    j++;
                }
                else{
                    for (Places p : places) {
                        System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                        System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                        System.out.print(" " + startTime + "-" + endTime);
                        System.out.println(" " + p.getName());
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
                        System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                        System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                        System.out.print(" " + startTime + "-" + endTime);
                        System.out.println(" " + p.getName());
                    }
                }
                i++;
            }
        }
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

        sortAppointmentList();
    }
}
