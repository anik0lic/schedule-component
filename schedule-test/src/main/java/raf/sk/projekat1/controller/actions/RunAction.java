package raf.sk.projekat1.controller.actions;

import raf.sk.projekat1.gui.InfoCSV;
import raf.sk.projekat1.gui.StartGui;
import raf.sk.projekat1.model.AppointmentRepeat;
import raf.sk.projekat1.specification.ScheduleService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class RunAction extends AbstractAction {
    public RunAction() {
        putValue(NAME, "Run");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        ScheduleService ss = StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().getSs();
        String[] parameters = StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().getTextArea().getText().split(",");

        switch (String.valueOf(StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().getComboBox().getSelectedItem())){
            case "Add":
                String[] map = parameters[parameters.length-1].split("-");
                Map<String, String> additional = new HashMap<>();
                for(String s : map){
                    String[] keyValue = s.split("=");
                    additional.put(keyValue[0], keyValue[1]);
                }

                boolean added = false;

                if(parameters.length == 4){
                    //when place time additional
                    added =  ss.addAppointment(parameters[0], parameters[1], parameters[2], additional);
                }
                else if(parameters.length == 6){
                    //startDate endDate time place repeat additional
                    String repeat = parameters[parameters.length-2];
                    repeat = repeat.replaceAll("(.)([A-Z])", "$1_$2");
                    AppointmentRepeat appointmentRepeat = AppointmentRepeat.valueOf(repeat.toUpperCase());

                    added =  ss.addAppointment(parameters[0], parameters[1], parameters[2], parameters[3], appointmentRepeat, additional);
                }
                else{
                    //error
                }

                if(added){
                    System.out.println("Uspesno dodat termin");
                    StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().updateTable();
                }
                else{
                    //error
                    System.out.println("Termin nije dodat");
                }

                break;
            case "Remove":
                boolean removed = false;

                if(parameters.length == 3){
                    //when place time
                    removed =  ss.removeAppointment(parameters[0], parameters[1], parameters[2]);
                }
                else if(parameters.length == 5){
                    //startDate endDate place time repeat
                    String repeat = parameters[parameters.length-1];
                    repeat = repeat.replaceAll("(.)([A-Z])", "$1_$2");
                    AppointmentRepeat appointmentRepeat = AppointmentRepeat.valueOf(repeat.toUpperCase());

                    removed =  ss.removeAppointment(parameters[0], parameters[1], parameters[2], parameters[3], appointmentRepeat);
                }
                else{
                    //error
                }

                if(removed){
                    System.out.println("Uspesno obrisan termin");
                    StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().updateTable();
                }
                else{
                    //error
                    System.out.println("Termin nije obrisan");
                }

                break;
            case "Update":
                break;
            case "Search":
                break;
            case "Check":
                break;
            default:
                //greska je
                break;
        }

        StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().getTextArea().setText("");
    }
}
