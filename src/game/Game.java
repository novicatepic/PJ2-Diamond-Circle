package game;

import FigureInterface.IDroppable;
import FigureInterface.IFly;
import FigureInterface.SuperFast;
import bonus.Bonus;
import cards.Card;
import cards.Deck;
import cards.SimpleCard;
import cards.SpecialCard;
import figure.GhostFigure;
import gui.RefreshingForm;
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
    private Player[] randomizedPlayers;
    private boolean isItOverFlag = false;
    private static MainFrame mainFrame;
    private final String fileName = "./IGRA_";
    private final static Logger logger;
    private boolean isEaten = false;

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

    public static GhostFigure getGhostFigure() {
        return ghostFigure;
    }

    public static void main(String[] args) {
        Game game = new Game();
        try {
            new GameMatrix();
            game.randomizedPlayers = GameMatrix.randomizePlayers(GameMatrix.getPlayers());
            mainFrame = new MainFrame(game);
            mainFrame.setVisible(true);
            game.playGame();
        } catch (Exception ex) {
            log(ex);
        }
    }

    private boolean checkIfAllKeysAreEmpty(HashMap<Player, Integer> map) {
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

    public void playGame() {
        Deck deck = new Deck();
        Random random = new Random();
        boolean helpBool = true;
        HashMap<Player, Integer> map = new HashMap<>();
        initializeMap(map);
        int i = 0;
        try {
            while (!checkIfAllKeysAreEmpty(map)) {
                while (i != GameMatrix.getNumberOfPlayers()) {
                    if (isItOverFlag) {
                        if (i != GameMatrix.getNumberOfPlayers() - 1) {
                            i++;
                            isItOverFlag = false;
                        } else {
                            ghostFigure.setGhostAlive();
                            ghostFigure.join();
                            setRefreshingFormFromGame();
                            break;
                        }
                    }
                    long figureTimeStart = new Date().getTime();
                    checkGamePause();
                    if (getWhichFigureIsPlayerPlayingWith(map, randomizedPlayers[i]) != -1) {
                        try {
                            int whichFigure = getWhichFigureIsPlayerPlayingWith(map, randomizedPlayers[i]);
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
                                mainFrame.setCardDescLabel("SPECIAL");
                                mainFrame.setCardPicLabel("special", 0);
                                processSpecialCard(random, i, map);
                                Thread.sleep(1000);
                                updateFigureTimeInThread(figureTimeStart, randomizedPlayers[i], whichFigure);
                            } else if (card instanceof SimpleCard) {
                                SimpleCard sCard = (SimpleCard) card;
                                mainFrame.setCardDescLabel("SIMPLE: " + sCard.getNumberOfFieldsToCross());
                                mainFrame.setCardPicLabel("simple", sCard.getNumberOfFieldsToCross());
                                processSimpleCard(map, i, whichFigure, (SimpleCard) card);
                                updateFigureTimeInThread(figureTimeStart, randomizedPlayers[i], whichFigure);
                            }
                        } catch (InterruptedException ex) {
                            log(ex);
                        }
                    }
                }
            }
            setRefreshingFormFromGame();
            if (ghostFigure.isAlive()) {
                try {
                    ghostFigure.join();
                } catch (InterruptedException ex) {
                    log(ex);
                }
            }
            try {
                writeToFiles();
            } catch (IOException ex) {
                log(ex);
            }
        } catch (Exception ex) {
            log(ex);
        }
    }

    private void setRefreshingFormFromGame() {
        RefreshingForm.setIsOver();
    }

    private void checkGamePause() throws InterruptedException {
        if (pause) {
            synchronized (this) {
                wait();
            }
        }
    }

    private void updateFigureTimeInThread(long figureTimeStart, Player randomizedPlayer, int whichFigure) {
        long figureTimeFinish = new Date().getTime();
        long finalTime = figureTimeFinish - figureTimeStart;
        if(isEaten || randomizedPlayer.getFigures()[whichFigure].didFigureFinish()) {
            finalTime -= RefreshingForm.getAccumulatedSeconds();
            isEaten = false;
        }

        randomizedPlayer.getFigures()[whichFigure].setTime(
                randomizedPlayer.getFigures()[whichFigure].getTime() + finalTime);
    }

    private void initializeMap(HashMap<Player, Integer> map) {
        for (int i = 0; i < GameMatrix.getNumberOfPlayers(); i++) {
            Player p = randomizedPlayers[i];
            map.put(p, GameMatrix.NUMBER_OF_FIGURES);
        }
    }

    private void processSimpleCard(HashMap<Player, Integer> map, int i, int whichFigure, SimpleCard card) throws InterruptedException {
        int positionToGoTo = card.getNumberOfFieldsToCross();
        int playerPosition = randomizedPlayers[i].getFigures()[whichFigure].getPosition();
        if (randomizedPlayers[i].getFigures()[whichFigure] instanceof SuperFast) {
            positionToGoTo *= 2;
        }
        int fullPosition = playerPosition + positionToGoTo + randomizedPlayers[i].getFigures()[whichFigure].getBonusCount();
        randomizedPlayers[i].getFigures()[whichFigure].setBonusCount(0);
        if (fullPosition >= GameMatrix.getMapTraversal().size()) fullPosition = GameMatrix.getMapTraversal().size() - 1;
        String labelText = "Na potezu je igrac " + randomizedPlayers[i].getName() + "., Figura " + (whichFigure+1) + ", prelazi " + (fullPosition - playerPosition) + "" +
                "polja, pomjera se sa pozicije " + GameMatrix.getOriginalMap().get(playerPosition) + " na " +
                GameMatrix.getOriginalMap().get(fullPosition) + ".";
        MainFrame.setCurrCardLabel(labelText);

        for (int pos = playerPosition; pos <= fullPosition; pos++) {
            checkGamePause();
            Pair realMatrixPosition = GameMatrix.getMatrixPositionOfElement(pos);
            Color realColour = randomizedPlayers[i].getFigures()[whichFigure].getRealColour();
            String newLabelString = randomizedPlayers[i].getFigures()[whichFigure].checkTypeOfFigure();
            if (pos == 0 && realMatrixPosition != null) {
                mainFrame.setMatrixLabel(realColour, realMatrixPosition, newLabelString);
            }
            if (pos > playerPosition) {
                updateGUIWhileMoving(pos, realMatrixPosition, realColour, newLabelString);
            }
            if (pos < GameMatrix.getMapTraversal().size() && GameMatrix.getMapTraversal().get(pos) instanceof Bonus) {
                pickUpBonus(i, whichFigure, pos);
            }
            if (pos < GameMatrix.getMapTraversal().size()) {
                figureMoved(i, whichFigure, pos);
                Thread.sleep(1000);
            }
            if (pos == GameMatrix.getMapTraversal().size() - 1) {
                figureFinished(map, i, whichFigure, pos);
            }
        }
    }

    private void processSpecialCard(Random random, int i, HashMap<Player, Integer> map) {
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
                    if(oldMatrixPosition != null) {
                        mainFrame.setMatrixLabel(Color.BLACK, oldMatrixPosition, "H");
                    }
                    if (!(GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof IFly)) {
                        if (GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof IDroppable) {
                            GameMatrix.setMapTraversal(holePositions.get(k), null);
                            isEaten = true;
                            map.put(randomizedPlayers[i], map.get(randomizedPlayers[i]) - 1);
                            if (map.get(randomizedPlayers[i]) <= 0) {
                                isItOverFlag = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private void writeToFiles() throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS");
        Date date = new Date();
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                fileName + sdf.format(date))));
        for (Player p : randomizedPlayers) {
            pw.println(p);
        }
        pw.println("\n\nUKUPNO VRIJEME TRAJANJA IGRE: ");
        pw.println((RefreshingForm.getSeconds() / (double) 1000) + "s");
        pw.close();
    }

    private void figureMoved(int i, int whichFigure, int pos) {
        if (pos != GameMatrix.getMapTraversal().size() - 1) {
            if (!randomizedPlayers[i].getFigures()[whichFigure].getFigurePath().contains(String.valueOf(GameMatrix.getOriginalMap().get(pos)))) {
                randomizedPlayers[i].getFigures()[whichFigure].setFigurePath(
                        randomizedPlayers[i].getFigures()[whichFigure].getFigurePath() + GameMatrix.getOriginalMap().get(pos) + "-");
            }
        }
        GameMatrix.setMapTraversal(pos, randomizedPlayers[i].getFigures()[whichFigure]);
        randomizedPlayers[i].getFigures()[whichFigure].setPosition(pos);
    }

    private void figureFinished(HashMap<Player, Integer> map, int i, int whichFigure, int pos) throws InterruptedException {
        GameMatrix.setMapTraversal(randomizedPlayers[i].getFigures()[whichFigure].getPosition(), null);
        map.put(randomizedPlayers[i], map.get(randomizedPlayers[i]) - 1);
        if (map.get(randomizedPlayers[i]) == 0) {
            isItOverFlag = true;
        }
        randomizedPlayers[i].getFigures()[whichFigure].setFigurePath(
                randomizedPlayers[i].getFigures()[whichFigure].getFigurePath() + GameMatrix.getOriginalMap().get(pos));
        randomizedPlayers[i].getFigures()[whichFigure].setPosition(pos + 1);

        Pair oldMatrixPosition = GameMatrix.getMatrixPositionOfElement(GameMatrix.getOriginalMap().size() - 1);
        if (oldMatrixPosition != null) {
            Integer element = (Integer) GameMatrix.getMATRIX()[oldMatrixPosition.getX()][oldMatrixPosition.getY()];
            mainFrame.setMatrixLabel(Color.MAGENTA, oldMatrixPosition, String.valueOf(element));
            Thread.sleep(1000);
            mainFrame.setMatrixLabel(Color.BLACK, oldMatrixPosition, String.valueOf(element));
        }
    }

    private void pickUpBonus(int i, int whichFigure, int pos) {
        GameMatrix.setMapTraversal(pos, null);
        Pair pair = GameMatrix.getMatrixPositionOfElement(pos);
        if (pair != null) {
            mainFrame.setBonusPickedUp(pair, randomizedPlayers[i].getFigures()[whichFigure]);
        }
        randomizedPlayers[i].getFigures()[whichFigure].setBonusCount(randomizedPlayers[i].getFigures()[whichFigure].getBonusCount() + 1);
    }

    private void updateGUIWhileMoving(int pos, Pair realMatrixPosition, Color realColour, String newLabelString) {
        int oldPos = pos - 1;
        GameMatrix.setMapTraversal(oldPos, null);
        Pair oldMatrixPosition = GameMatrix.getMatrixPositionOfElement(oldPos);
        if (oldMatrixPosition != null && realMatrixPosition != null) {
            Integer element = (Integer) GameMatrix.getMATRIX()[oldMatrixPosition.getX()][oldMatrixPosition.getY()];
            mainFrame.setMatrixLabel(Color.BLACK, oldMatrixPosition, String.valueOf(element));
            mainFrame.setMatrixLabel(realColour, realMatrixPosition, newLabelString);
        }
    }

    private int generateNumberOfHoles() {
        Random random = new Random();
        return random.nextInt(GameMatrix.NUMBER_OF_HOLES) + 1;
    }

    public static void log(Exception ex) {
        logger.log(Level.WARNING, ex.fillInStackTrace().toString());
    }
}