package raf.sk.projekat1.controller.actions;

import lombok.Getter;
import raf.sk.projekat1.gui.InfoCSV;
import raf.sk.projekat1.gui.InfoJSON;
import raf.sk.projekat1.gui.MainFrame;
import raf.sk.projekat1.gui.StartGui;
import raf.sk.projekat1.model.Info;
import raf.sk.projekat1.model.Schedule;
import raf.sk.projekat1.specification.ScheduleService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MainFrameAction extends AbstractAction {
    MainFrame frame;

    public MainFrameAction() {
        putValue(NAME, "Next");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame() != null){

            String[] dayFormat = StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().getTfDayFormat().getText().split(",");
            String dateFormat = StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().getTfDateFormat().getText();
            int place = Integer.parseInt(StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().getTfPlace().getText());
            int time = Integer.parseInt(StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().getTfTime().getText());
            int startDate = Integer.parseInt(StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().getTfstartDate().getText());
            int endDate = Integer.parseInt(StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().getTfendDate().getText());
            int day = Integer.parseInt(StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().getTfDay().getText());

            LocalDate startDateLD = LocalDate.parse(StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().getTfSchStartDate().getText(), DateTimeFormatter.ofPattern(dateFormat));
            LocalDate endDateLD = LocalDate.parse(StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().getTfSchStartDate().getText(), DateTimeFormatter.ofPattern(dateFormat));
            LocalTime startTimeLT = LocalTime.parse(StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().getTfSchStartTime().getText(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTimeLT = LocalTime.parse(StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().getTfSchEndTime().getText(), DateTimeFormatter.ofPattern("HH:mm"));

            String[] nonWorkignDatesSTring = StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().getTfSchNonWorkingDates().getText().split(",");
            List<LocalDate> nonWorkingDates = new ArrayList<>();

            for(String s:nonWorkignDatesSTring){
                nonWorkingDates.add(LocalDate.parse(s,DateTimeFormatter.ofPattern(dateFormat)));
            }

            String[] nonWorkingDays = StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().getTfSchNonWorkingDays().getText().split(",");

            String pathPlaces = null;
            Info info = new Info(place,time,day,startDate,endDate,dateFormat, List.of(dayFormat));
            if(StartGui.getInstance().getActionManager().getImportPlaces().getNesto() != null)
                pathPlaces = StartGui.getInstance().getActionManager().getImportPlaces().getNesto().getAbsolutePath();
            String pathFile = StartGui.getInstance().getActionManager().getImportSchedule().getNesto().getAbsolutePath();

            Schedule schedule = new Schedule(startDateLD,endDateLD,startTimeLT,endTimeLT,nonWorkingDates,List.of(nonWorkingDays),info);

            try {
                Class<?> impl = Class.forName("raf.sk.projekat1.ScheduleServiceImpl");
                ScheduleService ss = (ScheduleService) impl.getDeclaredConstructor().newInstance();

                ss.setSchedule(schedule);

                if(pathPlaces != null){
                    ss.loadPlacesCSV(pathPlaces);

                }

                ss.loadCSV(pathFile);
                for(String s : ss.printAppointments(ss.getSchedule().getAppointments())){
                    System.out.println(s);
                }

                frame = new MainFrame(StartGui.getInstance(), ss);
                frame.setVisible(true);
                StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().setVisible(false);

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException | ClassNotFoundException | IOException ex) {
                throw new RuntimeException(ex);
            }

        }else if(StartGui.getInstance().getActionManager().getInfoJSONAction().getFrame() != null) {

            String[] dayFormat = StartGui.getInstance().getActionManager().getInfoJSONAction().getFrame().getDayFTF().getText().split(",");
            String dateFormat = StartGui.getInstance().getActionManager().getInfoJSONAction().getFrame().getDateFTF().getText();

            Info info = new Info(dateFormat, List.of(dayFormat));
            String path = StartGui.getInstance().getActionManager().getImportSchedule().getNesto().getAbsolutePath();

            Schedule schedule = new Schedule(info);

            try {
                Class<?> impl = Class.forName("raf.sk.projekat1.ScheduleServiceImpl");
                ScheduleService ss = (ScheduleService) impl.getDeclaredConstructor().newInstance();

                ss.setSchedule(schedule);

                ss.loadJSON(path);
                for(String s : ss.printAppointments(ss.getSchedule().getAppointments())){
                    System.out.println(s);
                }

                frame = new MainFrame(StartGui.getInstance(), ss);
                frame.setVisible(true);
                StartGui.getInstance().getActionManager().getInfoJSONAction().getFrame().setVisible(false);

            } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException |
                     InvocationTargetException | IllegalAccessException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
