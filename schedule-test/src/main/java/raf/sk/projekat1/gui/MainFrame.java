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

    private ScheduleService ss;
    private JComboBox comboBox;
    private DefaultTableModel model;
    private JTable table;
    private JScrollPane scrollPane;
    private JTextArea textArea;

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

        model = new DefaultTableModel();
        table = new JTable(model);
        scrollPane = new JScrollPane(table);

        model.addColumn("Termini");

        updateTable();

        panelForTable.add(scrollPane);

        JPanel menu = new JPanel();
        menu.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 0, 5);

        String[] boxItems = {"Add", "Remove", "Update", "Search", "Check"};
        comboBox = new JComboBox(boxItems);

        textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(450, 100));

        JButton runBtn = new JButton("Run");
        runBtn.setAction(StartGui.getInstance().getActionManager().getRunAction());

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

    public void updateTable(){
        model.getDataVector().removeAllElements();
        for(String s : ss.printAppointments(ss.getSchedule().getAppointments())){
            model.addRow(new Object[]{s});
        }
    }

}
