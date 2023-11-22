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
                int row = StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().getTable().getSelectedRow();
                String tableString = (String) StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().getTable().getValueAt(row, 0);
                //UTO 28/11/2023 17:00-19:00 Raf05
                //Raf05 PON 17:00-19:00 28/11/2023-28/11/2023
                Appointment selected;
                boolean updated = false;

                String[] data = tableString.split(" ");
                System.out.println(data[0]);
                System.out.println(data[1]);
                System.out.println(data[2]);
                System.out.println(data[3]);

                if(data[3].contains("-")){
                    selected = ss.find(data[3], data[0], data[2]);
                }
                else{
                    selected = ss.find(data[1], data[3], data[2]);
                }

                if(parameters.length == 1){
                    if(parameters[0].contains("=")){
                        String[] mapa = parameters[parameters.length-1].split("-");
                        Map<String, String> additionals = new HashMap<>();
                        for(String s : mapa){
                            String[] keyValue = s.split("=");
                            additionals.put(keyValue[0], keyValue[1]);
                        }

                        updated = ss.updateAppointment(selected, additionals);
                    }
                    else {
                        int flag = 0;
                        for (Places p : ss.getSchedule().getPlaces()) {
                            if (p.getName().equals(parameters[0])) {
                                flag = 1;
                                break;
                            }
                        }

                        if (flag == 1) {
                            Places place = new Places(parameters[0]);
                            updated = ss.updateAppointment(selected, place);
                        } else {
                            updated = ss.updateAppointment(selected, parameters[0]);
                        }
                    }
                }
                else if(parameters.length == 2){
                    updated = ss.updateAppointment(selected, parameters[0], parameters[1]);
                }
                else if(parameters.length == 3){
                    updated = ss.updateAppointment(selected, parameters[0], parameters[1], parameters[2]);
                }
                else if(parameters.length == 4){
                    Places place = new Places(parameters[3]);

                    updated = ss.updateAppointment(selected, parameters[0], parameters[1], parameters[2], place);
                }

                if(updated){
                    System.out.println("Uspesno promenjen termin");
                    StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().updateTable();
                }
                else{
                    //error
                    System.out.println("Termin nije promenjen");
                }

                break;
            case "Search":
                List<Appointment> appointments = new ArrayList<>();

                if(StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().getTextArea().getText().isEmpty()){
                    appointments = ss.search();
                }else if(parameters.length == 1){
                    if(parameters[0].contains("=")){
                        String[] map1 = parameters[parameters.length-1].split("-");
                        Map<String, String> additional1 = new HashMap<>();
                        for(String s : map1){
                            String[] keyValue = s.split("=");
                            additional1.put(keyValue[0], keyValue[1]);
                        }

                        appointments = ss.search(additional1);
                    }
                    else{
                        Places place = new Places(parameters[0]);

                        appointments = ss.search(place);
                    }
                }
                else if(parameters.length == 2){
                    appointments = ss.search(parameters[0],parameters[1]);
                }
                else if(parameters.length == 3){
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


                }


                System.out.println(StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().getTextArea().getText());

                StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().searchUpdate(ss.printAppointments(appointments));


                break;
            case "Check":
                List<String> results = new ArrayList<>();
                //2 3 4 5 6
                if(parameters.length == 2){
                    results = ss.check(parameters[0], parameters[1]);
                }
                else if(parameters.length == 3){
                    if(parameters[2].contains("=")){
                        String[] mapa = parameters[parameters.length-1].split("-");
                        Map<String, String> additionals = new HashMap<>();
                        for(String s : mapa){
                            String[] keyValue = s.split("=");
                            additionals.put(keyValue[0], keyValue[1]);
                        }

                        results = ss.check(parameters[0], parameters[1], additionals);
                    }
                    else if(ss.getSchedule().getInfo().getDayFormat().contains(parameters[2])){
                        results = ss.check(parameters[0], parameters[1], parameters[2]);
                    }
                    else{
                        Places place = new Places(parameters[2]);

                        results = ss.check(parameters[0], parameters[1], place);
                    }
                }
                else if(parameters.length == 4){
                    if(parameters[3].contains("=")){
                        String[] mapa = parameters[parameters.length-1].split("-");
                        Map<String, String> additionals = new HashMap<>();
                        for(String s : mapa){
                            String[] keyValue = s.split("=");
                            additionals.put(keyValue[0], keyValue[1]);
                        }

                        results = ss.check(parameters[0], parameters[1], parameters[2], additionals);
                    }
                    else if(ss.getSchedule().getInfo().getDayFormat().contains(parameters[2])){
                        Places place = new Places(parameters[3]);

                        results = ss.check(parameters[0], parameters[1], parameters[2], place);
                    }
                    else{
                        results = ss.check(parameters[0], parameters[1], parameters[2], parameters[3]);
                    }
                }
                else if(parameters.length == 5){
                    if(parameters[4].contains("=")){
                        String[] mapa = parameters[parameters.length-1].split("-");
                        Map<String, String> additionals = new HashMap<>();
                        for(String s : mapa){
                            String[] keyValue = s.split("=");
                            additionals.put(keyValue[0], keyValue[1]);
                        }

                        results = ss.check(parameters[0], parameters[1], parameters[2], parameters[3], additionals);
                    }
                    else if(ss.getSchedule().getInfo().getDayFormat().contains(parameters[2])){
                        results = ss.check(parameters[0], parameters[1], parameters[2], parameters[3],  parameters[4]);
                    }
                    else{
                        Places place = new Places(parameters[4]);

                        results = ss.check(parameters[0], parameters[1], parameters[2], parameters[3], place);
                    }
                }
                else if(parameters.length == 6){
                    if(parameters[5].contains("=")){
                        String[] mapa = parameters[parameters.length-1].split("-");
                        Map<String, String> additionals = new HashMap<>();
                        for(String s : mapa){
                            String[] keyValue = s.split("=");
                            additionals.put(keyValue[0], keyValue[1]);
                        }

                        results = ss.check(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], additionals);
                    }
                    else{
                        Places place = new Places(parameters[5]);

                        results = ss.check(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], place);
                    }
                }

                StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().searchUpdate(results);

                break;
            default:
                System.out.println("Greska");
                break;
        }

        StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().getTextArea().setText("");
    }
}
