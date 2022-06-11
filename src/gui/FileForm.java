package gui;
import game.Game;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileForm extends JFrame {
    private final ArrayList<String> listOfFiles = new ArrayList<>();
    private static final String CURRENT_PATH = "./";
    private final JButton[] buttons;

    public FileForm() {
        final JPanel contentPane;
        final JTable table;
        final JScrollPane scrollPane;
        final String[] columns;
        final Object[][] data;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 733, 584);
        setTitle("FILES");
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        getContentPane().add(contentPane);

        int numberOfGames = getNumberOfFilesOfGamesFinished();
        buttons = new JButton[numberOfGames];
        for(int k = 0; k < buttons.length; k++) {
            buttons[k] = new JButton();
        }
        int i = 0;
        for(String fileString : listOfFiles) {
            if(fileString != null) {
                buttons[i].setText(fileString);
                i++;
            }
        }

        columns = new String[] {"RB", "NAZIV_FAJLA", "KLIK"};

        data = new String[numberOfGames][3];
        for(int k = 0; k < numberOfGames; k++) {
            data[k][0] = "File " + (k + 1);
        }
        for(int k = 0; k < numberOfGames; k++) {
            data[k][1] = buttons[k].getText();
        }

        DefaultTableModel model = new DefaultTableModel(data, columns);
        table = new JTable();
        table.setModel(model);
        table.getColumn("KLIK").setCellRenderer(new ButtonRenderer());
        table.getColumn("KLIK").setCellEditor(new ButtonEditor(new JCheckBox()));
        scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);


        for(JButton button : buttons) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        File openFile = new File(button.getText());
                        Desktop.getDesktop().open(openFile);

                    } catch (IOException ex) {
                        Game.log(ex);
                    }
                }
            });
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(buttons[row].getText());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        public ButtonEditor(JCheckBox c) {
            super(c);
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return buttons[row];
        }
    }

    private int getNumberOfFilesOfGamesFinished() {
        int returnCounter = 0;
        File currentPath = new File(CURRENT_PATH);
        File[] files = currentPath.listFiles();
        try {
            if(files != null) {
                for(File f : files) {
                    if(f != null && !f.isDirectory() && f.getName().contains("IGRA")) {
                        listOfFiles.add(f.getName());
                        returnCounter++;
                    }
                }
            }
        } catch (NullPointerException nex) {
            Game.log(nex);
        }
        return returnCounter;
    }
}
