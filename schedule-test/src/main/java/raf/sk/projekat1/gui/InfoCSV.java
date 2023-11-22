package raf.sk.projekat1.gui;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
@Getter
@Setter
public class InfoCSV extends JFrame {
    private JTextField tfPlace = new JTextField();
    private JTextField tfTime = new JTextField();
    private JTextField tfstartDate = new JTextField();
    private JTextField tfendDate = new JTextField();
    private JTextField tfDay = new JTextField();
    private JTextField tfDateFormat = new JTextField();
    private JTextField tfDayFormat = new JTextField();
    private JTextField tfSchStartDate = new JTextField();
    private JTextField tfSchEndDate = new JTextField();
    private JTextField tfSchStartTime = new JTextField();
    private JTextField tfSchEndTime = new JTextField();
    private JTextField tfSchNonWorkingDates = new JTextField();
    private JTextField tfSchNonWorkingDays = new JTextField();
    private JButton fileBtn = new JButton("File");
    private JButton placeBtn = new JButton("Places");
    private JButton nextBtn = new JButton("Next");

    public InfoCSV(Frame owner) throws HeadlessException {
        setTitle("Info");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        setSize((int) (screenWidth / 2), (int) (screenHeight / 2));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(owner);

        tfPlace.setPreferredSize(new Dimension(200, 20));
        tfTime.setPreferredSize(new Dimension(200, 20));
        tfstartDate.setPreferredSize(new Dimension(200, 20));
        tfendDate.setPreferredSize(new Dimension(200, 20));
        tfDay.setPreferredSize(new Dimension(200, 20));
        tfDateFormat.setPreferredSize(new Dimension(200, 20));
        tfDayFormat.setPreferredSize(new Dimension(200, 20));
        tfSchStartDate.setPreferredSize(new Dimension(200, 20));
        tfSchEndDate.setPreferredSize(new Dimension(200, 20));
        tfSchStartTime.setPreferredSize(new Dimension(200, 20));
        tfSchEndTime.setPreferredSize(new Dimension(200, 20));
        tfSchNonWorkingDates.setPreferredSize(new Dimension(200, 20));
        tfSchNonWorkingDays.setPreferredSize(new Dimension(200, 20));

        nextBtn.setAction(StartGui.getInstance().getActionManager().getMainFrameAction());
        fileBtn.setAction(StartGui.getInstance().getActionManager().getImportSchedule());
        placeBtn.setAction(StartGui.getInstance().getActionManager().getImportPlaces());

        nextBtn.setBackground(Color.CYAN);
        fileBtn.setBackground(Color.CYAN);
        placeBtn.setBackground(Color.CYAN);

        JPanel panelWest = new JPanel(new GridBagLayout());
        JPanel panelEast = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 5, 0, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelWest.add(new Label("Place"), gbc);
        gbc.gridx = 1;
        panelWest.add(tfPlace, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelWest.add(new Label("Time"), gbc);
        gbc.gridx = 1;
        panelWest.add(tfTime, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelWest.add(new Label("Start Date"), gbc);
        gbc.gridx = 1;
        panelWest.add(tfstartDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelWest.add(new Label("End Date"), gbc);
        gbc.gridx = 1;
        panelWest.add(tfendDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panelWest.add(new Label("Day"), gbc);
        gbc.gridx = 1;
        panelWest.add(tfDay, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panelWest.add(new Label("Date Format"), gbc);
        gbc.gridx = 1;
        panelWest.add(tfDateFormat, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panelWest.add(new Label("Day Format"), gbc);
        gbc.gridx = 1;
        panelWest.add(tfDayFormat, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panelWest.add(new Label("File"), gbc);
        gbc.gridx = 1;
        panelWest.add(fileBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        panelWest.add(new Label("Places"), gbc);
        gbc.gridx = 1;
        panelWest.add(placeBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelEast.add(new Label("Start Date"), gbc);
        gbc.gridx = 1;
        panelEast.add(tfSchStartDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelEast.add(new Label("End Date"), gbc);
        gbc.gridx = 1;
        panelEast.add(tfSchEndDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelEast.add(new Label("Start Time"), gbc);
        gbc.gridx = 1;
        panelEast.add(tfSchStartTime, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelEast.add(new Label("End Time"), gbc);
        gbc.gridx = 1;
        panelEast.add(tfSchEndTime, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panelEast.add(new Label("Non Working Dates"), gbc);
        gbc.gridx = 1;
        panelEast.add(tfSchNonWorkingDates, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panelEast.add(new Label("Non Working Days"), gbc);
        gbc.gridx = 1;
        panelEast.add(tfSchNonWorkingDays, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(80, 0, 0, 0);
        panelEast.add(nextBtn, gbc);

        this.setLayout(new GridLayout(1, 2));
        add(panelWest);
        add(panelEast);
    }
}
