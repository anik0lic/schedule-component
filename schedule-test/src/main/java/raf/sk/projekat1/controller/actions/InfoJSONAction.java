package raf.sk.projekat1.controller.actions;

import lombok.Getter;
import raf.sk.projekat1.gui.InfoJSON;
import raf.sk.projekat1.gui.StartGui;

import javax.swing.*;
import java.awt.event.ActionEvent;

@Getter
public class InfoJSONAction extends AbstractAction {
    InfoJSON frame;

    public InfoJSONAction() {
        putValue(NAME, "JSON");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame = new InfoJSON(StartGui.getInstance());
        StartGui.getInstance().setVisible(false);
        frame.setVisible(true);
    }
}
