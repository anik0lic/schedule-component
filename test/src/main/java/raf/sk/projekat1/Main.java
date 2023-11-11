package raf.sk.projekat1;

import raf.sk.projekat1.model.Info;
import raf.sk.projekat1.model.Places;
import raf.sk.projekat1.model.Schedule;
import raf.sk.projekat1.specification.ScheduleService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
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
        ScheduleService ss = new ScheduleServiceImpl(schedule);

        ss.loadJSON("E:\\IntellJ Projects\\projekat\\test\\src\\main\\resources\\terminiJSON1.json");

//        System.out.println(ss.getSchedule().getStartDate());
//        System.out.println(ss.getSchedule().getEndDate());
//        System.out.println(ss.getSchedule().getStartTime());
//        System.out.println(ss.getSchedule().getEndTime());
//        System.out.println(ss.getSchedule().getAppointments());
//        System.out.println(ss.getSchedule().getPlaces());

//        System.out.println(ss.getSchedule().getInfo().getDay());

        ss.search();

        Places place = new Places("RAF1");
        Map<String, String> a = new HashMap<>();
        a.put("racunari", "NE");
//
//        ss.search("Ned", "02/10/2023", "23/10/2023", place, a);
//        ss.check("10:00", "16:00","02/10/2023", "23/10/2023");
//        System.out.println(ss.getSchedule().getPlaces());
        ss.check("02/10/2023", "23/10/2023");

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