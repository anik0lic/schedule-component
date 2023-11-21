package raf.sk.projekat1.gui;

import javax.swing.*;
import java.awt.*;

public class InfoCSV extends JFrame {

    public InfoCSV(Frame owner) throws HeadlessException {
        setTitle("Info");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        setSize((int) (screenWidth / 2.5), (int) (screenHeight / 2.5));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel place = new JLabel("Place");
        JTextField tfPlace = new JTextField();
        tfPlace.setPreferredSize(new Dimension(200, 20));

        JLabel time = new JLabel("Time");
        JTextField tfTime = new JTextField();
        tfTime.setPreferredSize(new Dimension(200, 20));

        JLabel startDate = new JLabel("Start Date");
        JTextField tfstartDate = new JTextField();
        tfstartDate.setPreferredSize(new Dimension(200, 20));

        JLabel endDate = new JLabel("End Date");
        JTextField tfendDate = new JTextField();
        tfendDate.setPreferredSize(new Dimension(200, 20));

        JLabel day = new JLabel("Place");
        JTextField tfDay = new JTextField();
        tfDay.setPreferredSize(new Dimension(200, 20));

        JLabel dateFormat = new JLabel("Date Format");
        JTextField tfDateFormat = new JTextField();
        tfDateFormat.setPreferredSize(new Dimension(200, 20));

        JLabel dayFormat = new JLabel("Day Format");
        JTextField tfDayFormat = new JTextField();
        tfDayFormat.setPreferredSize(new Dimension(200, 20));

        JLabel file = new JLabel("File");
        JButton fileBtn = new JButton("File");

        JLabel places = new JLabel("Places");
        JButton placeBtn = new JButton("Places");

        JButton nextBtn = new JButton("Next");

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 0, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(place, gbc);
        gbc.gridx = 1;
        panel.add(tfPlace, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(time, gbc);
        gbc.gridx = 1;
        panel.add(tfTime, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(startDate, gbc);
        gbc.gridx = 1;
        panel.add(tfstartDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(endDate, gbc);
        gbc.gridx = 1;
        panel.add(tfendDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(day, gbc);
        gbc.gridx = 1;
        panel.add(tfDay, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(dateFormat, gbc);
        gbc.gridx = 1;
        panel.add(tfDateFormat, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(dayFormat, gbc);
        gbc.gridx = 1;
        panel.add(tfDayFormat, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(file, gbc);
        gbc.gridx = 1;
        panel.add(fileBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(places, gbc);
        gbc.gridx = 1;
        panel.add(placeBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(nextBtn, gbc);

        add(panel);


    }
}
