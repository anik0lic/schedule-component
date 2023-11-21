package raf.sk.projekat1.controller.actions;

import raf.sk.projekat1.gui.InfoCSV;
import raf.sk.projekat1.gui.MainFrame;
import raf.sk.projekat1.gui.StartGui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MainFrameAction extends AbstractAction {
    public MainFrameAction() {
        putValue(NAME, "Next");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MainFrame frame = new MainFrame(StartGui.getInstance());
        frame.setVisible(true);

        if(StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame() != null){
            StartGui.getInstance().getActionManager().getInfoCSVAction().getFrame().setVisible(false);
        }else if(StartGui.getInstance().getActionManager().getInfoJSONAction().getFrame() != null)
            StartGui.getInstance().getActionManager().getInfoJSONAction().getFrame().setVisible(false);
    }
}
