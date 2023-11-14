package raf.sk.projekat1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import raf.sk.projekat1.model.*;
import raf.sk.projekat1.specification.ScheduleService;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

        //16/10/2023
        //02/10/2023 - 30/12/2023
        for(Appointment a : getSchedule().getAppointments()){
            if(a.getStartDate().equals(date) || a.getEndDate().equals(date) || checkAppointmentForDate(a, date)) {
                if(a.getPlace().getName().equals(place)){
                    if(startTime.isBefore(a.getEndTime()) && endTime.isAfter(a.getStartTime())) {
                        System.out.println("Termin postoji");
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
            if((sd.isBefore(a.getEndDate()) || sd.equals(a.getEndDate()) ) && (ed.isAfter(a.getStartDate()) || ed.equals(a.getStartDate()))){
                if( a.getPlace().getName().equals(place)  ) {
                    if( startTime.isBefore(a.getEndTime()) && endTime.isAfter(a.getStartTime()) ) {
                        System.out.println("Termin postoji");
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
        return null;
    }

    @Override
    public void updateAppointment(Appointment appointment, String date) {

    }

    @Override
    public void updateAppointment(Appointment appointment, Places place) {

    }

    @Override
    public void updateAppointment(Appointment appointment, String startTime, String endTime) {

    }

    @Override
    public void updateAppointment(Appointment appointment, Map<String, String> additional) {

    }

    @Override
    public void updateAppointment(Appointment appointment, String date, String startTime, String endTime) {

    }

    @Override
    public void updateAppointment(Appointment appointment, String date, String startTime, String endTime, Places place) {

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

    @Override
    public void check(String startDate, String endDate) {

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
}
