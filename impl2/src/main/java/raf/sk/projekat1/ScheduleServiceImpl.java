package raf.sk.projekat1;

import raf.sk.projekat1.model.AppointmentRepeat;
import raf.sk.projekat1.model.Places;
import raf.sk.projekat1.model.Schedule;
import raf.sk.projekat1.specification.ScheduleService;

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

    @Override
    public void search() {
        System.out.println("impl2");
    }

    @Override
    public void search(Places place) {

    }

    @Override
    public void search(Map<String, String> additional) {

    }

    @Override
    public void search(String startDate, String endDate) {

    }

    @Override
    public void search(String startDate, String endDate, Map<String, String> additional) {

    }

    @Override
    public void search(String startDate, String endDate, Places place) {

    }

    @Override
    public void search(String startDate, String endDate, Places place, Map<String, String> additional) {

    }

    @Override
    public void search(String day, String startDate, String endDate, Places place) {

    }

    @Override
    public void search(String day, String startDate, String endDate, Map<String, String> additional) {

    }

    @Override
    public void search(String day, String startDate, String endDate, Places place, Map<String, String> additional) {

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
}
