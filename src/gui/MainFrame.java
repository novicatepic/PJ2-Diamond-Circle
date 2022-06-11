package gui;

import figure.Figure;
import game.Game;
import game.GameMatrix;
import pair.Pair;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {
    private final JPanel mainPane;
    private static final JLabel[][] matrixLabels = new JLabel[GameMatrix.getMatrixDimensions()][GameMatrix.getMatrixDimensions()];
    private final JButton[] playerButtons = new JButton[16];
    private static JLabel currCardLabel;
    private final JLabel cardDescLabel;
    private final JLabel cardPicLabel;
    private int gamesPlayed = 0;
    private final static JLabel labelGamesPlayed = new JLabel();
    private final JPanel[] squarePanels = new JPanel[4];
    private final Game game;
    private static JLabel timePlayedLabel;
    private final RefreshingFormThread refreshingFormThread = new RefreshingFormThread();
    private final String workingDirectory = "./";

    public static JLabel getCurrCardLabel() {
        return currCardLabel;
    }

    public static void setCurrCardLabel(String currCardLabelText) {
        MainFrame.currCardLabel.setText(currCardLabelText);
    }

    public static void setTimePlayedLabel(String timePlayedLabelText) {
        MainFrame.timePlayedLabel.setText(timePlayedLabelText);
    }

    public void setGamesPlayed() {
        File currentPath = new File(workingDirectory);
        try {
            File[] files = currentPath.listFiles();
            if(files != null) {
                for (File f : files) {
                    if (!f.isDirectory() && f.getName().contains("IGRA")) {
                        gamesPlayed++;
                    }
                }
                labelGamesPlayed.setText(gamesPlayed + " odigranih igara!");
            }
        } catch (NullPointerException ex) {
            Game.log(ex);
        }

    }

    public void setCardDescLabel(String text) {
        cardDescLabel.setText(text);
    }

    public static boolean checkIfFieldIsBlack(int position) {
        Pair pair = GameMatrix.getMatrixPositionOfElement(position);
        int xCoordinate = - 1, yCoordinate = -1;
        if(pair != null) {
            xCoordinate = pair.getX();
            yCoordinate = pair.getY();
        }


        return matrixLabels[xCoordinate][yCoordinate].getBackground() == Color.BLACK;
    }

    public void setCardPicLabel(String cardType, int value) {
        for (JPanel panel : squarePanels) {
            panel.setBackground(Color.WHITE);
        }

        if ("special".equals(cardType)) {
            shrinkingAndPuttingImage("specialcard.jpg");
        } else if ("simple".equals(cardType)) {
            shrinkingAndPuttingImage("simplecard.jpg");
        }

        for (int i = 0; i < value; i++) {
            squarePanels[i].setBackground(Color.BLACK);
        }
    }

    private void shrinkingAndPuttingImage(String imageName) {
        BufferedImage img;
        try {
            img = ImageIO.read(new File(imageName));
            Image dimg = img.getScaledInstance(cardPicLabel.getWidth(), cardPicLabel.getHeight(), Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(dimg);
            cardPicLabel.setIcon(imageIcon);
        } catch (IOException e) {
            Game.log(e);
        }
    }

    public static void setBonusLabel(int position) {
        Pair pair = GameMatrix.getMatrixPositionOfElement(position);
        int xCoordinate = -1, yCoordinate = -1;
        if(pair != null) {
            xCoordinate = pair.getX();
            yCoordinate = pair.getY();
        }


        try {
            BufferedImage img = ImageIO.read(new File("diamond.png"));
            Image dimg = img.getScaledInstance(matrixLabels[xCoordinate][yCoordinate].getWidth(),
                    matrixLabels[xCoordinate][yCoordinate].getHeight(), Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(dimg);
            matrixLabels[xCoordinate][yCoordinate].setIcon(imageIcon);
        } catch (IOException e) {
            Game.log(e);
        }
    }

    public void setBonusPickedUp(Pair pair, Figure figure) {
        int xCoordinate = pair.getX();
        int yCoordinate = pair.getY();
        matrixLabels[xCoordinate][yCoordinate].setIcon(null);
        matrixLabels[xCoordinate][yCoordinate].setText(figure.checkTypeOfFigure());
        matrixLabels[xCoordinate][yCoordinate].setBackground(Color.WHITE);
        matrixLabels[xCoordinate][yCoordinate].setForeground(figure.getRealColour());
    }

    public static void clearBonuses() {
        for (int i = 0; i < GameMatrix.getMatrixDimensions(); i++) {
            for (int j = 0; j < GameMatrix.getMatrixDimensions(); j++) {
                if (matrixLabels[i][j].getIcon() != null) {
                    matrixLabels[i][j].setIcon(null);
                    matrixLabels[i][j].setText(String.valueOf(GameMatrix.getMATRIX()[i][j]));
                    matrixLabels[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }

    public void setMatrixLabel(Color color, Pair pair, String newLabelString) {
        int xCoordinate = pair.getX();
        int yCoordinate = pair.getY();
        //matrixLabels[xCoordinate][yCoordinate].setBackground(color);
        if ("H".equals(newLabelString)) {
            matrixLabels[xCoordinate][yCoordinate].setBackground(color);
            if (!"FF".equals(matrixLabels[xCoordinate][yCoordinate].getText())) {
                matrixLabels[xCoordinate][yCoordinate].setForeground(Color.BLACK);
                matrixLabels[xCoordinate][yCoordinate].setText(String.valueOf(GameMatrix.getMATRIX()[xCoordinate][yCoordinate]));
            }
        } else {
            matrixLabels[xCoordinate][yCoordinate].setForeground(color);
            matrixLabels[xCoordinate][yCoordinate].setText(newLabelString);
        }
    }

    public void clearHoles() {
        for (int i = 0; i < GameMatrix.getMatrixDimensions(); i++) {
            for (int j = 0; j < GameMatrix.getMatrixDimensions(); j++) {
                if (matrixLabels[i][j].getIcon() == null) {
                    matrixLabels[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }

    public void eatBonus(Pair pair) {
        matrixLabels[pair.getX()][pair.getY()].setIcon(null);
        matrixLabels[pair.getX()][pair.getY()].setText(String.valueOf(GameMatrix.getMATRIX()[pair.getX()][pair.getY()]));
        matrixLabels[pair.getX()][pair.getY()].setBackground(Color.BLACK);
    }

    public MainFrame(Game game) {
        this.game = game;
        setGamesPlayed();

        final JLabel lbWelcome;

        setTitle("DIAMOND CIRCLE GAME");
        //was exit on close
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

        //labelGamesPlayed = new JLabel();
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
        startStopButton.setBounds(710, 22, 136, 27);
        mainPane.add(startStopButton);

        startStopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Game.pause) {
                    Game.pause = false;
                    try {
                        synchronized (game) {
                            synchronized (Game.getGhostFigure()) {
                                Game.getGhostFigure().notify();
                                game.notify();
                            }
                        }
                    } catch (Exception ex) {
                        Game.log(ex);
                    }
                } else {
                    Game.pause = true;
                }
            }
        });

        JPanel playerPanel = new JPanel();
        playerPanel.setBounds(25, 58, 821, 53);
        mainPane.add(playerPanel);
        playerPanel.setLayout(null);

        JLabel playerOneLabel = new JLabel(game.getRandomizedPlayers()[0].getName());
        playerOneLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerOneLabel.setBounds(131, 10, 75, 33);
        setFigureLabelColours(0, playerOneLabel);
        playerPanel.add(playerOneLabel);


        JLabel playerTwoLabel = new JLabel(game.getRandomizedPlayers()[1].getName());
        playerTwoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerTwoLabel.setBounds(280, 10, 75, 33);
        playerPanel.add(playerTwoLabel);
        setFigureLabelColours(1, playerTwoLabel);

        if(GameMatrix.getNumberOfPlayers() > 2) {
            JLabel playerThreeLabel = new JLabel(game.getRandomizedPlayers()[2].getName());
            playerThreeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            playerThreeLabel.setBounds(470, 10, 75, 33);
            playerPanel.add(playerThreeLabel);
            setFigureLabelColours(2, playerThreeLabel);
        }

        if(GameMatrix.getNumberOfPlayers() == 4) {
            JLabel playerFourLabel = new JLabel(game.getRandomizedPlayers()[3].getName());
            playerFourLabel.setHorizontalAlignment(SwingConstants.CENTER);
            playerFourLabel.setBounds(619, 10, 75, 33);
            playerPanel.add(playerFourLabel);
            setFigureLabelColours(3, playerFourLabel);
        }

        JPanel figurePanel = new JPanel();
        figurePanel.setBounds(10, 136, 105, 503);
        mainPane.add(figurePanel);
        figurePanel.setLayout(null);

        int initYValue = 10;
        for (int i = 0; i < playerButtons.length; i++) {
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

        JButton showFilesButton = new JButton("FAJLOVI");
        showFilesButton.setBounds(21, 10, 85, 86);
        showFilesPanel.add(showFilesButton);

        showFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileForm fileForm = new FileForm();
                fileForm.setVisible(true);
            }
        });

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
        cardPicLabel.setBounds(10, 52, 107, 139);
        cardPanel.add(cardPicLabel);

        initializeConcreteLabel(cardPanel);
        initializeMatrix();

        buttonActionListener(0, 0, 0, game);
        buttonActionListener(1, 0, 1, game);
        buttonActionListener(2, 0, 2, game);
        buttonActionListener(3, 0, 3, game);
        buttonActionListener(4, 1, 0, game);
        buttonActionListener(5, 1, 1, game);
        buttonActionListener(6, 1, 2, game);
        buttonActionListener(7, 1, 3, game);

        if (playerButtons[8].isEnabled()) {
            buttonActionListener(8, 2, 0, game);
            buttonActionListener(9, 2, 0, game);
            buttonActionListener(10, 2, 0, game);
            buttonActionListener(11, 2, 0, game);
        }

        if (playerButtons[12].isEnabled()) {
            buttonActionListener(12, 2, 0, game);
            buttonActionListener(13, 2, 0, game);
            buttonActionListener(14, 2, 0, game);
            buttonActionListener(15, 2, 0, game);
        }

        timePlayedLabel = new JLabel();
        timePlayedLabel.setText("0");
        timePlayedLabel.setBounds(516, 10, 184, 32);
        mainPane.add(timePlayedLabel);

        refreshingFormThread.start();

        JPanel panel = new JPanel();
        panel.setBounds(10, 201, 38, 46);
        cardPanel.add(panel);
        squarePanels[0] = panel;
        JPanel panel_1 = new JPanel();
        panel_1.setBounds(68, 201, 38, 46);
        cardPanel.add(panel_1);
        squarePanels[1] = panel_1;
        JPanel panel_2 = new JPanel();
        panel_2.setBounds(10, 257, 38, 46);
        cardPanel.add(panel_2);
        squarePanels[2] = panel_2;
        JPanel panel_3 = new JPanel();
        panel_3.setBounds(68, 257, 38, 46);
        cardPanel.add(panel_3);
        squarePanels[3] = panel_3;

        for (int i = 0; i < matrixLabels.length; i++) {
            for (int j = 0; j < matrixLabels.length; j++) {
                matrixLabels[i][j].setOpaque(true);
                matrixLabels[i][j].setBackground(Color.WHITE);
            }
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
        if (game.getRandomizedPlayers()[x].getFigures()[x].getColour().equals("blue")) {
            playerOneLabel.setForeground(Color.blue);
        } else if (game.getRandomizedPlayers()[x].getFigures()[x].getColour().equals("green")) {
            playerOneLabel.setForeground(Color.green);
        } else if (game.getRandomizedPlayers()[x].getFigures()[x].getColour().equals("red")) {
            playerOneLabel.setForeground(Color.red);
        } else {
            playerOneLabel.setForeground(Color.yellow);
        }
    }

    private void initializeConcreteLabel(JPanel cardPanel) {
        JLabel currentCardLabel = new JLabel("Trenutna karta");
        currentCardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentCardLabel.setBounds(10, 20, 107, 22);
        cardPanel.add(currentCardLabel);
    }

    private void initializeMatrix() {
        JPanel matrixPanel;
        matrixPanel = new JPanel();
        matrixPanel.setBounds(125, 136, 584, 390);
        matrixPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        mainPane.add(matrixPanel);
        matrixPanel.setLayout(new GridLayout(GameMatrix.getMatrixDimensions(), GameMatrix.getMatrixDimensions()));
        for (int i = 0; i < GameMatrix.getMatrixDimensions(); i++) {
            for (int j = 0; j < GameMatrix.getMatrixDimensions(); j++) {
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
