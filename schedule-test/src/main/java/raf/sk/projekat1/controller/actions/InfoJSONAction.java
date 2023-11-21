package raf.sk.projekat1.controller.actions;

import raf.sk.projekat1.gui.InfoJSON;
import raf.sk.projekat1.gui.StartGui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class InfoJSONAction extends AbstractAction {
    public InfoJSONAction() {
        putValue(NAME, "JSON");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        InfoJSON frame = new InfoJSON(StartGui.getInstance());
        StartGui.getInstance().setVisible(false);
        frame.setVisible(true);
    }
}
