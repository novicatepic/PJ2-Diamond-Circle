package game;

import FigureInterface.IDroppable;
import FigureInterface.IFly;
import FigureInterface.SuperFast;
import bonus.Bonus;
import bonus.Diamond;
import cards.Card;
import cards.Deck;
import cards.SimpleCard;
import cards.SpecialCard;
import figure.Figure;
import figure.GhostFigure;
import hole.Hole;
import gui.MainFrame;
import pair.Pair;
import player.Player;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game /*extends Thread*/ {

    public boolean pause = false;
    private GhostFigure ghostFigure;
    public static Handler handler;
    private boolean isItOverFlag = false;
    private Player[] randomizedPlayers;
    private Player currentPlayer;
    private int fullPosition;
    private int currentFigureNumber;
    private int positionToGoTo = 0;
    private MainFrame mainFrame;
    private static final int MAX_NUM_OF_GAMES = 100;

    static {
        try {
            handler = new FileHandler("game.log");
            Logger.getLogger(Game.class.getName()).addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Player[] getRandomizedPlayers() {
        return randomizedPlayers;
    }

    private boolean checkIfAllKeysAreEmpty(ArrayList<HashMap<Player, Integer>> list, Player[] randomizedPlayers) {
        for (HashMap<Player, Integer> elem : list) {
            for (Player p : randomizedPlayers) {
                if (!elem.containsKey(p)) {
                    continue;
                }
                if (elem.get(p) > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private int getWhichFigureIsPlayerPlayingWith(ArrayList<HashMap<Player, Integer>> list, int pos, Player p) {
        HashMap<Player, Integer> map = list.get(pos);
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

    public static void main(String[] args) {
        /*try {
            new GameMatrix();
        } catch(Exception e1) {
            Logger.getLogger(Game.class.getName()).log(Level.WARNING, e1.fillInStackTrace().toString());
        }*/

        /*System.out.println("Game is about to start, if you want to pause it, input PAUSE, if you want to continue" +
                "input CONTINUE, if you want to end input --exit");
        Game diamondCircle = new Game();
        diamondCircle.randomizedPlayers = GameMatrix.randomizePlayers(GameMatrix.getPlayers());
        diamondCircle.mainFrame = new MainFrame(diamondCircle);
        MainFrame.setGamesPlayed(0);
        diamondCircle.mainFrame.setVisible(true);
        diamondCircle.start();*/

        Game[] games = new Game[MAX_NUM_OF_GAMES];
        for(int i = 0; i < games.length; i++) {
            try {
                new GameMatrix();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("i" + i);
            if(i > 0) {
                System.out.println("1");
            }

            games[i] = new Game();
            games[i].randomizedPlayers = GameMatrix.randomizePlayers(GameMatrix.getPlayers());
            games[i].mainFrame = new MainFrame(games[i]);
            if(i == 0) {
                MainFrame.setGamesPlayed(0);
            }
            else {
                MainFrame.setGamesPlayed(MainFrame.getGamesPlayed() + 1);
            }
            games[i].mainFrame.setVisible(true);
            games[i].run();
            games[i].mainFrame.setVisible(false);
            games[i].mainFrame.dispose();
        }
    }

    public void run() {
        long startTime = new Date().getTime();
        Deck deck = new Deck();
        Random random = new Random();
        boolean helpBool = true;
        ArrayList<HashMap<Player, Integer>> list = new ArrayList<>();
        HashMap<Figure, String> figureMap = new HashMap<>();
        initializeHelpCollections(randomizedPlayers, list, figureMap);
        int i = 0;
        try {
            while (!checkIfAllKeysAreEmpty(list, randomizedPlayers)) {
                while(i != GameMatrix.getNumberOfPlayers()) {
                    if(isItOverFlag) {
                        if(i != GameMatrix.getNumberOfPlayers() - 1) {
                            i++;
                            isItOverFlag = false;
                        }
                        else {
                            ghostFigure.setGhostAlive();
                            ghostFigure.join();
                            break;
                        }
                    }
                    long figureTimeStart = new Date().getTime();
                    long figureTimeFinish = 0;
                    if(pause) {
                        synchronized (this) {
                            wait();
                        }
                    }
                    currentPlayer = randomizedPlayers[i];
                    if (getWhichFigureIsPlayerPlayingWith(list, i, currentPlayer) != -1) {
                        //System.out.println("RANDOMIZED PLAYERS: " + randomizedPlayers[i].getName());
                        //System.out.println("FIGURE: " + getWhichFigureIsPlayerPlayingWith(list, i, randomizedPlayers[i]));
                        try {
                            int whichFigure = getWhichFigureIsPlayerPlayingWith(list, i, currentPlayer);
                            currentFigureNumber = whichFigure;
                            if (helpBool) {
                                ghostFigure = new GhostFigure();
                                ghostFigure.start();
                                helpBool = false;
                            }
                            Card card = deck.pullOutACard();
                            removeHoles();
                            mainFrame.clearHoles();
                            if (card instanceof SpecialCard) {
                                String cardDescription = "SPECIAL";
                                mainFrame.setCardDescLabel(cardDescription);
                                mainFrame.setCardPicLabel("special", 0);
                                processSpecialCard(random, i, list, randomizedPlayers);
                                Thread.sleep(1000);
                            } else if (card instanceof SimpleCard) {
                                SimpleCard sCard = (SimpleCard) card;
                                String cardDescription = String.valueOf("SIMPLE: " + sCard.getNumberOfFieldsToCross());
                                mainFrame.setCardDescLabel(cardDescription);
                                mainFrame.setCardPicLabel("simple", sCard.getNumberOfFieldsToCross());
                                //System.out.println("SIMPLE");
                                if (processSimpleCard(randomizedPlayers, list, figureMap, i, whichFigure, (SimpleCard) card)) {
                                    updateFigureTimeInThread(figureTimeFinish, figureTimeStart, currentPlayer, whichFigure);
                                }
                                else {
                                    updateFigureTimeInThread(figureTimeFinish, figureTimeStart, currentPlayer, whichFigure);
                                }
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.WARNING, ex.fillInStackTrace().toString());
                            ex.printStackTrace();
                        }
                        //printFigures();
                    }
                }
            }
            System.out.println("WRITTING TO FILES!");
            try {
                writeToFiles(startTime, randomizedPlayers, figureMap);
            } catch (IOException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.WARNING, ex.fillInStackTrace().toString());
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            //Logger.getLogger(Game.class.getName()).log(Level.WARNING, ex.fillInStackTrace().toString());
            ex.printStackTrace();
        }
    }

    private void updateFigureTimeInThread(long figureTimeFinish, long figureTimeStart, Player randomizedPlayers, int whichFigure) {
        figureTimeFinish = new Date().getTime();
        long finalTime = figureTimeFinish - figureTimeStart;
        randomizedPlayers.getFigures()[whichFigure].setTime(
                randomizedPlayers.getFigures()[whichFigure].getTime() + finalTime);
    }

    private void initializeHelpCollections(Player[] randomizedPlayers, ArrayList<HashMap<Player, Integer>> list, HashMap<Figure, String> figureMap) {
        for (Player p : randomizedPlayers) {
            for (Figure f : p.getFigures()) {
                figureMap.put(f, "");
            }
        }
        for (int i = 0; i < GameMatrix.getNumberOfPlayers(); i++) {
            Player p = randomizedPlayers[i];
            HashMap<Player, Integer> map = new HashMap<>();
            map.put(p, GameMatrix.NUMBER_OF_FIGURES);
            list.add(map);
        }
    }

    private boolean processSimpleCard(Player[] randomizedPlayers, ArrayList<HashMap<Player, Integer>> list, HashMap<Figure,
            String> figureMap, int i, int whichFigure, SimpleCard card) throws InterruptedException {

        positionToGoTo = card.getNumberOfFieldsToCross();
        int playerPosition;
        synchronized (randomizedPlayers[i].getFigures()[whichFigure]) {
            playerPosition = randomizedPlayers[i].getFigures()[whichFigure].getPosition();
        }
        /*System.out.println("FIGURE " + randomizedPlayers[i].getFigures()[whichFigure].checkTypeOfFigure());
        System.out.println("PLAYER POSITION: " + playerPosition);*/
        if (randomizedPlayers[i].getFigures()[whichFigure] instanceof SuperFast) {
            positionToGoTo *= 2;
        }
        fullPosition = playerPosition + positionToGoTo + randomizedPlayers[i].getBonusCount();
        randomizedPlayers[i].setBonusCount(0);
        if (fullPosition >= GameMatrix.getMapTraversal().size()) fullPosition = GameMatrix.getMapTraversal().size() - 1;

        String labelText = "Na potezu je igrac " + i + "., Figura " + whichFigure + ", prelazi " + fullPosition + "" +
                "polja, pomjera se sa pozicije " + GameMatrix.getOriginalMap().get(playerPosition) + " na " +
                GameMatrix.getOriginalMap().get(fullPosition) + ".";
        mainFrame.setCurrCardLabel(labelText);


        for (int pos = playerPosition; pos <= fullPosition; pos++) {
            if(pause) {
                synchronized (this) {
                    wait();
                }
            }

            Pair realMatrixPosition = GameMatrix.getMatrixPositionOfElement(pos);
            Color realColour = randomizedPlayers[i].getFigures()[whichFigure].getRealColour();
            if(pos > playerPosition) {
                int oldPos = pos - 1;
                //System.out.println("Figure removed from pos: " + oldPos);
                GameMatrix.setMapTraversal(oldPos, null);
                Pair oldMatrixPosition = GameMatrix.getMatrixPositionOfElement(oldPos);
                Integer element = (Integer)GameMatrix.getMATRIX()[oldMatrixPosition.getX()][oldMatrixPosition.getY()];
                mainFrame.setMatrixLabel(Color.BLACK, oldMatrixPosition, String.valueOf(element));
                String newLabelString = randomizedPlayers[i].getFigures()[whichFigure].checkTypeOfFigure();
                mainFrame.setMatrixLabel(realColour, realMatrixPosition, newLabelString);
            }

            if (pos < GameMatrix.getMapTraversal().size() && GameMatrix.getMapTraversal().get(pos) instanceof Bonus) {
                GameMatrix.setMapTraversal(pos, null);
                randomizedPlayers[i].setBonusCount(randomizedPlayers[i].getBonusCount() + 1);
                Thread.sleep(1000);
            }
            if (pos == GameMatrix.getMapTraversal().size() - 1) {
                GameMatrix.setMapTraversal(randomizedPlayers[i].getFigures()[whichFigure].getPosition(), null);
                //randomizedPlayers[i].getFigures()[whichFigure].setPosition(GameMatrix.getMapTraversal().size() - 1);
                list.get(i).replace(randomizedPlayers[i], list.get(i).get(randomizedPlayers[i]) - 1);
                if(list.get(i).get(randomizedPlayers[i]) == 0) {
                    isItOverFlag = true;
                }
                String newString = figureMap.get(randomizedPlayers[i].getFigures()[whichFigure]) + "" +
                        GameMatrix.getOriginalMap().get(pos);
                figureMap.replace(randomizedPlayers[i].getFigures()[whichFigure], newString);
                randomizedPlayers[i].getFigures()[whichFigure].setPosition(pos + 1);

                Pair oldMatrixPosition = GameMatrix.getMatrixPositionOfElement(GameMatrix.getOriginalMap().size() - 1);
                Integer element = (Integer)GameMatrix.getMATRIX()[oldMatrixPosition.getX()][oldMatrixPosition.getY()];
                mainFrame.setMatrixLabel(Color.MAGENTA, oldMatrixPosition, String.valueOf(element));

                Thread.sleep(1000);
                return true;
            }
            if (pos < GameMatrix.getMapTraversal().size()) {
                if (pos != GameMatrix.getMapTraversal().size() - 1 && (pos == fullPosition || pos == 0)) {
                    String newString = figureMap.get(randomizedPlayers[i].getFigures()[whichFigure]) + "" +
                            GameMatrix.getOriginalMap().get(pos) + "-";
                    figureMap.replace(randomizedPlayers[i].getFigures()[whichFigure], newString);
                }
                //System.out.println("Figure set at pos: " + pos);
                GameMatrix.setMapTraversal(pos, randomizedPlayers[i].getFigures()[whichFigure]);
                randomizedPlayers[i].getFigures()[whichFigure].setPosition(pos);
                Thread.sleep(1000);
            }
        }
        /*if (fullPosition < GameMatrix.getMapTraversal().size()) {
            synchronized (randomizedPlayers[i].getFigures()[whichFigure]) {
                GameMatrix.setMapTraversal(randomizedPlayers[i].getFigures()[whichFigure].getPosition(), null);
                randomizedPlayers[i].getFigures()[whichFigure].setPosition(fullPosition);
                GameMatrix.setMapTraversal(fullPosition, randomizedPlayers[i].getFigures()[whichFigure]);
            }
        }*/
        return false;
    }

    private void processSpecialCard(Random random, int i, ArrayList<HashMap<Player, Integer>> list, Player[] randomizedPlayers) {
        int generatedNumberOfHoles = generateNumberOfHoles();
        Hole[] holes = new Hole[generatedNumberOfHoles];
        for (int h = 0; h < holes.length; h++) {
            holes[h] = new Hole();
        }
        List<Integer> holePositions = new ArrayList<>();
        int temp = generatedNumberOfHoles;

        while (temp > 0) {
            int position = random.nextInt(GameMatrix.getMapTraversal().size() - 1) + 1;
            if (!(GameMatrix.getMapTraversal().get(position) instanceof Bonus) &&
                    position != GameMatrix.getMapTraversal().size() - 1 &&
                    !holePositions.contains(position)) {
                holePositions.add(position);
                temp--;
            }
        }

        for(int holePosition : holePositions) {
            Pair oldMatrixPosition = GameMatrix.getMatrixPositionOfElement(holePosition);
            Integer element = (Integer)GameMatrix.getMATRIX()[oldMatrixPosition.getX()][oldMatrixPosition.getY()];
            mainFrame.setMatrixLabel(Color.BLACK, oldMatrixPosition, "H");
        }

        for (int k = 0; k < holePositions.size(); k++) {
            if (!(GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof Bonus)) {
                if(!(GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof IFly)) {
                    if(GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof IDroppable) {
                        System.out.println("FIGURE EATEN!");
                        GameMatrix.setMapTraversal(holePositions.get(k), null);
                        list.get(i).replace(randomizedPlayers[i], list.get(i).get(randomizedPlayers[i]) - 1);
                        if(list.get(i).get(randomizedPlayers[i]) <= 0) {
                            isItOverFlag = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void writeToFiles(long startTime, Player[] randomizedPlayers, HashMap<Figure, String> figureMap) throws IOException, InterruptedException {
        int outerCounter = 0;
        int innerCounter = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS");
        Date date = new Date();
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                "IGRA_" + sdf.format(date))));
        for (Player p : randomizedPlayers) {
            for (Figure f : p.getFigures()) {
                String helpString = null;
                if (f.didFigureFinish()) {
                    helpString = "DA";
                } else {
                    helpString = "NE";
                }
                pw.println("Igrac " + (outerCounter + 1) + " - " + p.getName() + "\n" +
                        "Figura " + innerCounter++ + " (" + f.checkTypeOfFigure() + ", " + f.getColour() + ") - " +
                        "predjeni put (" + figureMap.get(f) + ") - stigla do cilja " + helpString + "\n" +
                        "Ukupno vrijeme igranja sa figurom: " + f.getTime() / (double)1000 + "s\n");
            }
            outerCounter++;
        }
        pw.println((new Date().getTime() - startTime) / (double)1000 + "s");
        pw.close();
    }

    private int generateNumberOfHoles() {
        Random random = new Random();
        return random.nextInt(GameMatrix.NUMBER_OF_HOLES) + 1;
    }

    private void removeHoles() {
        for(int i = 0; i < GameMatrix.getMapTraversal().size(); i++) {
            if(GameMatrix.getMapTraversal().get(i) instanceof Hole) {
                GameMatrix.setMapTraversal(i, null);
            }
        }
    }

    /*private void printFigures() {
        for(int i = 1; i < GameMatrix.getMapTraversal().size(); i++) {
            if(GameMatrix.getMapTraversal().get(i) instanceof Figure) {
                System.out.println("Figure at " + i);
            }
        }
        System.out.println("!");
    }*/
}