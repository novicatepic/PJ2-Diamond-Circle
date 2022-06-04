package gui;

import game.Game;
import game.GameMatrix;
import pair.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    private JPanel mainPane;
    private JLabel lbWelcome;
    private JPanel matrixPanel;
    private JLabel[][] matrixLabels = new JLabel[GameMatrix.getMatrixDimensions()][GameMatrix.getMatrixDimensions()];
    private JButton[] playerButtons = new JButton[16];
    private JLabel currCardLabel;
    private JLabel cardDescLabel;
    private JLabel cardPicLabel;

    private Game game = new Game();

    public void setCardDescLabel(String text) {
        cardDescLabel.setText(text);
    }

    public void setCardPicLabel(String cardType) {
        if("special".equals(cardType)) {
            ImageIcon imageIcon = new ImageIcon("specialcard.jpg");
            cardPicLabel.setIcon(imageIcon);
        }
        else if("simple".equals(cardType)) {
            ImageIcon imageIcon = new ImageIcon("simplecard.jpg");
            cardPicLabel.setIcon(imageIcon);
        }
    }

    public void setCurrCardLabel(String text) {
        currCardLabel.setText(text);
    }

    public void setMatrixLabel(Color color, Pair pair, String newLabelString) {
        int xCoordinate = pair.getX();
        int yCoordinate = pair.getY();
        //matrixLabels[xCoordinate][yCoordinate].setBackground(color);
        matrixLabels[xCoordinate][yCoordinate].setForeground(color);
        matrixLabels[xCoordinate][yCoordinate].setText(newLabelString);
    }

    public void clearHoles() {
        for(int i = 0; i < GameMatrix.getMatrixDimensions(); i++) {
            for(int j = 0; j < GameMatrix.getMatrixDimensions(); j++) {
                if(matrixLabels[i][j].getText() != null && matrixLabels[i][j].getText().equals("H")) {
                    matrixLabels[i][j].setText(String.valueOf(i * GameMatrix.getMatrixDimensions() + j + 1));
                }
            }
        }
    }

    public MainFrame(Game game) {
        this.game = game;

        setTitle("Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 870, 686);
        mainPane = new JPanel();
        mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(mainPane);
        mainPane.setLayout(null);

        lbWelcome = new JLabel("");
        lbWelcome.setBounds(10, 93, 576, 33);
        mainPane.add(lbWelcome);

        JLabel labelGamesPlayed = new JLabel("New label");
        labelGamesPlayed.setBounds(34, 23, 150, 25);
        mainPane.add(labelGamesPlayed);

        JLabel labelDiamondCircle = new JLabel("DiamondCircle");
        labelDiamondCircle.setBackground(Color.ORANGE);
        labelDiamondCircle.setForeground(Color.RED);
        labelDiamondCircle.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        labelDiamondCircle.setHorizontalAlignment(SwingConstants.CENTER);
        labelDiamondCircle.setBounds(275, 10, 271, 38);
        mainPane.add(labelDiamondCircle);

        JButton startStopButton = new JButton("Pokreni / zaustavi");
        startStopButton.setBounds(628, 23, 136, 27);
        mainPane.add(startStopButton);

        startStopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(game.pause) {
                    game.pause = false;
                    try {
                        synchronized (game) {
                            game.notify();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    game.pause = true;
                }
            }
        });

        JPanel playerPanel = new JPanel();
        playerPanel.setBounds(25, 58, 821, 53);
        mainPane.add(playerPanel);
        playerPanel.setLayout(null);

        JLabel playerOneLabel = new JLabel("Igrac 1");
        playerOneLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerOneLabel.setBounds(131, 10, 75, 33);
        setFigureLabelColours(0, playerOneLabel);
        playerPanel.add(playerOneLabel);


        JLabel playerTwoLabel = new JLabel("Igrac 2");
        playerTwoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerTwoLabel.setBounds(280, 10, 75, 33);
        playerPanel.add(playerTwoLabel);
        setFigureLabelColours(1, playerTwoLabel);

        JLabel playerThreeLabel = new JLabel("Igrac 3");
        playerThreeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerThreeLabel.setBounds(470, 10, 75, 33);
        playerPanel.add(playerThreeLabel);
        if(GameMatrix.getNumberOfPlayers() == 2) {
            playerThreeLabel.setVisible(false);
        }
        else {
            setFigureLabelColours(2, playerThreeLabel);
        }

        JLabel playerFourLabel = new JLabel("Igrac 4");
        playerFourLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerFourLabel.setBounds(619, 10, 75, 33);
        playerPanel.add(playerFourLabel);
        if(GameMatrix.getNumberOfPlayers() == 2 || GameMatrix.getNumberOfPlayers() == 3) {
            playerFourLabel.setVisible(false);
        }
        else {
            setFigureLabelColours(3, playerFourLabel);
        }

        JPanel figurePanel = new JPanel();
        figurePanel.setBounds(10, 136, 105, 503);
        mainPane.add(figurePanel);
        figurePanel.setLayout(null);

        int initYValue = 10;
        for(int i = 0; i < playerButtons.length; i++) {
            int temp = i + 1;
            playerButtons[i] = new JButton("Figura" + temp);
            playerButtons[i].setBounds(10, initYValue, 85, 21);
            initYValue += 31;
            figurePanel.add(playerButtons[i]);
        }
        disablePlayerButtons(playerButtons[8], playerButtons[9], playerButtons[10], playerButtons[11], playerButtons[12], playerButtons[13], playerButtons[14], playerButtons[15]);

        currCardLabel = new JLabel("New label");
        currCardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currCardLabel.setBounds(125, 536, 584, 103);
        mainPane.add(currCardLabel);

        JPanel showFilesPanel = new JPanel();
        showFilesPanel.setBounds(719, 533, 127, 106);
        mainPane.add(showFilesPanel);
        showFilesPanel.setLayout(null);

        JButton showFilesButton = new JButton("Prikaz liste fajlova sa rezultatima");
        showFilesButton.setBounds(21, 10, 85, 86);
        showFilesPanel.add(showFilesButton);

        JPanel cardPanel = new JPanel();
        cardPanel.setBounds(719, 133, 127, 390);
        mainPane.add(cardPanel);
        cardPanel.setLayout(null);

        cardDescLabel = new JLabel("New label");
        cardDescLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardDescLabel.setBounds(10, 319, 107, 50);
        cardPanel.add(cardDescLabel);

        cardPicLabel = new JLabel("");
        cardPicLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardPicLabel.setBounds(10, 52, 107, 272);
        cardPanel.add(cardPicLabel);

        initializeConcreteLabel("Trenutna karta", 10, 20, 107, 22, cardPanel);
        initializeMatrix();

        buttonActionListener(0, 0, 0, game);
        buttonActionListener(1, 0, 1, game);
        buttonActionListener(2, 0, 2, game);
        buttonActionListener(3, 0, 3, game);
        buttonActionListener(4, 1, 0, game);
        buttonActionListener(5, 1, 1, game);
        buttonActionListener(6, 1, 2, game);
        buttonActionListener(7, 1, 3, game);

        if(playerButtons[8].isEnabled()) {
            buttonActionListener(8, 2, 0, game);
            buttonActionListener(9, 2, 0, game);
            buttonActionListener(10, 2, 0, game);
            buttonActionListener(11, 2, 0, game);
        }

        if(playerButtons[12].isEnabled()) {
            buttonActionListener(12, 2, 0, game);
            buttonActionListener(13, 2, 0, game);
            buttonActionListener(14, 2, 0, game);
            buttonActionListener(15, 2, 0, game);
        }

    }

    private void buttonActionListener(int x, int x1, int x2, Game game) {
        playerButtons[x].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PathForm pathForm = new PathForm(game.getRandomizedPlayers()[x1].getFigures()[x2].getPosition());
                pathForm.setVisible(true);
            }
        });
    }

    private void setFigureLabelColours(int x, JLabel playerOneLabel) {
        if(game.getRandomizedPlayers()[x].getFigures()[x].getColour().equals("blue")) {
            playerOneLabel.setForeground(Color.blue);
        }
        else if(game.getRandomizedPlayers()[x].getFigures()[x].getColour().equals("green")) {
            playerOneLabel.setForeground(Color.green);
        }
        else if(game.getRandomizedPlayers()[x].getFigures()[x].getColour().equals("red")) {
            playerOneLabel.setForeground(Color.red);
        }
        else {
            playerOneLabel.setForeground(Color.yellow);
        }
    }

    private void initializeConcreteLabel(String Trenutna_karta, int x, int y, int width, int height, JPanel cardPanel) {
        JLabel currentCardLabel = new JLabel(Trenutna_karta);
        currentCardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentCardLabel.setBounds(x, y, width, height);
        cardPanel.add(currentCardLabel);
    }

    private void initializeMatrix() {
        matrixPanel = new JPanel();
        matrixPanel.setBounds(125, 136, 584, 390);
        matrixPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        mainPane.add(matrixPanel);
        matrixPanel.setLayout(new GridLayout(GameMatrix.getMatrixDimensions(), GameMatrix.getMatrixDimensions()));
        for(int i = 0; i < GameMatrix.getMatrixDimensions(); i++) {
            for(int j = 0; j < GameMatrix.getMatrixDimensions(); j++) {
                matrixLabels[i][j] = new JLabel();
                String text = String.valueOf(i * GameMatrix.getMatrixDimensions() + j + 1);
                matrixLabels[i][j].setText(text);
                matrixPanel.add(matrixLabels[i][j]);
            }
        }
    }

    private void disablePlayerButtons(JButton figureOnePlayerThree, JButton figureTwoPlayerThree, JButton figureThreePlayerThree, JButton figureFourPlayerThree, JButton figureOnePlayerFour, JButton figureTwoPlayerFour, JButton figureThreePlayerFour, JButton figureFourPlayerFour) {
        switch (GameMatrix.getNumberOfPlayers()) {
            case 2:
                disableConcreteButtons(figureOnePlayerThree, figureTwoPlayerThree, figureThreePlayerThree, figureFourPlayerThree);
                disableConcreteButtons(figureOnePlayerFour, figureTwoPlayerFour, figureThreePlayerFour, figureFourPlayerFour);
                break;
            case 3:
                disableConcreteButtons(figureOnePlayerFour, figureTwoPlayerFour, figureThreePlayerFour, figureFourPlayerFour);
                break;
            default:
        }
    }

    private void disableConcreteButtons(JButton figureOnePlayerFour, JButton figureTwoPlayerFour, JButton figureThreePlayerFour, JButton figureFourPlayerFour) {
        figureOnePlayerFour.setEnabled(false);
        figureTwoPlayerFour.setEnabled(false);
        figureThreePlayerFour.setEnabled(false);
        figureFourPlayerFour.setEnabled(false);
    }



}