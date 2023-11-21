package raf.sk.projekat1.controller.actions;

import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
@Getter
public class ImportPlaces extends AbstractAction {

    File nesto;
    public ImportPlaces() {

        putValue(NAME, "ImportPlaces");

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JFileChooser jfc = new JFileChooser();
        jfc.showSaveDialog(null);
        nesto = jfc.getSelectedFile();


    }
}
