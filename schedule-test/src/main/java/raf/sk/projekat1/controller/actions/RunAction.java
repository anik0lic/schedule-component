package raf.sk.projekat1.controller.actions;

import raf.sk.projekat1.gui.InfoCSV;
import raf.sk.projekat1.gui.StartGui;
import raf.sk.projekat1.model.Appointment;
import raf.sk.projekat1.model.AppointmentRepeat;
import raf.sk.projekat1.model.Places;
import raf.sk.projekat1.specification.ScheduleService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


                List<Appointment> appointments = new ArrayList<>();
                if(parameters.length == 2){

                    appointments = ss.search(parameters[0],parameters[1]);

                }else if(parameters.length == 3){

                    if(parameters[2].contains("=")){

                        String[] map1 = parameters[parameters.length-1].split("-");
                        Map<String, String> additional1 = new HashMap<>();
                        for(String s : map1){
                            String[] keyValue = s.split("=");
                            additional1.put(keyValue[0], keyValue[1]);
                        }

                        appointments = ss.search(parameters[0],parameters[1],additional1);

                    }else {

                        Places place = new Places(parameters[2]);

                        appointments = ss.search(parameters[0],parameters[1],place);

                    }

                }else if(parameters.length == 4){

                    if(parameters[3].contains("=")){

                        String[] map1 = parameters[parameters.length-1].split("-");
                        Map<String, String> additional1 = new HashMap<>();
                        for(String s : map1){
                            String[] keyValue = s.split("=");
                            additional1.put(keyValue[0], keyValue[1]);
                        }

                        if(ss.getSchedule().getInfo().getDayFormat().contains(parameters[0])){
                            appointments = ss.search(parameters[0],parameters[1],parameters[2],additional1);
                        }else {
                            Places place = new Places(parameters[2]);
                            appointments = ss.search(parameters[0],parameters[1],place,additional1);
                        }




                    }else {

                        Places place = new Places(parameters[3]);

                        appointments = ss.search(parameters[0],parameters[1],parameters[2],place);

                    }



                }else if(parameters.length == 5){
                    Places place = new Places(parameters[3]);

                    String[] map1 = parameters[parameters.length-1].split("-");
                    Map<String, String> additional1 = new HashMap<>();
                    for(String s : map1){
                        String[] keyValue = s.split("=");
                        additional1.put(keyValue[0], keyValue[1]);
                    }

                    appointments = ss.search(parameters[0],parameters[1],parameters[2],place,additional1);


                }else if(StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().getTextArea().getText().isEmpty()){
                    appointments = ss.search();
                }


                StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().searchUpdate(ss.printAppointments(appointments));


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
