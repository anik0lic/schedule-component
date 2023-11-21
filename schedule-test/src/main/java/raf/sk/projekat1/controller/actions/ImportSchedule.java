package raf.sk.projekat1.controller.actions;

import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;


@Getter
public class ImportSchedule extends AbstractAction {

    File nesto;
    public ImportSchedule() {

        putValue(NAME, "ImportSchedule");

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JFileChooser jfc = new JFileChooser();
        jfc.showSaveDialog(null);
        nesto = jfc.getSelectedFile();


    }
}
