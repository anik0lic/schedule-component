package raf.sk.projekat1;

import raf.sk.projekat1.model.Appointment;
import raf.sk.projekat1.model.AppointmentRepeat;
import raf.sk.projekat1.model.Places;
import raf.sk.projekat1.model.Schedule;
import raf.sk.projekat1.specification.ScheduleService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ScheduleServiceImpl extends ScheduleService {
    public ScheduleServiceImpl(Schedule schedule) {
        super(schedule);
    }

    @Override
    public void exportCSV(String filepath) {
    }

    @Override
    public void exportJSON(String filepath) {
    }

    @Override
    public void addAppointment(String when, String place, String time, Map<String, String> additional) {

    }

    @Override
    public void addAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat, Map<String, String> additional) {

    }

    @Override
    public void removeAppointment() {

    }

    @Override
    public void updateAppointment() {

    }
    //sreda, 18.10.2023. 10-12h, soba S1
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
    //
    @Override
    public void check(String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        Duration diff = Duration.between(sd.atStartOfDay(), ed.atStartOfDay());
        long diffDays = diff.toDays();

        for(int i = 0; i < diffDays; i++){
            for(int j = 0; j < getSchedule().getAppointments().size(); j++){
                Appointment a = getSchedule().getAppointments().get(j);
                if(sd.plusDays(i).equals(a.getStartDate())){
                    for(Places p : getSchedule().getPlaces()){
                        if(a.getPlace().getName().equals(p.getName())){
                            System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                            System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                            LocalTime start = getSchedule().getStartTime();

                            while(j < getSchedule().getAppointments().size()){
                                Appointment a2 = getSchedule().getAppointments().get(j);
                                if(a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())){
                                    if(!a2.getStartTime().equals(start)){
                                        System.out.print(" " + start + "-" + a2.getStartTime());
                                    }
                                    start = a2.getEndTime();
                                    j++;
                                }
                                else{
                                    j--;
                                    break;
                                }
                            }
                            if(!start.equals(getSchedule().getEndTime()))
                                System.out.print(" " + start + "-" + getSchedule().getEndTime());
                            System.out.println(" " + a.getPlace().getName());
                        }
                        else{
                            System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                            System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                            System.out.print(" " + getSchedule().getStartTime() + "-" + getSchedule().getEndTime());
                            System.out.println(" " + p.getName());
                        }
                        //oba vremena su izmedju
                        //oba su pre/posle
                        //ne postoji vreme
                        //jedno je izmedju jedno je posle
                        //jedno je pre drugo izmedju
                    }
                }else{
                    for(Places p : getSchedule().getPlaces()){
                        System.out.print(getSchedule().getInfo().getDayFormat().get(sd.plusDays(i).getDayOfWeek().getValue() - 1));
                        System.out.print(", " + sd.plusDays(i).format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                        System.out.print(" " + getSchedule().getStartTime() + "-" + getSchedule().getEndTime());
                        System.out.println(" " + p.getName());
                    }
                }
            }
        }

//        for(int i = 0; i < getSchedule().getAppointments().size(); i++){
//            Appointment a = getSchedule().getAppointments().get(i);
//            if((a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)) || a.getStartDate().isEqual(sd) || a.getStartDate().isEqual(ed)) {
//                System.out.print(getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1));
//                System.out.print(", " + a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
//                LocalTime start = getSchedule().getStartTime();
//
//                while(i < getSchedule().getAppointments().size()){
//                    Appointment a2 = getSchedule().getAppointments().get(i);
//
//                    if(a2.getStartDate().equals(a.getStartDate()) && a2.getPlace().getName().equals(a.getPlace().getName())){
//                        if(!a2.getStartTime().equals(start)){
//                            System.out.print(" " + start + "-" + a2.getStartTime());
//                        }
//                        start = a2.getEndTime();
//                        i++;
//                    }
//                    else{
//                        i--;
//                        break;
//                    }
//                }
//                if(!start.equals(getSchedule().getEndTime()))
//                    System.out.print(" " + start + "-" + getSchedule().getEndTime());
//            }
//            System.out.println(" " + a.getPlace().getName());
//        }

    }

    @Override
    public void check(String startDate, String endDate, Map<String, String> additional) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(int i = 0; i < getSchedule().getAppointments().size(); i++){
            Appointment a = getSchedule().getAppointments().get(i);
            for(Map.Entry<String,String> entry : additional.entrySet()) {
                if (a.getPlace().getAdditional().containsValue(entry.getValue())) {
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
    public void check(String startDate, String endDate, String day) {
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));

        for(int i = 0; i < getSchedule().getAppointments().size(); i++){
            Appointment a = getSchedule().getAppointments().get(i);
            if((a.getStartDate().isAfter(sd) && a.getStartDate().isBefore(ed)) || a.getStartDate().isEqual(sd) || a.getStartDate().isEqual(ed)) {
                if (getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue() - 1).equals(day)) {
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
