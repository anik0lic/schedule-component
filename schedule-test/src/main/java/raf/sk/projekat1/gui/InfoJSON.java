package raf.sk.projekat1.gui;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;


@Getter
@Setter
public class InfoJSON extends JFrame {

    JLabel dayFLabel = new JLabel("Day Format");
    JLabel dateFLabel = new JLabel("Date Format");
    JLabel fExpLabel = new JLabel("File");
    public JTextField dayFTF = new JTextField(20);
    JTextField dateFTF = new JTextField(20);


    JFileChooser jfc = new JFileChooser();

    private JButton dugmeJfc = new JButton("Select a file");
    private JButton dugme = new JButton("Next");

    public InfoJSON(Frame owner) throws HeadlessException {



        dugmeJfc.setBackground(Color.CYAN);

        dugme.setBackground(Color.CYAN);
        dugme.setAction(StartGui.getInstance().getActionManager().getMainFrameAction());
        dugmeJfc.setAction(StartGui.getInstance().getActionManager().getImportSchedule());



//        JFrame frame = new JFrame("Json Presets");
        setTitle("Json Presets");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        setSize((int) (screenWidth / 4), (int) (screenHeight / 4));
        setLocationRelativeTo(null);

        JPanel newPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;

        constraints.insets = new Insets(10, 0, 0, 10);



        // add components to the panel
        constraints.gridx = 0;
        constraints.gridy = 0;
        newPanel.add(dayFLabel, constraints);

        constraints.gridx = 1;
        newPanel.add(dayFTF, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        newPanel.add(dateFLabel, constraints);

        constraints.gridx = 1;
        newPanel.add(dateFTF, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        newPanel.add(fExpLabel, constraints);

        constraints.gridx = 1;

        newPanel.add(dugmeJfc, constraints);




        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(70, 0, 0, 10);
        newPanel.add(dugme, constraints);





        add(newPanel);



    }
}
