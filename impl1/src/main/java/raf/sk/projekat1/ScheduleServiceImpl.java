package raf.sk.projekat1;

import raf.sk.projekat1.model.Appointment;
import raf.sk.projekat1.model.AppointmentRepeat;
import raf.sk.projekat1.model.Places;
import raf.sk.projekat1.model.Schedule;
import raf.sk.projekat1.specification.ScheduleService;

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
        if(date.isBefore(getSchedule().getStartDate()) || date.isAfter(getSchedule().getEndDate()))
            return false;
        String[] split = time.split("-");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);
        if(startTime.isBefore(getSchedule().getStartTime()) || endTime.isAfter(getSchedule().getEndTime()))
            return false;

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().equals(date) && (a.getStartTime().equals(startTime) || a.getEndTime().equals(endTime) || (a.getStartTime().isBefore(startTime) && a.getEndTime().isAfter(startTime)) || (a.getStartTime().isBefore(endTime)
                    && a.getEndTime().isAfter(endTime))) && a.getPlace().getName().equals(place)) || (getSchedule().getNonWorkingDates().contains(date) && getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(date.getDayOfWeek().getValue()-1)))){
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
    public void addAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat, Map<String, String> additional) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(start.isBefore(getSchedule().getStartDate()) || end.isAfter(getSchedule().getEndDate()))
            return;
        String[] split = time.split("-");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);
        if(startTime.isBefore(getSchedule().getStartTime()) || endTime.isAfter(getSchedule().getEndTime()))
            return;
        Duration diff = Duration.between(start.atStartOfDay(), end.atStartOfDay());
        long diffDays = diff.toDays();
        List<Appointment> validAppointments = new ArrayList<>();

        if(repeat.equals(AppointmentRepeat.EVERY_DAY)){
            for(int i = 0; i <= diffDays; i++) {
                for (Appointment a : getSchedule().getAppointments()) {
                    if((a.getStartDate().equals(start.plusDays(i)) && (a.getStartTime().equals(startTime) || a.getEndTime().equals(endTime) || (a.getStartTime().isBefore(startTime) && a.getEndTime().isAfter(startTime)) || (a.getStartTime().isBefore(endTime)
                            && a.getEndTime().isAfter(endTime))) && a.getPlace().getName().equals(place))){
                        System.out.println("greska");
                        return;
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
                        return;
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
                        return;
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
    }
    @Override
    public void removeAppointment(String when, String place, String time) {
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
        if(!(appointment == null))
            getSchedule().getAppointments().remove(appointment);
    }
    @Override
    public void removeAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat) {
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
                return;
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
                return;
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
                return;
            }
        }

        for(Appointment a : validAppointments){
            getSchedule().getAppointments().remove(a);
        }

        sortAppointmentList();
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

    }

    @Override
    public void updateAppointment(Appointment appointment, String when, String startTime, String endTime) {

    }

    @Override
    public void updateAppointment(Appointment appointment, String when, String startTime, String endTime, Places place) {

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
            for(Map.Entry<String,String> entry : additional.entrySet()){
                if(a.getAdditional().containsValue(entry.getValue())){
                    System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue()-1));
                    System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                    System.out.print(" " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName() + "\n");
                }
            }
        }
    }
    @Override
    public void search(String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)) || a.getStartDate().isEqual(sd) || a.getStartDate().isEqual(ed)) {
                System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
                System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                System.out.print(" " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName() + "\n");
            }
        }
    }
    @Override
    public void search(String startDate, String endDate, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)) || a.getStartDate().isEqual(sd) || a.getStartDate().isEqual(ed)) {
                for(Map.Entry<String,String> entry : additional.entrySet()) {
                    if (a.getAdditional().containsValue(entry.getValue())) {
                        System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
                        System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                        System.out.print(" " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName() + "\n");
                    }
                }
            }
        }
    }
    @Override
    public void search(String startDate, String endDate, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)) || a.getStartDate().isEqual(sd) || a.getStartDate().isEqual(ed)) {
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
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)) || a.getStartDate().isEqual(sd) || a.getStartDate().isEqual(ed)) {
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
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)) || a.getStartDate().isEqual(sd) || a.getStartDate().isEqual(ed)) {
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
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)) || a.getStartDate().isEqual(sd) || a.getStartDate().isEqual(ed)) {
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
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)) || a.getStartDate().isEqual(sd) || a.getStartDate().isEqual(ed)) {
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
    //sreda 18.10.2023. 08:00-10:00, 12:00-20:00 soba S1
    ////oba vremena su izmedju
    //oba su pre/posle
    //ne postoji vreme
    //jedno je izmedju jedno je posle
    //jedno je pre drugo izmedju
    @Override
    public void check(String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();
        int j = 0, i = 0;

        while(i < diffDays){
            while(j < getSchedule().getAppointments().size()){
                Appointment a = getSchedule().getAppointments().get(j);
                if(!(getSchedule().getNonWorkingDates().contains(sd.plusDays(i)) || getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)))) {
                    if (sd.plusDays(i).equals(a.getStartDate())) {
                        for (Places p : getSchedule().getPlaces()) {
                            if (a.getPlace().getName().equals(p.getName())) {
                                System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                                System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                                LocalTime start = getSchedule().getStartTime();

                                while (j < getSchedule().getAppointments().size()) {
                                    Appointment a2 = getSchedule().getAppointments().get(j);
                                    if (a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())) {
                                        if (!a2.getStartTime().equals(start)) {
                                            System.out.print(" " + start + "-" + a2.getStartTime());
                                        }
                                        start = a2.getEndTime();
                                        j++;
                                    } else {
                                        break;
                                    }
                                }
                                if (!start.equals(getSchedule().getEndTime()))
                                    System.out.print(" " + start + "-" + getSchedule().getEndTime());
                                System.out.println(" " + a.getPlace().getName());
                            } else {
                                System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                                System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                                System.out.print(" " + getSchedule().getStartTime() + "-" + getSchedule().getEndTime());
                                System.out.println(" " + p.getName());
                            }
                        }
                    } else {
                        for (Places p : getSchedule().getPlaces()) {
                            System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                            System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                            System.out.print(" " + getSchedule().getStartTime() + "-" + getSchedule().getEndTime());
                            System.out.println(" " + p.getName());
                        }
                        i++;
                        break;
                    }
                    i++;
                }else{
                    i++;
                    break;
                }
            }
        }
    }
    @Override
    public void check(String startDate, String endDate, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();
        int j = 0, i = 0;
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


        while(i < diffDays){
            while(j < validAppointments.size()){
                Appointment a = validAppointments.get(j);
                if(!(getSchedule().getNonWorkingDates().contains(sd.plusDays(i)) || getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)))) {
                    if (sd.plusDays(i).equals(a.getStartDate())) {
                        for (Places p : validPlaces) {
                            if (a.getPlace().getName().equals(p.getName())) {
                                System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                                System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                                LocalTime start = getSchedule().getStartTime();

                                while (j < getSchedule().getAppointments().size()) {
                                    Appointment a2 = validAppointments.get(j);
                                    if (a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())) {
                                        if (!a2.getStartTime().equals(start)) {
                                            System.out.print(" " + start + "-" + a2.getStartTime());
                                        }
                                        start = a2.getEndTime();
                                        j++;
                                    } else {
                                        break;
                                    }
                                }
                                if (!start.equals(getSchedule().getEndTime()))
                                    System.out.print(" " + start + "-" + getSchedule().getEndTime());
                                System.out.println(" " + a.getPlace().getName());
                            } else {
                                System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                                System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                                System.out.print(" " + getSchedule().getStartTime() + "-" + getSchedule().getEndTime());
                                System.out.println(" " + p.getName());
                            }
                        }
                    } else {
                        for (Places p : validPlaces) {
                            System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                            System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                            System.out.print(" " + getSchedule().getStartTime() + "-" + getSchedule().getEndTime());
                            System.out.println(" " + p.getName());
                        }
                        i++;
                        break;
                    }
                    i++;
                }else{
                    i++;
                    break;
                }
            }
        }
    }

    @Override
    public void check(String startDate, String endDate, String day) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();
        int j = 0, i = 0;

        while(i < diffDays){
            while(j < getSchedule().getAppointments().size()){
                Appointment a = getSchedule().getAppointments().get(j);
                if(!(getSchedule().getNonWorkingDates().contains(sd.plusDays(i)) || !day.equals(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)))) {
                    if (sd.plusDays(i).equals(a.getStartDate())) {
                        for (Places p : getSchedule().getPlaces()) {
                            if (a.getPlace().getName().equals(p.getName())) {
                                System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                                System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                                LocalTime start = getSchedule().getStartTime();

                                while (j < getSchedule().getAppointments().size()) {
                                    Appointment a2 = getSchedule().getAppointments().get(j);
                                    if (a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())) {
                                        if (!a2.getStartTime().equals(start)) {
                                            System.out.print(" " + start + "-" + a2.getStartTime());
                                        }
                                        start = a2.getEndTime();
                                        j++;
                                    } else {
                                        break;
                                    }
                                }
                                if (!start.equals(getSchedule().getEndTime()))
                                    System.out.print(" " + start + "-" + getSchedule().getEndTime());
                                System.out.println(" " + a.getPlace().getName());
                            } else {
                                System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                                System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                                System.out.print(" " + getSchedule().getStartTime() + "-" + getSchedule().getEndTime());
                                System.out.println(" " + p.getName());
                            }
                        }
                    } else {
                        for (Places p : getSchedule().getPlaces()) {
                            System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                            System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                            System.out.print(" " + getSchedule().getStartTime() + "-" + getSchedule().getEndTime());
                            System.out.println(" " + p.getName());
                        }
                        i++;
                        break;
                    }
                    i++;
                }else{
                    i++;
                    break;
                }
            }
        }
    }

    @Override
    public void check(String startDate, String endDate, String day, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(int i = 0; i < getSchedule().getAppointments().size(); i++){
            Appointment a = getSchedule().getAppointments().get(i);
            if((a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)) || a.getStartDate().isEqual(sd) || a.getStartDate().isEqual(ed)) {
                if (getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1).equals(day)) {
                    for(Map.Entry<String,String> entry : additional.entrySet()) {
                        if (a.getPlace().getAdditional().containsValue(entry.getValue())) {
                            System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
                            System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                            LocalTime start = getSchedule().getStartTime();

                            while (i < getSchedule().getAppointments().size()) {
                                Appointment a2 = getSchedule().getAppointments().get(i);

                                if (a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())) {
                                    if (!a2.getStartTime().equals(start)) {
                                        System.out.print(" " + start + "-" + a2.getStartTime());
                                    }
                                    start = a2.getEndTime();
                                    i++;
                                } else {
                                    i--;
                                    break;
                                }
                            }
                            if (!start.equals(getSchedule().getEndTime()))
                                System.out.print(" " + start + "-" + getSchedule().getEndTime());
                            System.out.println(" " + a.getPlace().getName());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void check(String startDate, String endDate, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(int i = 0; i < getSchedule().getAppointments().size(); i++){
            Appointment a = getSchedule().getAppointments().get(i);
            if(a.getPlace().getName().equals(place.getName())) {
                if ((a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)) || a.getStartDate().isEqual(sd) || a.getStartDate().isEqual(ed)) {
                    System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
                    System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                    LocalTime start = getSchedule().getStartTime();

                    while (i < getSchedule().getAppointments().size()) {
                        Appointment a2 = getSchedule().getAppointments().get(i);

                        if (a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())) {
                            if (!a2.getStartTime().equals(start)) {
                                System.out.print(" " + start + "-" + a2.getStartTime());
                            }
                            start = a2.getEndTime();
                            i++;
                        } else {
                            i--;
                            break;
                        }
                    }
                    if (!start.equals(getSchedule().getEndTime()))
                        System.out.print(" " + start + "-" + getSchedule().getEndTime());
                }
                System.out.println(" " + a.getPlace().getName());
            }
        }
    }

    @Override
    public void check(String startDate, String endDate, String day, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(int i = 0; i < getSchedule().getAppointments().size(); i++){
            Appointment a = getSchedule().getAppointments().get(i);
            if(a.getPlace().getName().equals(place.getName())) {
                if (getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1).equals(day)) {
                    if ((a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)) || a.getStartDate().isEqual(sd) || a.getStartDate().isEqual(ed)) {
                        System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
                        System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                        LocalTime start = getSchedule().getStartTime();

                        while (i < getSchedule().getAppointments().size()) {
                            Appointment a2 = getSchedule().getAppointments().get(i);

                            if (a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())) {
                                if (!a2.getStartTime().equals(start)) {
                                    System.out.print(" " + start + "-" + a2.getStartTime());
                                }
                                start = a2.getEndTime();
                                i++;
                            } else {
                                i--;
                                break;
                            }
                        }
                        if (!start.equals(getSchedule().getEndTime()))
                            System.out.print(" " + start + "-" + getSchedule().getEndTime());
                    }
                    System.out.println(" " + a.getPlace().getName());
                }
            }
        }
    }

    @Override
    public void check(String startTime, String endTime, String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        String time = startTime;




//        for(int i = 0; i < getSchedule().getAppointments().size(); i++){
//            Appointment a = getSchedule().getAppointments().get(i);
//            System.out.println(startTime);
//            if((a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)) || a.getStartDate().isEqual(sd) || a.getStartDate().isEqual(ed)) {
//                if(a.getStartTime().isAfter(LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"))) && a.getEndTime().isBefore(LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm")))){
//                    System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
//                    System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
//                    LocalTime start = LocalTime.parse(startTime);
//
//                    while(i < getSchedule().getAppointments().size()){
//                        Appointment a2 = getSchedule().getAppointments().get(i);
//
//                        if(a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())){
//                            if(!a2.getStartTime().equals(start)){
//                                System.out.print(" " + start + "-" + a2.getStartTime());
//                            }
//                            start = a2.getEndTime();
//                            i++;
//                        }
//                        else{
//                            i--;
//                            break;
//                        }
//                    }
//                    if(!start.equals(getSchedule().getEndTime()))
//                        System.out.print(" " + start + "-" + endTime);
//                    System.out.println(" " + a.getPlace().getName());
//                    startTime = time;
//                }else if(a.getStartTime().isAfter(LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"))) && a.getEndTime().isAfter(LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm")))){
//                    System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
//                    System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
//                    LocalTime start = LocalTime.parse(time);
//
//                    while(i < getSchedule().getAppointments().size()){
//                        Appointment a2 = getSchedule().getAppointments().get(i);
//
//                        if(a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())){
//                            if(!a2.getStartTime().equals(start)){
//                                System.out.print(" " + start + "-" + a2.getStartTime());
//                            }
//                            start = a2.getEndTime();
//                            i++;
//                        }
//                        else{
//                            i--;
//                            break;
//                        }
//                    }
//                    if(!start.equals(getSchedule().getEndTime()))
//                        System.out.print(" " + start + "-" + endTime);
//                    System.out.println(" " + a.getPlace().getName());
//                    startTime = time;
//                }
//                else{
//                    startTime = String.valueOf(a.getEndTime());
//                }
//            }
//        }
    }

    @Override
    public void check(String startTime, String endTime, String startDate, String endDate, Map<String, String> additional) {

    }

    @Override
    public void check(String startTime, String endTime, String startDate, String endDate, Places place) {

    }

    @Override
    public void check(String startTime, String endTime, String day, String startDate, String endDate) {

    }

    @Override
    public void check(String startTime, String endTime, String day, String startDate, String endDate, Map<String, String> additional) {

    }

    @Override
    public void check(String startTime, String endTime, String day, String startDate, String endDate, Places place) {

    }


}
