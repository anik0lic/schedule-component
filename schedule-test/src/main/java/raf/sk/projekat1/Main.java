package raf.sk.projekat1;

import raf.sk.projekat1.model.*;
import raf.sk.projekat1.specification.ScheduleService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            List<String> dayFormat = new ArrayList<>();
            dayFormat.add("Pon");
            dayFormat.add("Uto");
            dayFormat.add("Sre");
            dayFormat.add("Cet");
            dayFormat.add("Pet");
            dayFormat.add("Sub");
            dayFormat.add("Ned");

            //csv1
//            Info info = new Info(0, 2, 1, "dd/MM/yyyy", dayFormat);
            //csv2
//            Info info = new Info(0, 2, 1, -1, -1, "dd/MM/yyyy", dayFormat);
            //csv22
//            Info info = new Info(0, 4, 3, 1, 2, "dd/MM/yyyy", dayFormat);
            //csv23
            Info info = new Info(0, 3, -1, 1, 2, "dd/MM/yyyy", dayFormat);
            LocalDate startDate = LocalDate.parse("01/10/2023", DateTimeFormatter.ofPattern(info.getDateFormat()));
            LocalDate endDate = LocalDate.parse("01/06/2024", DateTimeFormatter.ofPattern(info.getDateFormat()));
            LocalTime startTime = LocalTime.parse("08:00", DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse("20:00", DateTimeFormatter.ofPattern("HH:mm"));
            Schedule schedule = new Schedule(startDate, endDate, startTime, endTime, info);

            Class<?> impl = Class.forName("raf.sk.projekat1.ScheduleServiceImpl");
            ScheduleService ss = (ScheduleService) impl.getDeclaredConstructor().newInstance();

            ss.setSchedule(schedule);
            ss.loadJSON("E:\\IntellJ Projects\\projekat\\test\\src\\main\\resources\\terminiJSON2.json");
//            ss.loadCSV("E:\\IntellJ Projects\\projekat\\test\\src\\main\\resources\\terminiCSV23.csv");
            Places place = new Places("RAF1");
            Map<String, String> a = new HashMap<>();
            a.put("racunari", "NE");
//            ss.check("02/10/2023", "23/10/2023", "Pon");
//            ss.addAppointment("10/10/2023",  "30/10/2023",  "11:00-13:00", "RAF1", AppointmentRepeat.EVERY_DAY, a);
//            ss.addAppointment("10/10/2023",  "30/12/2023",  "11:00-13:00", "RAF1", AppointmentRepeat.EVERY_WEDNESDAY, a);
//            ss.removeAppointment("10/10/2023",  "30/10/2023",  "11:00-13:00", "RAF1", AppointmentRepeat.EVERY_DAY);
//            ss.removeAppointment("10/10/2023",  "30/12/2023",  "11:00-13:00", "RAF1", AppointmentRepeat.EVERY_WEDNESDAY);
//            ss.addAppointment("14/10/2023", "RAF1", "09:00-11:00", a);
//            Appointment app = ss.find("02/10/2023", "RAF1", "09:00-11:00");
//            ss.updateAppointment(app, "03/10/2023" , "13:00", "14:00",place);
            ss.printAppointments(ss.search());
            System.out.println(" ");

            for(String s : ss.check("09:00","13:00","03/10/2023", "30/10/2023")){
                System.out.println(s);
            }

//            ss.printAppointments(ss.search("Pon", "02/10/2023", "30/10/2023", place));

//            System.out.println(ss.addAppointment("03/10/2023", "RAF1", "11:00-13:00",a));
//            ss.addAppointment("01/10/2023", "31/10/2023", "14:00-15:00", "RAF1", AppointmentRepeat.EVERY_WEEK, a);
//            ss.removeAppointment("03/10/2023","04/10/2023","09:00-11:00","RAF1",AppointmentRepeat.EVERY_WEEK);
//            ss.check("02/10/2023", "23/10/2023", "Pon", a);
//            ss.check("11:00", "17:00", "Pon", "02/10/2023", "23/10/2023", place);
//            ss.check("01/10/2023", "30/10/2023");
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}