package raf.sk.projekat1.gui;

import lombok.Getter;
import lombok.Setter;
import raf.sk.projekat1.model.Appointment;
import raf.sk.projekat1.specification.ScheduleService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class MainFrame extends JFrame {

    ScheduleService ss;

    public MainFrame(Frame owner, ScheduleService ss) throws HeadlessException {
        this.ss = ss;

        setTitle("Schedule");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        setSize((int) (screenWidth / 2), (int) (screenHeight / 2));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(owner);

        JPanel panelForTable = new JPanel();

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        model.addColumn("Termini");

        for(Appointment a : ss.getSchedule().getAppointments()){
            //staviti da printAppointments vraca listu stringova i onda samo stavljati u row te stringove
            String result = ss.getSchedule().getInfo().getDayFormat().get(a.getStartDate().getDayOfWeek().getValue()-1);
            result += ", " + a.getStartDate().format(DateTimeFormatter.ofPattern(ss.getSchedule().getInfo().getDateFormat()));
            result += " " + a.getStartTime() + "-" + a.getEndTime() + ", " + a.getPlace().getName();
            model.addRow(new Object[]{result});
        }

        panelForTable.add(scrollPane);

        JPanel menu = new JPanel();
        menu.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 0, 5);

        String[] boxItems = {"Add", "Remove", "Update", "Search", "Check"};
        JComboBox comboBox = new JComboBox(boxItems);

        JTextArea textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(300, 300));

        JButton runBtn = new JButton("Run");

        gbc.gridx = 0;
        gbc.gridy = 0;
        menu.add(comboBox, gbc);
        gbc.gridy = 1;
        menu.add(textArea, gbc);
        gbc.gridy = 2;
        menu.add(runBtn, gbc);

        JButton exitBtn = new JButton("Exit");

        add(panelForTable, BorderLayout.WEST);
        add(menu, BorderLayout.EAST);
        add(exitBtn, BorderLayout.SOUTH);
    }

}
