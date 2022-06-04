package gui;

import game.GameMatrix;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class PathForm extends JFrame {
    private int position = 0;
    private ArrayList<Integer> integersToPrint = new ArrayList<>();
    private JPanel contentPane;
    private JLabel matrixLabels[][] = new JLabel[GameMatrix.getMatrixDimensions()][GameMatrix.getMatrixDimensions()];

    public PathForm(int elem) {
        /*for(Object element : GameMatrix.getOriginalMap()) {
            if((Integer)element != elem) {
                this.position++;
            }
            else {
                break;
            }
        }*/
        position = elem;
        for(int i = 0; i < position; i++) {
            integersToPrint.add((Integer)GameMatrix.getOriginalMap().get(i));
        }
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBounds(100, 100, 660, 573);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new GridLayout(GameMatrix.getMatrixDimensions(), GameMatrix.getMatrixDimensions()));
        for(int i = 0; i < GameMatrix.getMatrixDimensions(); i++) {
            for(int j = 0; j < GameMatrix.getMatrixDimensions(); j++) {
                matrixLabels[i][j] = new JLabel();
                String text = String.valueOf(i * GameMatrix.getMatrixDimensions() + j + 1);
                matrixLabels[i][j].setText(text);
                contentPane.add(matrixLabels[i][j]);
            }
        }
        printPath();
    }

    private void printPath() {
        for(int i = 0; i < GameMatrix.getMatrixDimensions(); i++) {
            for(int j = 0; j < GameMatrix.getMatrixDimensions(); j++) {
                if(integersToPrint.contains(Integer.parseInt(matrixLabels[i][j].getText()))) {
                    matrixLabels[i][j].setForeground(Color.BLUE);
                }
                else {
                    matrixLabels[i][j].setForeground(Color.WHITE);
                }
            }
        }
    }
}
