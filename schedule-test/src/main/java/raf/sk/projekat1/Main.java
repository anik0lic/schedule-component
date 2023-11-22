package raf.sk.projekat1;

import com.itextpdf.text.DocumentException;
import raf.sk.projekat1.gui.StartGui;
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
//        try {
//            List<String> dayFormat = new ArrayList<>();
//            dayFormat.add("PON");
//            dayFormat.add("UTO");
//            dayFormat.add("SRE");
//            dayFormat.add("CET");
//            dayFormat.add("PET");
//            dayFormat.add("SUB");
//            dayFormat.add("NED");

            //csv11
//            Info info = new Info(7, 6, 4, 5, "dd/MM/yyyy", dayFormat);
            //csv12
//            Info info = new Info(6, 5, -1, 4, "dd/MM/yyyy", dayFormat);
            //csv21
//            Info info = new Info(6, 5, 4, -1, -1, "dd/MM/yyyy", dayFormat);
            //csv22
//            Info info = new Info(8, 7, 4, 5, 6, "dd/MM/yyyy", dayFormat);
            //csv23
//            Info info = new Info(7, 6, -1, 4, 5, "dd/MM/yyyy", dayFormat);
//            LocalDate startDate = LocalDate.parse("01/10/2023", DateTimeFormatter.ofPattern(info.getDateFormat()));
//            LocalDate endDate = LocalDate.parse("01/06/2024", DateTimeFormatter.ofPattern(info.getDateFormat()));
//            LocalTime startTime = LocalTime.parse("09:00", DateTimeFormatter.ofPattern("HH:mm"));
//            LocalTime endTime = LocalTime.parse("21:00", DateTimeFormatter.ofPattern("HH:mm"));
//            Schedule schedule = new Schedule(startDate, endDate, startTime, endTime, info);

//            Class<?> impl = Class.forName("raf.sk.projekat1.ScheduleServiceImpl");
//            ScheduleService ss = (ScheduleService) impl.getDeclaredConstructor().newInstance();

//            ss.setSchedule(schedule);

//            ss.loadPlacesCSV("E:\\IntellJ Projects\\schedule-component\\schedule-test\\src\\main\\resources\\places.csv");
//            ss.loadCSV("C:\\Users\\Lukam\\Desktop\\softverskjo\\projekat2\\schedule-test\\src\\main\\resources\\terminiCSV22.csv");
//            Places place = new Places("RAF1");
//
//            Map<String, String> a = new HashMap<>();
//            a.put("racunari", "NE");
//            ss.check("02/10/2023", "23/10/2023", "Pon");
//            ss.addAppointment("10/10/2023",  "30/10/2023",  "11:00-13:00", "RAF1", AppointmentRepeat.EVERY_DAY, a);
//            ss.addAppointment("10/10/2023",  "30/12/2023",  "11:00-13:00", "RAF1", AppointmentRepeat.EVERY_WEDNESDAY, a);
//            ss.removeAppointment("10/10/2023",  "30/10/2023",  "11:00-13:00", "RAF1", AppointmentRepeat.EVERY_DAY);
//            ss.removeAppointment("10/10/2023",  "30/12/2023",  "11:00-13:00", "RAF1", AppointmentRepeat.EVERY_WEDNESDAY);
//            ss.addAppointment("14/10/2023", "RAF1", "09:00-11:00", a);
//            Appointment app = ss.find("02/10/2023", "RAF1", "09:00-11:00");
//            ss.updateAppointment(app, "03/10/2023" , "13:00", "14:00",place);

//            ss.exportPDF("C:\\Users\\Lukam\\Desktop\\softverskjo\\projekat2\\schedule-test\\src\\main\\resources\\test.pdf", ss.getSchedule().getAppointments());
//            ss.printAppointments(ss.search());

            StartGui.getInstance();

//            System.out.println(ss.addAppointment("04/09/2023", "RAF1", "11:00-14:00", a));
//            System.out.println(ss.addAppointment("04/09/2023", "30/09/2023", "11:00-14:00", "RAF1", AppointmentRepeat.EVERY_WEEK, a));
//
//            Appointment app = ss.find("11/09/2023-18/09/2023", "RAF1", "11:00-14:00");
//            ss.printAppointments(List.of(app));
//
//            System.out.println(ss.updateAppointment(app, "10/11/2023-17/11/2023"));
//
//            ss.printAppointments(ss.search());

//            System.out.println(ss.removeAppointment("01/09/2023", "15/09/2023", "11:00-14:00", "RAF1", AppointmentRepeat.EVERY_MONDAY));
//            ss.printAppointments(ss.search());
//            System.out.println(ss.addAppointment("04/09/2023", "RAF1", "14:00-15:00", a));
//            System.out.println(ss.addAppointment("04/09/2023", "RAF1", "10:00-12:00", a));
//            System.out.println(ss.addAppointment("04/09/2023", "RAF1", "11:00-14:00", a));
//
//            System.out.println(ss.addAppointment("27/11/2023", "RAF1", "08:00-10:00", a));
//            System.out.println(ss.addAppointment("27/11/2023", "RAF1", "11:00-14:00", a));
//
//            System.out.println(ss.addAppointment("11/09/2023", "RAF1", "08:00-10:00", a));

//        } catch (NoSuchMethodException | IllegalAccessException |
//                 InstantiationException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        } catch (DocumentException e) {
//            throw new RuntimeException(e);
//        }
    }
}