package raf.sk.projekat1.gui;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
@Getter
@Setter
public class MainFrame extends JFrame {

    public MainFrame(Frame owner) throws HeadlessException {
        setTitle("Schedule");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        setSize((int) (screenWidth / 2), (int) (screenHeight / 2));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(owner);
    }

}
