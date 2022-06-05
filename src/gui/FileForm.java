package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileForm extends JFrame {

    private JPanel contentPane;
    private ArrayList<String> listOfFiles = new ArrayList<>();

    public FileForm() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 733, 584);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        int numberOfGames = getNumberOfFilesOfGamesFinished();
        contentPane.setLayout(new GridLayout(numberOfGames, 1));
        JButton[] buttons = new JButton[numberOfGames];
        for(int k = 0; k < buttons.length; k++) {
            buttons[k] = new JButton();
        }
        int i = 0;
        for(String fileString : listOfFiles) {
            if(fileString != null) {
                buttons[i].setText(fileString);
                contentPane.add(buttons[i]);
                i++;
            }
        }

        for(JButton button : buttons) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        File openFile = new File(button.getText());
                        Desktop.getDesktop().open(openFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    private int getNumberOfFilesOfGamesFinished() {
        int returnCounter = 0;
        File currentPath = new File("./");
        File[] files = currentPath.listFiles();
        for(File f : files) {
            if(f != null && !f.isDirectory() && f.getName().contains("IGRA")) {
                listOfFiles.add(f.getName());
                returnCounter++;
            }
        }
        return returnCounter;
    }
}
