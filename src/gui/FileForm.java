package gui;

import game.Game;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileForm extends JFrame {

    private JPanel contentPane;
    private ArrayList<String> listOfFiles = new ArrayList<>();
    private static final String CURRENT_PATH = "./";
    private static Handler fileFormHandler;

    static {
        try {
            fileFormHandler = new FileHandler("fileform.log");
            Logger.getLogger(FileForm.class.getName()).addHandler(fileFormHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                        log(ex.fillInStackTrace());
                    }
                }
            });
        }
    }

    private int getNumberOfFilesOfGamesFinished() {
        int returnCounter = 0;
        File currentPath = new File(CURRENT_PATH);
        File[] files = currentPath.listFiles();
        try {
            for(File f : files) {
                if(f != null && !f.isDirectory() && f.getName().contains("IGRA")) {
                    listOfFiles.add(f.getName());
                    returnCounter++;
                }
            }
        } catch (NullPointerException nex) {
            log(nex.fillInStackTrace());
        }
        return returnCounter;
    }

    private void log(Throwable ex) {
        Logger.getLogger(FileForm.class.getName()).log(Level.WARNING, ex.toString());
    }
}
