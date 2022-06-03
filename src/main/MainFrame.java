package main;

import player.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFrame extends JFrame {
    private final JLabel decriptionLabel;
    /*private final JButton figureThreePlayerOne;
            private final JButton figureFourPlayerOne;
            private final JButton figureOnePlayerTwoo;
            private final JButton figureTwoPlayerTwo;
            private final JButton figureThreePlayerTwo;
            private final JButton figureFourPlayerTwo;
            private final JButton figureOnePlayerThree;
            private final JButton figureTwoPlayerThree;
            private final JButton figureThreePlayerThree;
            private final JButton figureFourPlayerThree;
            private final JButton figureOnePlayerFour;
            private final JButton figureTwoPlayerFour;
            private final JButton figureThreePlayerFour;
            private final JButton figureFourPlayerFour;*/
    private JPanel mainPane;
    private JLabel lbWelcome;
    private JPanel matrixPanel;
    private static int go = 0;
    /*private JButton figure1Player1;
    private JButton figureTwoPlayerOne;*/

    private Game game;

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
        //frame.matrixPanel.
        frame.game.start();

        while (true) {
            if(frame.game.currentPlayer != null) {
                Timer timer = new Timer(1000, e -> frame.decriptionLabel.setText(/*"Na potezu je igrac " + */frame.game.currentPlayer.getName()/* + ", Figura " + "" +
                        + frame.game.currentFigureNumber + ", prelazi " + frame.game.positionToGoTo + " polja")*/));
                timer.setRepeats(true);
                timer.start();
            }
        }


    }

    public MainFrame() {
        try {
            new GameMatrix();
        } catch(Exception e1) {
            Logger.getLogger(Game.class.getName()).log(Level.WARNING, e1.fillInStackTrace().toString());
        }
        game = new Game();
        int numOfPlayers = GameMatrix.getNumberOfPlayers();
        game.randomizedPlayers = new Player[numOfPlayers];
        game.randomizedPlayers = GameMatrix.randomizePlayers(GameMatrix.getPlayers());

        setTitle("Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        JButton figure1Player1 = new JButton("Figura 1");
        figure1Player1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        figure1Player1.setBounds(10, 10, 85, 21);
        figurePanel.add(figure1Player1);

        JButton figureTwoPlayerOne = new JButton("Figura 2");
        figureTwoPlayerOne.setBounds(10, 41, 85, 21);
        figurePanel.add(figureTwoPlayerOne);

        JButton figureThreePlayerOne = new JButton("Figura 3");
        figureThreePlayerOne.setBounds(10, 72, 85, 21);
        figurePanel.add(figureThreePlayerOne);

        JButton figureFourPlayerOne = new JButton("Figura 4");
        figureFourPlayerOne.setBounds(10, 100, 85, 21);
        figurePanel.add(figureFourPlayerOne);

        JButton figureOnePlayerTwoo = new JButton("Figura 5");
        figureOnePlayerTwoo.setBounds(10, 131, 85, 21);
        figurePanel.add(figureOnePlayerTwoo);

        JButton figureTwoPlayerTwo = new JButton("Figura 6");
        figureTwoPlayerTwo.setBounds(10, 162, 85, 21);
        figurePanel.add(figureTwoPlayerTwo);

        JButton figureThreePlayerTwo = new JButton("Figura 7");
        figureThreePlayerTwo.setBounds(10, 193, 85, 21);
        figurePanel.add(figureThreePlayerTwo);

        JButton figureFourPlayerTwo = new JButton("Figura 8");
        figureFourPlayerTwo.setBounds(10, 224, 85, 21);
        figurePanel.add(figureFourPlayerTwo);

        JButton figureOnePlayerThree = new JButton("Figura 9");
        figureOnePlayerThree.setBounds(10, 255, 85, 21);
        figurePanel.add(figureOnePlayerThree);

        JButton figureTwoPlayerThree = new JButton("Figura10");
        figureTwoPlayerThree.setBounds(10, 286, 85, 21);
        figurePanel.add(figureTwoPlayerThree);

        JButton figureThreePlayerThree = new JButton("Figura11");
        figureThreePlayerThree.setBounds(10, 317, 85, 21);
        figurePanel.add(figureThreePlayerThree);

        JButton figureFourPlayerThree = new JButton("Figura12");
        figureFourPlayerThree.setBounds(10, 348, 85, 21);
        figurePanel.add(figureFourPlayerThree);

        JButton figureOnePlayerFour = new JButton("Figura13");
        figureOnePlayerFour.setBounds(10, 378, 85, 21);
        figurePanel.add(figureOnePlayerFour);

        JButton figureTwoPlayerFour = new JButton("Figura14");
        figureTwoPlayerFour.setBounds(10, 409, 85, 21);
        figurePanel.add(figureTwoPlayerFour);

        JButton figureThreePlayerFour = new JButton("Figura15");
        figureThreePlayerFour.setBounds(10, 440, 85, 21);
        figurePanel.add(figureThreePlayerFour);

        JButton figureFourPlayerFour = new JButton("Figura16");
        figureFourPlayerFour.setBounds(10, 471, 85, 21);
        figurePanel.add(figureFourPlayerFour);

        JPanel cardDescriptionPanel = new JPanel();
        cardDescriptionPanel.setBounds(125, 533, 584, 106);
        mainPane.add(cardDescriptionPanel);
        cardDescriptionPanel.setLayout(null);

        JLabel cardDescriptionLabel = new JLabel("Opis znacenja karte:");
        cardDescriptionLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        cardDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardDescriptionLabel.setBounds(184, 10, 243, 37);
        cardDescriptionPanel.add(cardDescriptionLabel);

        decriptionLabel = new JLabel("New label");
        decriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        decriptionLabel.setBounds(232, 57, 138, 39);
        cardDescriptionPanel.add(decriptionLabel);

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

        initializeConcreteLabel("Trenutna karta", 10, 20, 107, 22, cardPanel);

        initializeMatrix();

        disablePlayerButtons(figureOnePlayerThree, figureTwoPlayerThree, figureThreePlayerThree, figureFourPlayerThree, figureOnePlayerFour, figureTwoPlayerFour, figureThreePlayerFour, figureFourPlayerFour);
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
        mainPane.add(matrixPanel);
        matrixPanel.setLayout(new GridLayout(GameMatrix.getMatrixDimensions(), GameMatrix.getMatrixDimensions()));
        for(int i = 0; i < GameMatrix.getMatrixDimensions() * GameMatrix.getMatrixDimensions(); i++) {
            JLabel l = new JLabel();
            l.setText(String.valueOf(i + 1));
            matrixPanel.add(l);
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
