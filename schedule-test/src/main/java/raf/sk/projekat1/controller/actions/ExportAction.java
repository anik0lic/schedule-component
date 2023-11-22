package raf.sk.projekat1.controller.actions;

import com.itextpdf.text.DocumentException;
import raf.sk.projekat1.gui.StartGui;
import raf.sk.projekat1.model.Appointment;
import raf.sk.projekat1.model.Places;
import raf.sk.projekat1.specification.ScheduleService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportAction extends AbstractAction {

    public ExportAction() {
        putValue(NAME, "Export");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ScheduleService ss = StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().getSs();

        JFileChooser jfc = new JFileChooser();
        jfc.showSaveDialog(null);
        File file = jfc.getSelectedFile();

        try {
            List<Appointment> appointments = null;

            if(StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().getTextArea().getText().isEmpty()){
                appointments = ss.getSchedule().getAppointments();
            }
            else{
                String[] parameters = StartGui.getInstance().getActionManager().getMainFrameAction().getFrame().getTextArea().getText().split(",");

                if(parameters.length == 1){
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
                    }
                    else {
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
                    }
                    else {
                        Places place = new Places(parameters[3]);

                        appointments = ss.search(parameters[0],parameters[1],parameters[2],place);
                    }
                }
                else if(parameters.length == 5){
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
                else{
                    appointments = ss.getSchedule().getAppointments();
                }
            }


            if(file.getAbsolutePath().contains(".pdf")){
                ss.exportPDF(file.getAbsolutePath(), appointments);
            }
            else if(file.getAbsolutePath().contains(".csv")) {
                ss.exportCSV(file.getAbsolutePath(), appointments);
            }
        } catch (IOException | DocumentException ex) {
            throw new RuntimeException(ex);
        }


    }
}
