package game;

import FigureInterface.IDroppable;
import FigureInterface.IFly;
import FigureInterface.SuperFast;
import bonus.Bonus;
import cards.Card;
import cards.Deck;
import cards.SimpleCard;
import figure.Figure;
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
    private static MainFrame mainFrame;
    private final String fileName = "./IGRA_";
    private final static Logger logger;

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
        try {
            while (!checkIfAllKeysAreEmpty(map)) {
                for(int i = 0; i < GameMatrix.getNumberOfPlayers(); i++) {
                    checkGamePause();
                    if (getWhichFigureIsPlayerPlayingWith(map, randomizedPlayers[i]) != -1) {
                        try {
                            int whichFigure = getWhichFigureIsPlayerPlayingWith(map, randomizedPlayers[i]);
                            if(randomizedPlayers[i].getFigures()[whichFigure].getStartTime() == 0) {
                                randomizedPlayers[i].getFigures()[whichFigure].setStartTime(new Date().getTime());
                                randomizedPlayers[i].getFigures()[whichFigure].setTime(0);
                            } else {
                                randomizedPlayers[i].getFigures()[whichFigure].setStartTime(new Date().getTime());
                            }
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
                            if ("SPECIAL".equals(card.getCardType())) {
                                mainFrame.setCardDescLabel(card.getCardType());
                                mainFrame.setCardPicLabel("special", 0);
                                processSpecialCard(random, map);
                                Thread.sleep(1000);
                                updateFigureTimeInThread(randomizedPlayers[i].getFigures()[whichFigure]);
                            } else if ("SIMPLE".equals(card.getCardType())) {
                                SimpleCard sCard = (SimpleCard) card;
                                mainFrame.setCardDescLabel(card.getCardType() + " " + sCard.getNumberOfFieldsToCross());
                                mainFrame.setCardPicLabel("simple", sCard.getNumberOfFieldsToCross());
                                processSimpleCard(map, i, whichFigure, (SimpleCard) card);
                                updateFigureTimeInThread(randomizedPlayers[i].getFigures()[whichFigure]);
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
                    ghostFigure.setGhostAlive();
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

    private void updateFigureTimeInThread(Figure f) {
        long figureTimeFinish = new Date().getTime();
        long finalTime = figureTimeFinish - RefreshingForm.getAccumulatedSeconds() * 1000 - f.getStartTime();
        RefreshingForm.setAccumulatedSeconds();
        f.setTime(f.getTime() + finalTime);
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

            if(pos != playerPosition && GameMatrix.getMapTraversal().get(pos) instanceof Figure) {
                pos = jumpOverFigure(i, whichFigure, pos, realColour, newLabelString);
                if(pos > fullPosition) {
                    labelText = updateTextLabel(i, whichFigure, playerPosition, fullPosition, pos);
                    MainFrame.setCurrCardLabel(labelText);
                }
            }
            else if (pos > playerPosition && !(GameMatrix.getMapTraversal().get(pos) instanceof Figure)) {
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

    private String updateTextLabel(int i, int whichFigure, int playerPosition, int fullPosition, int pos) {
        String labelText;
        labelText = "Na potezu je igrac " + randomizedPlayers[i].getName() + "., Figura " + (whichFigure +1) + ", prelazi " + (fullPosition - playerPosition) + "" +
                "polja, pomjera se sa pozicije " + GameMatrix.getOriginalMap().get(playerPosition) + " na " +
                GameMatrix.getOriginalMap().get(pos) + ".";
        return labelText;
    }

    private int jumpOverFigure(int i, int whichFigure, int pos, Color realColour, String newLabelString) {
        Pair oldMatrixPosition = GameMatrix.getMatrixPositionOfElement(pos -1);
        GameMatrix.setMapTraversal(pos - 1, null);
        Integer element = (Integer) GameMatrix.getMATRIX()[oldMatrixPosition.getX()][oldMatrixPosition.getY()];
        mainFrame.setMatrixLabel(Color.BLACK, oldMatrixPosition, String.valueOf(element));
        while((GameMatrix.getMapTraversal().get(pos) instanceof Figure)) {
            if(pos != GameMatrix.getMapTraversal().size() - 1) {
                if (!randomizedPlayers[i].getFigures()[whichFigure].getFigurePath().contains(String.valueOf(GameMatrix.getOriginalMap().get(pos)))) {
                    randomizedPlayers[i].getFigures()[whichFigure].setFigurePath(
                            randomizedPlayers[i].getFigures()[whichFigure].getFigurePath() + GameMatrix.getOriginalMap().get(pos) + "-");
                }
                pos++;
            }
        }
        Pair newMatrixPosition = GameMatrix.getMatrixPositionOfElement(pos);
        mainFrame.setMatrixLabel(realColour, newMatrixPosition, newLabelString);
        GameMatrix.setMapTraversal(pos, randomizedPlayers[i].getFigures()[whichFigure]);
        return pos;
    }

    private void processSpecialCard(Random random, HashMap<Player, Integer> map) {
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
                        if(!(GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof IFly)) {
                            mainFrame.setMatrixLabel(Color.BLACK, oldMatrixPosition, "H");
                        } else if(GameMatrix.getMapTraversal().get(holePositions.get(k)) != null &&
                                GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof IFly)  {
                            mainFrame.makeFlyingFigureBlink(oldMatrixPosition);
                        }
                    }

                    if (!(GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof IFly)) {
                        if (GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof IDroppable) {
                            Figure f = (Figure)GameMatrix.getMapTraversal().get(holePositions.get(k));
                            Player p = checkWhichPlayerToDelete(f);
                            GameMatrix.setMapTraversal(holePositions.get(k), null);
                            map.put(p, map.get(p) - 1);
                        }
                    }
                }
            }
        }
    }

    private Player checkWhichPlayerToDelete(Figure f) {
        for(Player p : randomizedPlayers) {
            if(p.getFigures()[0].getColour().equals(f.getColour())) {
                return p;
            }
        }
        return null;
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