package raf.sk.projekat1.gui;

import lombok.Getter;
import lombok.Setter;
import raf.sk.projekat1.controller.ActionManager;

import javax.swing.*;
import java.awt.*;

@Getter
public class StartGui extends JFrame {

    private static StartGui instance;
    private ActionManager actionManager;

    private StartGui() {
    }

//    public StartGui() {
//        InitialiseGui();
//    }

    private void initialiseGui() {
        actionManager = new ActionManager();

        setTitle("Schedule");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        setSize((int) (screenWidth / 3), (int) (screenHeight / 3));
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 5, 5, 5);

        JButton button = new JButton("CSV");
        button.setPreferredSize(new Dimension(200, 50));
        button.setBackground(Color.CYAN);
        button.setAction(StartGui.getInstance().getActionManager().getInfoCSVAction());

        JButton button2 = new JButton("JSON");
        button2.setPreferredSize(new Dimension(200, 50));
        button2.setBackground(Color.CYAN);
        button2.setAction(StartGui.getInstance().getActionManager().getInfoJSONAction());

        panel.add(button, gbc);
        panel.add(button2, gbc);

        add(panel);
        setVisible(true);
    }

    public static StartGui getInstance() {
        if (instance == null) {
            instance = new StartGui();
            instance.initialiseGui();
        }
        return instance;
    }
}