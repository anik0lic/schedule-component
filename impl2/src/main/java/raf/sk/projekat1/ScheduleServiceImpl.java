package raf.sk.projekat1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import raf.sk.projekat1.model.*;
import raf.sk.projekat1.specification.ScheduleService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.*;

public class ScheduleServiceImpl extends ScheduleService {
    public ScheduleServiceImpl(Schedule schedule) {
        super(schedule);
    }

    public ScheduleServiceImpl(){}

    @Override
    public void exportCSV(String filepath) throws IOException {


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
            if(a.getStartDate().equals(date) || a.getEndDate().equals(date) || checkAppointmentForDate(a, date)) {
                if(a.getPlace().getName().equals(place)){
                    if(startTime.isBefore(a.getEndTime()) && endTime.isAfter(a.getStartTime())) {
                        System.out.println("Greska: Termin vec postoji.");
                        return false;
                    }
                }
            }
        }

        Appointment newAppointment = new Appointment(startTime,endTime,date,date,getSchedule().getInfo().getDayFormat().get(date.getDayOfWeek().getValue()-1),additional);

        for(Places p : getSchedule().getPlaces()){
            if(p.getName().equals(place)){
                newAppointment.setPlace(p);
            }
        }

        getSchedule().getAppointments().add(newAppointment);
        sortAppointmentList();

        return true;
    }

    private boolean checkAppointmentForDate(Appointment a, LocalDate date){
        Duration diff = Duration.between(a.getStartDate().atStartOfDay(), a.getEndDate().atStartOfDay());
        long diffDays = diff.toDays();

        for(int i = 0; i <= diffDays; i += 7){
            if(a.getStartDate().plusDays(i) == date){
                return false;
            }
        }

        return true;
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

        for(Appointment a : getSchedule().getAppointments()){
            if( (sd.isBefore(a.getEndDate()) || sd.equals(a.getEndDate()) ) && ( ed.isAfter(a.getStartDate()) || ed.equals(a.getStartDate()) ) ) {
                if( a.getPlace().getName().equals(place)  ) {
                    if( startTime.isBefore(a.getEndTime()) && endTime.isAfter(a.getStartTime()) ) {
                        System.out.println("Greska: Termin vec postoji.");
                        return false;
                    }
                }
            }
        }

        Appointment newAppointment = new Appointment(startTime,endTime,sd,ed,getSchedule().getInfo().getDayFormat().get(sd.getDayOfWeek().getValue()-1),additional);

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
            if( a.getStartDate().equals(date) || a.getEndDate().equals(date) || ( a.getStartDate().isBefore(date) && a.getEndDate().isAfter(date)  ) ) {
                if( a.getPlace().getName().equals(place)  ) {
                    if( startTime.isBefore(a.getEndTime()) && endTime.isAfter(a.getStartTime()) ) {
                        System.out.println("Termin postoji");
                        app = a;
                        break;
                    }
                }
            }
        }

        if(app == null){
            return false;
        }

        if(date.equals(app.getStartDate()) && date.equals(app.getEndDate())){
            getSchedule().getAppointments().remove(app);
        }
        else if(date.equals(app.getStartDate()) && date.isBefore(app.getEndDate()) ){
            app.setStartDate(date.plusDays(7));
        }
        else if(date.isAfter(app.getStartDate()) && date.equals(app.getEndDate()) ){
            app.setEndDate(date.minusDays(7));
        }
        else if(date.isAfter(app.getStartDate()) && date.isBefore(app.getEndDate()) ){
            int appDay = app.getStartDate().getDayOfWeek().getValue();
            int dateDay = date.getDayOfWeek().getValue();
            LocalDate newStartdate = date;
            LocalDate newEndDate = date;

            newStartdate = newStartdate.plusDays((7-dateDay+appDay)%7);
            newEndDate = newEndDate.minusDays((7+dateDay-appDay)%7);

            Appointment newappointment = new Appointment(app.getStartTime(),app.getEndTime(),app.getStartDate(),newEndDate,app.getDay(),app.getAdditional());
            newappointment.setPlace(app.getPlace());

            app.setStartDate(newStartdate);

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

        Appointment app = null;

        for(Appointment a : getSchedule().getAppointments()){
            if( ( a.getStartDate().isBefore(ed) || a.getStartDate().equals(ed) ) && ( a.getEndDate().isAfter(sd) || a.getEndDate().equals(sd) ) ) {
                if( a.getPlace().getName().equals(place)  ) {
                    if( startTime.isBefore(a.getEndTime()) && endTime.isAfter(a.getStartTime()) ) {
                        System.out.println("Termin postoji");
                        app = a;
                        break;
                    }
                }
            }
        }

        if(app == null){
            return false;
        }

        int appDay = app.getStartDate().getDayOfWeek().getValue();
        int sdDay = sd.getDayOfWeek().getValue();
        int edDay = ed.getDayOfWeek().getValue();

        LocalDate newStartdate = ed;
        LocalDate newEndDate = sd;

        newStartdate = newStartdate.plusDays((7-edDay+appDay)%7);
        newEndDate = newEndDate.minusDays((7+sdDay-appDay)%7);

        if(sd.equals(app.getStartDate()) && ed.equals(app.getEndDate())){
            getSchedule().getAppointments().remove(app);
        }
        else if(sd.equals(app.getStartDate()) && ed.isBefore(app.getEndDate()) ){
            app.setStartDate(newStartdate);
        }
        else if(sd.isAfter(app.getStartDate()) && ed.equals(app.getEndDate()) ){
            app.setEndDate(newEndDate);
        }
        else if(sd.isAfter(app.getStartDate()) && ed.isBefore(app.getEndDate()) ){
            Appointment newappointment = new Appointment(app.getStartTime(),app.getEndTime(),app.getStartDate(),newEndDate,app.getDay(),app.getAdditional());
            newappointment.setPlace(app.getPlace());

            app.setStartDate(newStartdate);

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

        String day = getSchedule().getInfo().getDayFormat().get(startDate.getDayOfWeek().getValue()-1);

        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
            if( (startDate.isBefore(a.getEndDate()) || startDate.equals(a.getEndDate()) ) && ( endDate.isAfter(a.getStartDate()) || endDate.equals(a.getStartDate()) ) ) {
                if( a.getPlace().getName().equals(place)  ) {
                    if( startTime.isBefore(a.getEndTime()) && endTime.isAfter(a.getStartTime()) ) {
                        if(day.equals(a.getDay())) {
                            foundAppointment = a;
                        }

                    }
                }
            }
        }

        if(foundAppointment == null){
            return null;
        }

        Appointment newAppointment = new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),startDate,endDate,foundAppointment.getDay(),foundAppointment.getAdditional());
        newAppointment.setPlace(foundAppointment.getPlace());


        return newAppointment;
    }

    @Override
    public void updateAppointment(Appointment appointment, String date) {




        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
            if( (appointment.getStartDate().isBefore(a.getEndDate()) || appointment.getStartDate().equals(a.getEndDate()) ) && ( appointment.getEndDate().isAfter(a.getStartDate()) || appointment.getEndDate().equals(a.getStartDate()) ) ) {
                if( a.getPlace().getName().equals(appointment.getPlace().getName())  ) {
                    if( appointment.getStartTime().isBefore(a.getEndTime()) && appointment.getEndTime().isAfter(a.getStartTime()) ) {
                        if(appointment.getDay().equals(a.getDay())) {
                            foundAppointment = a;
                            break;
                        }

                    }
                }
            }
        }


        if(foundAppointment == null){
            return;
        }

        String time = appointment.getStartTime() + "-" + appointment.getEndTime();
        String[] dates = date.split("-");

        if(addAppointment(dates[0],dates[1],time,appointment.getPlace().getName(),AppointmentRepeat.EVERY_WEEK,appointment.getAdditional())){

            int appDay = foundAppointment.getStartDate().getDayOfWeek().getValue();
            int sdDay = appointment.getStartDate().getDayOfWeek().getValue();
            int edDay = appointment.getEndDate().getDayOfWeek().getValue();

            LocalDate newStartdate = appointment.getEndDate();
            LocalDate newEndDate = appointment.getStartDate();

            newStartdate = newStartdate.plusDays((7-edDay+appDay)%7);
            newEndDate = newEndDate.minusDays((7+sdDay-appDay)%7);

            System.out.println(newStartdate + " " + newEndDate);

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
                Appointment newappointment = new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),foundAppointment.getStartDate(),newEndDate,foundAppointment.getDay(),foundAppointment.getAdditional());
                newappointment.setPlace(foundAppointment.getPlace());

                foundAppointment.setStartDate(newStartdate);

                getSchedule().getAppointments().add(newappointment);
            }
            sortAppointmentList();




        }

    }

    @Override
    public void updateAppointment(Appointment appointment, Places place) {

        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
            if( (appointment.getStartDate().isBefore(a.getEndDate()) || appointment.getStartDate().equals(a.getEndDate()) ) && ( appointment.getEndDate().isAfter(a.getStartDate()) || appointment.getEndDate().equals(a.getStartDate()) ) ) {
                if( a.getPlace().getName().equals(appointment.getPlace().getName())  ) {
                    if( appointment.getStartTime().isBefore(a.getEndTime()) && appointment.getEndTime().isAfter(a.getStartTime()) ) {
                        if(appointment.getDay().equals(a.getDay())) {
                            foundAppointment = a;
                            break;
                        }

                    }
                }
            }
        }


        if(foundAppointment == null){
            return;
        }

        String time = appointment.getStartTime() + "-" + appointment.getEndTime();

        String startDate = appointment.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        String endDate = appointment.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        System.out.println(startDate + " " + endDate);


        if(addAppointment(startDate,endDate,time,place.getName(),AppointmentRepeat.EVERY_WEEK,appointment.getAdditional())){

            int appDay = foundAppointment.getStartDate().getDayOfWeek().getValue();
            int sdDay = appointment.getStartDate().getDayOfWeek().getValue();
            int edDay = appointment.getEndDate().getDayOfWeek().getValue();

            LocalDate newStartdate = appointment.getEndDate();
            LocalDate newEndDate = appointment.getStartDate();

            newStartdate = newStartdate.plusDays((7-edDay+appDay)%7);
            newEndDate = newEndDate.minusDays((7+sdDay-appDay)%7);

            System.out.println(newStartdate + " " + newEndDate);

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
                Appointment newappointment = new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),foundAppointment.getStartDate(),newEndDate,foundAppointment.getDay(),foundAppointment.getAdditional());
                newappointment.setPlace(foundAppointment.getPlace());

                foundAppointment.setStartDate(newStartdate);

                getSchedule().getAppointments().add(newappointment);
            }
            sortAppointmentList();




        }


    }

    @Override
    public void updateAppointment(Appointment appointment, String startTime, String endTime) {

        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
            if( (appointment.getStartDate().isBefore(a.getEndDate()) || appointment.getStartDate().equals(a.getEndDate()) ) && ( appointment.getEndDate().isAfter(a.getStartDate()) || appointment.getEndDate().equals(a.getStartDate()) ) ) {
                if( a.getPlace().getName().equals(appointment.getPlace().getName())  ) {
                    if( appointment.getStartTime().isBefore(a.getEndTime()) && appointment.getEndTime().isAfter(a.getStartTime()) ) {
                        if(appointment.getDay().equals(a.getDay())) {
                            foundAppointment = a;
                            break;
                        }

                    }
                }
            }
        }


        if(foundAppointment == null){
            return;
        }

        String time = startTime + "-" + endTime;

        String startDate = appointment.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        String endDate = appointment.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

//        System.out.println(startDate + " " + endDate);


        if(addAppointment(startDate,endDate,time,appointment.getPlace().getName(),AppointmentRepeat.EVERY_WEEK,appointment.getAdditional())){

            int appDay = foundAppointment.getStartDate().getDayOfWeek().getValue();
            int sdDay = appointment.getStartDate().getDayOfWeek().getValue();
            int edDay = appointment.getEndDate().getDayOfWeek().getValue();

            LocalDate newStartdate = appointment.getEndDate();
            LocalDate newEndDate = appointment.getStartDate();

            newStartdate = newStartdate.plusDays((7-edDay+appDay)%7);
            newEndDate = newEndDate.minusDays((7+sdDay-appDay)%7);

            System.out.println(newStartdate + " " + newEndDate);

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
                Appointment newappointment = new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),foundAppointment.getStartDate(),newEndDate,foundAppointment.getDay(),foundAppointment.getAdditional());
                newappointment.setPlace(foundAppointment.getPlace());

                foundAppointment.setStartDate(newStartdate);

                getSchedule().getAppointments().add(newappointment);
            }
            sortAppointmentList();




        }
    }

    @Override
    public void updateAppointment(Appointment appointment, Map<String, String> additional) {

        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
            if( (appointment.getStartDate().isBefore(a.getEndDate()) || appointment.getStartDate().equals(a.getEndDate()) ) && ( appointment.getEndDate().isAfter(a.getStartDate()) || appointment.getEndDate().equals(a.getStartDate()) ) ) {
                if( a.getPlace().getName().equals(appointment.getPlace().getName())  ) {
                    if( appointment.getStartTime().isBefore(a.getEndTime()) && appointment.getEndTime().isAfter(a.getStartTime()) ) {
                        if(appointment.getDay().equals(a.getDay())) {
                            foundAppointment = a;
                            break;
                        }

                    }
                }
            }
        }


        if(foundAppointment == null){
            return;
        }

        String time = appointment.getStartTime() + "-" + appointment.getEndTime();

        String startDate = appointment.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        String endDate = appointment.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

//        System.out.println(startDate + " " + endDate);

        int appDay = foundAppointment.getStartDate().getDayOfWeek().getValue();
        int sdDay = appointment.getStartDate().getDayOfWeek().getValue();
        int edDay = appointment.getEndDate().getDayOfWeek().getValue();

        LocalDate newStartdate = appointment.getEndDate().plusDays(7);
        LocalDate newEndDate = appointment.getStartDate().minusDays(7);

        newStartdate = newStartdate.plusDays((7-edDay+appDay)%7);
        newEndDate = newEndDate.minusDays((7+sdDay-appDay)%7);

        System.out.println(newStartdate + " " + newEndDate);

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
            Appointment newappointment = new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),foundAppointment.getStartDate(),newEndDate,foundAppointment.getDay(),foundAppointment.getAdditional());
            newappointment.setPlace(foundAppointment.getPlace());

            foundAppointment.setStartDate(newStartdate);

            getSchedule().getAppointments().add(newappointment);
        }
        sortAppointmentList();




        addAppointment(startDate,endDate,time,appointment.getPlace().getName(),AppointmentRepeat.EVERY_WEEK,additional);


    }

    @Override
    public void updateAppointment(Appointment appointment, String date, String startTime, String endTime) {

        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
            if( (appointment.getStartDate().isBefore(a.getEndDate()) || appointment.getStartDate().equals(a.getEndDate()) ) && ( appointment.getEndDate().isAfter(a.getStartDate()) || appointment.getEndDate().equals(a.getStartDate()) ) ) {
                if( a.getPlace().getName().equals(appointment.getPlace().getName())  ) {
                    if( appointment.getStartTime().isBefore(a.getEndTime()) && appointment.getEndTime().isAfter(a.getStartTime()) ) {
                        if(appointment.getDay().equals(a.getDay())) {
                            foundAppointment = a;
                            break;
                        }

                    }
                }
            }
        }


        if(foundAppointment == null){
            return;
        }

        String time = startTime + "-" + endTime;
        String[] dates = date.split("-");

//        System.out.println(startDate + " " + endDate);


        if(addAppointment(dates[0],dates[1],time,appointment.getPlace().getName(),AppointmentRepeat.EVERY_WEEK,appointment.getAdditional())){

            int appDay = foundAppointment.getStartDate().getDayOfWeek().getValue();
            int sdDay = appointment.getStartDate().getDayOfWeek().getValue();
            int edDay = appointment.getEndDate().getDayOfWeek().getValue();

            LocalDate newStartdate = appointment.getEndDate();
            LocalDate newEndDate = appointment.getStartDate();

            newStartdate = newStartdate.plusDays((7-edDay+appDay)%7);
            newEndDate = newEndDate.minusDays((7+sdDay-appDay)%7);

            System.out.println(newStartdate + " " + newEndDate);

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
                Appointment newappointment = new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),foundAppointment.getStartDate(),newEndDate,foundAppointment.getDay(),foundAppointment.getAdditional());
                newappointment.setPlace(foundAppointment.getPlace());

                foundAppointment.setStartDate(newStartdate);

                getSchedule().getAppointments().add(newappointment);
            }
            sortAppointmentList();




        }


    }

    @Override
    public void updateAppointment(Appointment appointment, String date, String startTime, String endTime, Places place) {

        Appointment foundAppointment = null;

        for(Appointment a : getSchedule().getAppointments()){
            if( (appointment.getStartDate().isBefore(a.getEndDate()) || appointment.getStartDate().equals(a.getEndDate()) ) && ( appointment.getEndDate().isAfter(a.getStartDate()) || appointment.getEndDate().equals(a.getStartDate()) ) ) {
                if( a.getPlace().getName().equals(appointment.getPlace().getName())  ) {
                    if( appointment.getStartTime().isBefore(a.getEndTime()) && appointment.getEndTime().isAfter(a.getStartTime()) ) {
                        if(appointment.getDay().equals(a.getDay())) {
                            foundAppointment = a;
                            break;
                        }

                    }
                }
            }
        }


        if(foundAppointment == null){
            return;
        }

        String time = startTime + "-" + endTime;
        String[] dates = date.split("-");

//        System.out.println(startDate + " " + endDate);


        if(addAppointment(dates[0],dates[1],time,place.getName(),AppointmentRepeat.EVERY_WEEK,appointment.getAdditional())){

            int appDay = foundAppointment.getStartDate().getDayOfWeek().getValue();
            int sdDay = appointment.getStartDate().getDayOfWeek().getValue();
            int edDay = appointment.getEndDate().getDayOfWeek().getValue();

            LocalDate newStartdate = appointment.getEndDate();
            LocalDate newEndDate = appointment.getStartDate();

            newStartdate = newStartdate.plusDays((7-edDay+appDay)%7);
            newEndDate = newEndDate.minusDays((7+sdDay-appDay)%7);

            System.out.println(newStartdate + " " + newEndDate);

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
                Appointment newappointment = new Appointment(foundAppointment.getStartTime(),foundAppointment.getEndTime(),foundAppointment.getStartDate(),newEndDate,foundAppointment.getDay(),foundAppointment.getAdditional());
                newappointment.setPlace(foundAppointment.getPlace());

                foundAppointment.setStartDate(newStartdate);

                getSchedule().getAppointments().add(newappointment);
            }
            sortAppointmentList();




        }

    }

    @Override
    public void search() {
        for(Appointment a : getSchedule().getAppointments()){
            System.out.print(a.getPlace().getName() + ", " + a.getDay() + " " + a.getStartTime() + "-" + a.getEndTime() + " ");
            System.out.println(a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + "-" + a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));

        }
    }
    @Override
    public void search(Places place) {
        for(Appointment a : getSchedule().getAppointments()){
            if(a.getPlace().getName().equals(place.getName())) {
                System.out.print(a.getPlace().getName() + ", " + a.getDay() + " " + a.getStartTime() + "-" + a.getEndTime() + " ");
                System.out.println(a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + "-" + a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
            }
        }
    }
    @Override
    public void search(Map<String, String> additional) {
        for(Appointment a : getSchedule().getAppointments()){
            int flag = additional.size();
            for(Map.Entry<String,String> entry : additional.entrySet()) {
                if (a.getAdditional().containsValue(entry.getValue())) {
                    flag--;
                }
            }
            if(flag == 0){
                System.out.print(a.getPlace().getName() + ", " + a.getDay() + " " + a.getStartTime() + "-" + a.getEndTime() + " ");
                System.out.println(a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + "-" + a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
            }
        }
    }
    //mora da se prikaze startdate i enddate tj to kad traje a ne ceo interval
    @Override
    public void search(String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if( ( a.getStartDate().isBefore(ed) || a.getStartDate().equals(ed) ) && ( a.getEndDate().isAfter(sd) || a.getEndDate().equals(sd) ) ) {
                System.out.print(a.getPlace().getName() + ", " + a.getDay() + " " + a.getStartTime() + "-" + a.getEndTime() + " ");
                System.out.println(a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + "-" + a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
            }
        }
    }
    @Override
    public void search(String startDate, String endDate, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if(  ( a.getStartDate().isBefore(ed) || a.getStartDate().equals(ed) ) && ( a.getEndDate().isAfter(sd) || a.getEndDate().equals(sd) )  ) {
                int flag = additional.size();
                for(Map.Entry<String,String> entry : additional.entrySet()) {
                    if (a.getAdditional().containsValue(entry.getValue())) {
                        flag--;
                    }
                }
                if(flag == 0){
                    System.out.print(a.getPlace().getName() + ", " + a.getDay() + " " + a.getStartTime() + "-" + a.getEndTime() + " ");
                    System.out.println(a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + "-" + a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                }
            }
        }
    }
    @Override
    public void search(String startDate, String endDate, Places place) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isBefore(ed) || a.getStartDate().equals(ed)) && (a.getEndDate().isAfter(sd) || a.getEndDate().equals(sd) )) {
                if(a.getPlace().getName().equals(place.getName())) {
                    System.out.print(a.getPlace().getName() + ", " + a.getDay() + " " + a.getStartTime() + "-" + a.getEndTime() + " ");
                    System.out.println(a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + "-" + a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                }
            }
        }
    }
    @Override
    public void search(String startDate, String endDate, Places place, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if(  ( a.getStartDate().isBefore(ed) || a.getStartDate().equals(ed) ) && ( a.getEndDate().isAfter(sd) || a.getEndDate().equals(sd) )  ) {
                if(a.getPlace().getName().equals(place.getName())) {
                    int flag = additional.size();
                    for (Map.Entry<String, String> entry : additional.entrySet()) {
                        if (a.getAdditional().containsValue(entry.getValue())) {
                            flag--;
                        }
                    }
                    if (flag == 0) {
                        System.out.print(a.getPlace().getName() + ", " + a.getDay() + " " + a.getStartTime() + "-" + a.getEndTime() + " ");
                        System.out.println(a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + "-" + a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
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

            if((a.getStartDate().isBefore(ed) || a.getStartDate().equals(ed)) && (a.getEndDate().isAfter(sd) || a.getEndDate().equals(sd))) {
                if(a.getPlace().getName().equals(place.getName()) && a.getDay().equals(day)) {
                    System.out.print(a.getPlace().getName() + ", " + a.getDay() + " " + a.getStartTime() + "-" + a.getEndTime() + " ");
                    System.out.println(a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + "-" + a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                }
            }
        }
    }
    @Override
    public void search(String day, String startDate, String endDate, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(Appointment a : getSchedule().getAppointments()){
            if((a.getStartDate().isBefore(ed) || a.getStartDate().equals(ed)) && (a.getEndDate().isAfter(sd) || a.getEndDate().equals(sd))) {
                if(a.getDay().equals(day)) {
                    int flag = additional.size();
                    for (Map.Entry<String, String> entry : additional.entrySet()) {
                        if (a.getAdditional().containsValue(entry.getValue())) {
                            flag--;
                        }
                    }
                    if (flag == 0) {
                        System.out.print(a.getPlace().getName() + ", " + a.getDay() + " " + a.getStartTime() + "-" + a.getEndTime() + " ");
                        System.out.println(a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + "-" + a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
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
            if((a.getStartDate().isBefore(ed) || a.getStartDate().equals(ed)) && (a.getEndDate().isAfter(sd) || a.getEndDate().equals(sd))) {
                if(a.getPlace().getName().equals(place.getName()) && a.getDay().equals(day)) {
                    int flag = additional.size();
                    for (Map.Entry<String, String> entry : additional.entrySet()) {
                        if (a.getAdditional().containsValue(entry.getValue())) {
                            flag--;
                        }
                    }
                    if (flag == 0) {
                        System.out.print(a.getPlace().getName() + ", " + a.getDay() + " " + a.getStartTime() + "-" + a.getEndTime() + " ");
                        System.out.println(a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())) + "-" + a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                    }
                }
            }
        }
    }

    //
    @Override
    public void check(String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(sd.isBefore(getSchedule().getStartDate()) || ed.isAfter(getSchedule().getEndDate()))
            return;

    }

    @Override
    public void check(String startDate, String endDate, Map<String, String> additional) {

    }

    @Override
    public void check(String startDate, String endDate, String day) {

    }

    @Override
    public void check(String startDate, String endDate, String day, Map<String, String> additional) {

    }

    @Override
    public void check(String startDate, String endDate, Places place) {

    }

    @Override
    public void check(String startDate, String endDate, String day, Places place) {

    }

    @Override
    public void check(String startTime, String endTime, String startDate, String endDate) {

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

    private void check(long diffDays, LocalDate sd, LocalDate ed, LocalTime startTime, LocalTime endTime, List<Appointment> appointments, List<Places> places, String day){
        int i = 0, j = 0;

        while(true){
            if(j >= appointments.size() || i >= diffDays)
                break;

            Appointment a = appointments.get(j);
            if(a.getDay().equals(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1))){
                if(a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)){

                }
            }




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

//        int i = 0, j = 0;
//
//        Map<String, List<String>> validAppointments = new HashMap<>();
//        List<Integer> dayCounter = new ArrayList<>();
//
//        for(int k = 0; k < getSchedule().getInfo().getDayFormat().size(); k++){
//            List<String> initialized = new ArrayList<>();
//            validAppointments.put(getSchedule().getInfo().getDayFormat().get(k), initialized);
//            dayCounter.add(0);
//        }
//        //raf1 petak 13:00-15:00 1/10/2023 - 20/01/2023
//        while(true){
//            if(j >= appointments.size() || i >= diffDays)
//                break;
//            Appointment a = appointments.get(j);
//            if(!(getSchedule().getNonWorkingDates().contains(sd.plusDays(i)) || getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)) || (day != null && !(day.equals(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1)))))){
//                String dan = getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1);
//                Integer integer = dayCounter.get(sd.plusDays(i).getDayOfWeek().getValue() - 1) + 1;
//                dayCounter.set(sd.plusDays(i).getDayOfWeek().getValue() - 1, integer);
//                if (sd.plusDays(i).equals(a.getStartDate())) {
//                    for (Places p : places) {
//                        if (a.getPlace().getName().equals(p.getName())) {
//                            List<String> newList = new LinkedList<>(validAppointments.get(dan));
//                            LocalTime start = startTime;
//                            String appointmentString = "";
//
//                            while (j < appointments.size()) {
//                                Appointment a2 = appointments.get(j);
//                                if (a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())) {
//                                    if (!a2.getStartTime().equals(start)) {
//                                        if(a2.getStartTime().isAfter(endTime)){
//                                            appointmentString += start;
//                                            appointmentString += "-" + endTime + " ";
//                                        }else{
//                                            appointmentString += start;
//                                            appointmentString += "-" + a2.getStartTime() + " ";
//                                        }
//                                    }
//                                    start = a2.getEndTime();
//                                    j++;
//                                }
//                                else {
//                                    break;
//                                }
//                            }
//                            if (!start.equals(endTime)){
//                                appointmentString += start;
//                                appointmentString += "-" + endTime + " ";
//                            }
//                            appointmentString += a.getPlace().getName();
//                            newList.add(appointmentString);
//                            validAppointments.put(dan, newList);
//                        }
//                        else{
//                            List<String> newList = new LinkedList<>(validAppointments.get(dan));
//                            String appointmentString = "";
//                            appointmentString += startTime;
//                            appointmentString += "-" + endTime + " " + p.getName();
//                            newList.add(appointmentString);
//                            validAppointments.put(dan, newList);
//                        }
//                    }
//                    i++;
//                }
//                else if(sd.plusDays(i).isAfter(a.getStartDate())){
//                    j++;
//                }
//                else{
//                    for (Places p : places) {
//                        List<String> newList = new LinkedList<>(validAppointments.get(dan));
//                        String appointmentString = "";
//                        appointmentString += startTime;
//                        appointmentString += "-" + endTime + " " + p.getName();
//                        newList.add(appointmentString);
//                        validAppointments.put(dan, newList);
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
//                        String dan = getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue()-1);
//                        List<String> newList = new LinkedList<>(validAppointments.get(dan));
//                        String appointmentString = "";
//                        appointmentString += startTime;
//                        appointmentString += "-" + endTime + " " + p.getName();
//                        newList.add(appointmentString);
//                        validAppointments.put(dan, newList);
//                        validAppointments.put(dan, newList);
//                    }
//                    Integer integer = dayCounter.get(sd.plusDays(i).getDayOfWeek().getValue() - 1) + 1;
//                    dayCounter.set(sd.plusDays(i).getDayOfWeek().getValue() - 1, integer);
//                }
//                i++;
//            }
//        }
//
//
//
//
//        System.out.println("kraj");
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
        //Pon
        //1/10/2023 - 10/10/2023
        //1/10/2023 - 10/10/2023 Pon
        for(Appointment a : schedule.getAppointments()){
            if(a.getDay() != null && a.getStartDate() == null && a.getEndDate() == null){
                a.setStartDate(getSchedule().getStartDate());
                a.setEndDate(getSchedule().getEndDate());
            }
            else if(a.getDay() == null && a.getStartDate() != null && a.getEndDate() != null){
                a.setDay(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue()-1));
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
        }

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

        getSchedule().getAppointments().sort(new Comparator<Appointment>() {
            @Override
            public int compare(Appointment o1, Appointment o2) {
                return o1.getStartDate().getDayOfWeek().compareTo(o2.getStartDate().getDayOfWeek());
            }
        });

        getSchedule().getPlaces().sort(new Comparator<Places>() {
            @Override
            public int compare(Places o1, Places o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}
