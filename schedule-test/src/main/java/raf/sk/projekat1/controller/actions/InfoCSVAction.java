package raf.sk.projekat1.controller.actions;

import lombok.Getter;
import raf.sk.projekat1.gui.InfoCSV;
import raf.sk.projekat1.gui.StartGui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@Getter
public class InfoCSVAction extends AbstractAction {
    InfoCSV frame;
    public InfoCSVAction() {
        putValue(NAME, "CSV");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame = new InfoCSV(StartGui.getInstance());
        StartGui.getInstance().setVisible(false);
        frame.setVisible(true);
    }
}
