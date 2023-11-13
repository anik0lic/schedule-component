package raf.sk.projekat1;

import raf.sk.projekat1.model.*;
import raf.sk.projekat1.specification.ScheduleService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args){
        try {
            List<String> dayFormat = new ArrayList<>();
            dayFormat.add("Pon");
            dayFormat.add("Uto");
            dayFormat.add("Sre");
            dayFormat.add("Cet");
            dayFormat.add("Pet");
            dayFormat.add("Sub");
            dayFormat.add("Ned");

            Info info = new Info(0,2,1,"dd/MM/yyyy", dayFormat);
            Schedule schedule = new Schedule(info);

            Class<?> impl = Class.forName("raf.sk.projekat1.ScheduleServiceImpl");
            ScheduleService ss = (ScheduleService) impl.getDeclaredConstructor().newInstance();

            ss.setSchedule(schedule);
            ss.loadJSON("C:\\Users\\anikolic11522rn\\IdeaProjects\\projekat\\test\\src\\main\\resources\\terminiJSON2.json");
            Places place = new Places("RAF1");
            Map<String, String> a = new HashMap<>();
            a.put("racunari", "DA");
//            ss.check("02/10/2023", "23/10/2023", "Pon");
//            ss.addAppointment("10/10/2023",  "30/10/2023",  "11:00-13:00", "RAF1", AppointmentRepeat.EVERY_DAY, a);
//            ss.addAppointment("10/10/2023",  "30/12/2023",  "11:00-13:00", "RAF1", AppointmentRepeat.EVERY_WEDNESDAY, a);
//            ss.search();
//            ss.removeAppointment("10/10/2023",  "30/10/2023",  "11:00-13:00", "RAF1", AppointmentRepeat.EVERY_DAY);
//            ss.removeAppointment("10/10/2023",  "30/12/2023",  "11:00-13:00", "RAF1", AppointmentRepeat.EVERY_WEDNESDAY);
//            ss.addAppointment("10/10/2023", "RAF1", "11:00-13:00", a);
//            Appointment app = ss.find("02/10/2023", "RAF1", "09:00-11:00");
//            ss.updateAppointment(app, "03/10/2023" , "13:00", "14:00",place);
            ss.search();
            System.out.println(" ");
//            System.out.println(ss.addAppointment("03/10/2023", "RAF1", "11:00-13:00",a));
//            ss.addAppointment("01/10/2023","01/10/2023","14:00-15:00","RAF1",AppointmentRepeat.EVERY_WEEK,a);
            ss.removeAppointment("03/10/2023","04/10/2023","09:00-11:00","RAF1",AppointmentRepeat.EVERY_WEEK);

            ss.search();
//            ss.search("Sre","05/10/2023","10/10/2023", place,a);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

//        ScheduleService ss = new ScheduleServiceImpl(schedule);



//        ss.loadJSON("E:\\IntellJ Projects\\projekat\\test\\src\\main\\resources\\terminiJSON1.json");

//        System.out.println(ss.getSchedule().getStartDate());
//        System.out.println(ss.getSchedule().getEndDate());
//        System.out.println(ss.getSchedule().getStartTime());
//        System.out.println(ss.getSchedule().getEndTime());
//        System.out.println(ss.getSchedule().getAppointments());
//        System.out.println(ss.getSchedule().getPlaces());

//        System.out.println(ss.getSchedule().getInfo().getDay());

//        ss.search();

//
//        ss.search( "02/10/2023", "23/10/2023");

//        ss.check("10:00", "16:00","02/10/2023", "23/10/2023");
//        System.out.println(ss.getSchedule().getPlaces());
//        ss.check("02/10/2023", "23/10/2023");

//
//        schedule1.loadPlacesCSV("src/main/resources/places.csv");
//
//        for(Appointment b: schedule.getAppointments()){
//            System.out.print(b.getPlace().getName() +" ");
//            System.out.print(b.getDay() +" ");
//            System.out.print(b.getStartTime() +" ");
//            System.out.print(b.getEndTime() +" ");
//            System.out.println(b.getAdditional());
//        }
//
//
//        for(Places p: schedule1.getPlaces()){
//            System.out.println(p.getAdditional());
//        }
    }
}