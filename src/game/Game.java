package game;

import FigureInterface.IDroppable;
import FigureInterface.IFly;
import FigureInterface.SuperFast;
import bonus.Bonus;
import cards.Card;
import cards.Deck;
import cards.SimpleCard;
import cards.SpecialCard;
import figure.Figure;
import figure.GhostFigure;
import gui.RefreshingFormThread;
import gui.MainFrame;
import pair.Pair;
import player.Player;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.*;

public class Game {

    public static boolean pause = false;
    private final static GhostFigure ghostFigure = new GhostFigure();
    public static Logger logger;
    private boolean isItOverFlag = false;
    private Player[] randomizedPlayers;
    private static MainFrame mainFrame;
    private final String fileName = "./IGRA_";

    static {
        logger = Logger.getGlobal();
        try {
            FileHandler fileHandler = new FileHandler("GameLog.log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.setUseParentHandlers(false);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public Player[] getRandomizedPlayers() {
        return randomizedPlayers;
    }

    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static GhostFigure getGhostFigure() {return ghostFigure;}

    public static void main(String[] args) {
        Game game = new Game();
        try {
            new GameMatrix();
            game.randomizedPlayers = GameMatrix.randomizePlayers(GameMatrix.getPlayers());
            //GameMatrix.printMatrix();
            mainFrame = new MainFrame(game);
            mainFrame.setVisible(true);
            game.run();
            RefreshingFormThread.setIsOver();
        } catch (Exception ex) {
            log(ex);
        }
    }

    private boolean checkIfAllKeysAreEmpty(HashMap<Player, Integer> map, Player[] randomizedPlayers) {
        for (Player p : randomizedPlayers) {
            if (map.get(p) > 0) {
                return false;
            }
        }
        return true;
    }

    private int getWhichFigureIsPlayerPlayingWith(HashMap<Player, Integer> map, Player p) {
        int value = map.get(p);
        switch (value) {
            case 4:
                return 0;
            case 3:
                return 1;
            case 2:
                return 2;
            case 1:
                return 3;
            case 0:
            default:
                return -1;
        }
    }

    public void run() {
        long startTime = new Date().getTime();
        Deck deck = new Deck();
        Random random = new Random();
        boolean helpBool = true;
        HashMap<Player, Integer> map = new HashMap<>();
        HashMap<Figure, String> figureMap = new HashMap<>();
        initializeHelpCollections(randomizedPlayers, map, figureMap);
        int i = 0;
        try {
            while (!checkIfAllKeysAreEmpty(map, randomizedPlayers)) {
                while (i != GameMatrix.getNumberOfPlayers()) {
                    if (isItOverFlag) {
                        if (i != GameMatrix.getNumberOfPlayers() - 1) {
                            i++;
                            isItOverFlag = false;
                        } else {
                            ghostFigure.setGhostAlive();
                            ghostFigure.join();
                            break;
                        }
                    }
                    long figureTimeStart = new Date().getTime();
                    if (pause) {
                        synchronized (this) {
                            wait();
                        }
                    }
                    Player currentPlayer = randomizedPlayers[i];
                    if (getWhichFigureIsPlayerPlayingWith(map, currentPlayer) != -1) {
                        try {
                            int whichFigure = getWhichFigureIsPlayerPlayingWith(map, currentPlayer);
                            if (helpBool) {
                                ghostFigure.start();
                                helpBool = false;
                            }
                            Card card;
                            synchronized (GameMatrix.getMapTraversal()) {
                                synchronized (ghostFigure) {
                                    card = deck.pullOutACard();
                                }
                            }
                            mainFrame.clearHoles();
                            if (card instanceof SpecialCard) {
                                //System.out.println("SPECIAL");
                                String cardDescription = "SPECIAL";
                                mainFrame.setCardDescLabel(cardDescription);
                                mainFrame.setCardPicLabel("special", 0);
                                processSpecialCard(random, i, map, randomizedPlayers);
                                Thread.sleep(1000);
                            } else if (card instanceof SimpleCard) {
                                SimpleCard sCard = (SimpleCard) card;
                                String cardDescription = "SIMPLE: " + sCard.getNumberOfFieldsToCross();
                                mainFrame.setCardDescLabel(cardDescription);
                                mainFrame.setCardPicLabel("simple", sCard.getNumberOfFieldsToCross());
                                if (processSimpleCard(randomizedPlayers, map, figureMap, i, whichFigure, (SimpleCard) card)) {
                                    updateFigureTimeInThread(figureTimeStart, currentPlayer, whichFigure);
                                } else {
                                    updateFigureTimeInThread(figureTimeStart, currentPlayer, whichFigure);
                                }
                            }
                        } catch (InterruptedException ex) {
                            log(ex);
                        }
                    }
                }
            }
            if (ghostFigure.isAlive()) {
                try {
                    ghostFigure.join();
                } catch (InterruptedException ex) {
                    log(ex);
                }
            }
            try {
                writeToFiles(startTime, randomizedPlayers, figureMap);
            } catch (IOException ex) {
                log(ex);
            }
        } catch (Exception ex) {
            log(ex);
        }
    }

    private void updateFigureTimeInThread(long figureTimeStart, Player randomizedPlayers, int whichFigure) {
        long figureTimeFinish = new Date().getTime();
        long finalTime = figureTimeFinish - figureTimeStart;
        randomizedPlayers.getFigures()[whichFigure].setTime(
                randomizedPlayers.getFigures()[whichFigure].getTime() + finalTime);
    }

    private void initializeHelpCollections(Player[] randomizedPlayers, HashMap<Player, Integer> map, HashMap<Figure, String> figureMap) {
        for (Player p : randomizedPlayers) {
            for (Figure f : p.getFigures()) {
                figureMap.put(f, "");
            }
        }
        for (int i = 0; i < GameMatrix.getNumberOfPlayers(); i++) {
            Player p = randomizedPlayers[i];
            map.put(p, GameMatrix.NUMBER_OF_FIGURES);
        }
    }

    private boolean processSimpleCard(Player[] randomizedPlayers, HashMap<Player, Integer> map, HashMap<Figure,
            String> figureMap, int i, int whichFigure, SimpleCard card) throws InterruptedException {
        int positionToGoTo = card.getNumberOfFieldsToCross();
        int playerPosition;
        playerPosition = randomizedPlayers[i].getFigures()[whichFigure].getPosition();
        if (randomizedPlayers[i].getFigures()[whichFigure] instanceof SuperFast) {
            positionToGoTo *= 2;
        }
        int fullPosition = playerPosition + positionToGoTo + randomizedPlayers[i].getFigures()[whichFigure].getBonusCount();
        randomizedPlayers[i].getFigures()[whichFigure].setBonusCount(0);
        if (fullPosition >= GameMatrix.getMapTraversal().size()) fullPosition = GameMatrix.getMapTraversal().size() - 1;
        String labelText = "Na potezu je igrac " + i + "., Figura " + whichFigure + ", prelazi " + (fullPosition - playerPosition) + "" +
                "polja, pomjera se sa pozicije " + GameMatrix.getOriginalMap().get(playerPosition) + " na " +
                GameMatrix.getOriginalMap().get(fullPosition) + ".";
        MainFrame.setCurrCardLabel(labelText);


        for (int pos = playerPosition; pos <= fullPosition; pos++) {
            if (pause) {
                synchronized (this) {
                    wait();
                }
            }

            Pair realMatrixPosition = GameMatrix.getMatrixPositionOfElement(pos);
            Color realColour = randomizedPlayers[i].getFigures()[whichFigure].getRealColour();
            String newLabelString = randomizedPlayers[i].getFigures()[whichFigure].checkTypeOfFigure();
            if (pos == 0 && realMatrixPosition != null) {
                mainFrame.setMatrixLabel(realColour, realMatrixPosition, newLabelString);
            }
            if (pos > playerPosition) {
                int oldPos = pos - 1;
                GameMatrix.setMapTraversal(oldPos, null);
                Pair oldMatrixPosition = GameMatrix.getMatrixPositionOfElement(oldPos);
                if(oldMatrixPosition != null && realMatrixPosition != null) {
                    Integer element = (Integer) GameMatrix.getMATRIX()[oldMatrixPosition.getX()][oldMatrixPosition.getY()];
                    mainFrame.setMatrixLabel(Color.BLACK, oldMatrixPosition, String.valueOf(element));
                    mainFrame.setMatrixLabel(realColour, realMatrixPosition, newLabelString);
                }
            }

            if (pos < GameMatrix.getMapTraversal().size() && GameMatrix.getMapTraversal().get(pos) instanceof Bonus) {
                GameMatrix.setMapTraversal(pos, null);
                Pair pair = GameMatrix.getMatrixPositionOfElement(pos);
                if(pair != null) {
                    mainFrame.setBonusPickedUp(pair, randomizedPlayers[i].getFigures()[whichFigure]);
                }
                randomizedPlayers[i].getFigures()[whichFigure].setBonusCount(randomizedPlayers[i].getFigures()[whichFigure].getBonusCount() + 1);
                Thread.sleep(1000);
            }
            if (pos == GameMatrix.getMapTraversal().size() - 1) {
                GameMatrix.setMapTraversal(randomizedPlayers[i].getFigures()[whichFigure].getPosition(), null);
                map.put(randomizedPlayers[i], map.get(randomizedPlayers[i]) - 1);
                if (map.get(randomizedPlayers[i]) == 0) {
                    isItOverFlag = true;
                }
                String newString = figureMap.get(randomizedPlayers[i].getFigures()[whichFigure]) + "" +
                        GameMatrix.getOriginalMap().get(pos);
                figureMap.replace(randomizedPlayers[i].getFigures()[whichFigure], newString);
                randomizedPlayers[i].getFigures()[whichFigure].setPosition(pos + 1);

                Pair oldMatrixPosition = GameMatrix.getMatrixPositionOfElement(GameMatrix.getOriginalMap().size() - 1);
                if(oldMatrixPosition != null) {
                    Integer element = (Integer) GameMatrix.getMATRIX()[oldMatrixPosition.getX()][oldMatrixPosition.getY()];
                    mainFrame.setMatrixLabel(Color.MAGENTA, oldMatrixPosition, String.valueOf(element));
                    Thread.sleep(1000);
                    mainFrame.setMatrixLabel(Color.BLACK, oldMatrixPosition, String.valueOf(element));
                }
                return true;
            }
            if (pos < GameMatrix.getMapTraversal().size()) {
                if (pos != GameMatrix.getMapTraversal().size() - 1 /*&& (pos == fullPosition || pos == 0)*/) {
                    if (!figureMap.get(randomizedPlayers[i].getFigures()[whichFigure]).contains(String.valueOf(GameMatrix.getOriginalMap().get(pos)))) {
                        String newString = figureMap.get(randomizedPlayers[i].getFigures()[whichFigure]) + "" +
                                GameMatrix.getOriginalMap().get(pos) + "-";
                        figureMap.replace(randomizedPlayers[i].getFigures()[whichFigure], newString);
                    }
                }
                GameMatrix.setMapTraversal(pos, randomizedPlayers[i].getFigures()[whichFigure]);
                randomizedPlayers[i].getFigures()[whichFigure].setPosition(pos);
                Thread.sleep(1000);
            }
        }
        return false;
    }

    private void processSpecialCard(Random random, int i, HashMap<Player, Integer> map, Player[] randomizedPlayers) {
        int generatedNumberOfHoles = generateNumberOfHoles();
        List<Integer> holePositions = new ArrayList<>();
        int temp = generatedNumberOfHoles;

        synchronized (mainFrame) {
            synchronized (GameMatrix.getMapTraversal()) {
                while (temp > 0) {
                    int position = random.nextInt(GameMatrix.getMapTraversal().size() - 1) + 1;
                    if (!(GameMatrix.getMapTraversal().get(position) instanceof Bonus) &&
                            position != GameMatrix.getMapTraversal().size() - 1 &&
                            !holePositions.contains(position)) {
                        holePositions.add(position);
                    }
                    temp--;
                }

                for (int k = 0; k < holePositions.size(); k++) {
                    Pair oldMatrixPosition = GameMatrix.getMatrixPositionOfElement(holePositions.get(k));
                    if (!(GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof Bonus) && oldMatrixPosition != null) {
                        mainFrame.setMatrixLabel(Color.BLACK, oldMatrixPosition, "H");
                    } /*else {
                        if(oldMatrixPosition != null) {
                            mainFrame.eatBonus(oldMatrixPosition);
                            mainFrame.setMatrixLabel(Color.BLACK, oldMatrixPosition, "H");
                            GameMatrix.setMapTraversal(holePositions.get(k), null);
                        }
                    }*/

                    //if (!(GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof Bonus)) {
                        if (!(GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof IFly)) {
                            if (GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof IDroppable) {
                                GameMatrix.setMapTraversal(holePositions.get(k), null);
                                map.put(randomizedPlayers[i], map.get(randomizedPlayers[i]) - 1);
                                if (map.get(randomizedPlayers[i]) <= 0) {
                                    isItOverFlag = true;
                                }
                            }
                        }
                    //}
                }
            }
        }
    }

    private void writeToFiles(long startTime, Player[] randomizedPlayers, HashMap<Figure, String> figureMap) throws IOException {
        int outerCounter = 0;
        int innerCounter = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS");
        Date date = new Date();
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                fileName + sdf.format(date))));
        for (Player p : randomizedPlayers) {
            for (Figure f : p.getFigures()) {
                String helpString;
                if (f.didFigureFinish()) {
                    helpString = "DA";
                } else {
                    helpString = "NE";
                }
                pw.println("Igrac " + (outerCounter + 1) + " - " + p.getName() + "\n" +
                        "Figura " + innerCounter++ + " (" + f.checkTypeOfFigure() + ", " + f.getColour() + ") - " +
                        "predjeni put (" + figureMap.get(f) + ") - stigla do cilja " + helpString + "\n" +
                        "Ukupno vrijeme igranja sa figurom: " + f.getTime() / (double) 1000 + "s\n");
            }
            outerCounter++;
        }
        pw.println((new Date().getTime() - startTime) / (double) 1000 + "s");
        pw.close();
    }

    private int generateNumberOfHoles() {
        Random random = new Random();
        return random.nextInt(GameMatrix.NUMBER_OF_HOLES) + 1;
    }

    public static void log(Exception ex) {
        logger.log(Level.WARNING, ex.fillInStackTrace().toString());
    }
}