package main;

import FigureInterface.IDroppable;
import FigureInterface.IFly;
import FigureInterface.SuperFast;
import bonus.Bonus;
import cards.Card;
import cards.Deck;
import cards.SimpleCard;
import cards.SpecialCard;
import exceptions.IncorrectColour;
import exceptions.InvalidMatrixDimension;
import exceptions.InvalidNumberOfPlayers;
import figure.Figure;
import figure.FlyingFigure;
import figure.GhostFigure;
import hole.Hole;
import player.Player;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game extends Thread {

    private static boolean pause = false;
    public static Handler handler;

    {
        try {
            handler = new FileHandler("game.log");
            Logger.getLogger(Game.class.getName()).addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfAllKeysAreEmpty(ArrayList<HashMap<Player, Integer>> list, Player[] randomizedPlayers) {
        for (HashMap<Player, Integer> elem : list) {
            for (Player p : randomizedPlayers) {
                if (!elem.containsKey(p)) {
                    continue;
                }
                if (elem.get(p) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private int getWhichFigureIsPlayerPlayingWith(ArrayList<HashMap<Player, Integer>> list, int pos, Player p) {
        HashMap<Player, Integer> map = list.get(pos);
        Integer value = map.get(p);
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
        try {
            GameMatrix gameMatrix = new GameMatrix();
        } catch(Exception e1) {
            Logger.getLogger(Game.class.getName()).log(Level.WARNING, e1.fillInStackTrace().toString());
        } /*catch (InvalidNumberOfPlayers e2) {

        } catch (IncorrectColour e3) {

        } catch (Exception e) {

        }*/

        System.out.println("Game is about to start, if you want to pause it, input PAUSE, if you want to continue" +
                "input CONTINUE, if you want to end input --exit");
        Game diamondCircle = new Game();
        diamondCircle.start();
        Scanner scanner = new Scanner(System.in);
        String option = "";
        while(!"--exit".equals(option)) {
            option = scanner.nextLine();
            if("PAUSE".equals(option)) {
                pause = true;
            }
            else if("CONTINUE".equals(option)) {
                pause = false;
                try {
                    synchronized (diamondCircle) {
                        diamondCircle.notify();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if("--exit".equals(option)) { }
            else {
                System.out.println("Unknown option, try again!");
            }
        }
    }

    @Override
    public void run() {
        long startTime = new Date().getTime();
        Player[] randomizedPlayers = GameMatrix.randomizePlayers(GameMatrix.getPlayers());
        Deck deck = new Deck();
        Random random = new Random();
        boolean helpBool = true;
        ArrayList<HashMap<Player, Integer>> list = new ArrayList<>();
        HashMap<Figure, String> figureMap = new HashMap<>();
        initializeHelpCollections(randomizedPlayers, list, figureMap);
        try {
            while (!checkIfAllKeysAreEmpty(list, randomizedPlayers)) {
                for (int i = 0; i < GameMatrix.getNumberOfPlayers(); i++) {
                    long figureTimeStart = new Date().getTime();
                    long figureTimeFinish = 0;
                    if(pause) {
                        synchronized (this) {
                            wait();
                        }
                    }
                    if (getWhichFigureIsPlayerPlayingWith(list, i, randomizedPlayers[i]) != -1) {
                        //System.out.println("RANDOMIZED PLAYERS: " + randomizedPlayers[i].getName());
                        try {
                            int whichFigure = getWhichFigureIsPlayerPlayingWith(list, i, randomizedPlayers[i]);
                            //System.out.println("WHICH FIGURE: " + whichFigure);
                            //System.out.println("FIGURE TYPE: " + randomizedPlayers[i].getFigures()[whichFigure].checkTypeOfFigure());
                            if (helpBool) {
                                new GhostFigure().start();
                                helpBool = false;
                            }
                            Card card = deck.pullOutACard();
                            if (card instanceof SpecialCard) {
                                processSpecialCard(random, i);
                            } else if (card instanceof SimpleCard) {
                                if (processSimpleCard(randomizedPlayers, list, figureMap, i, whichFigure, (SimpleCard) card)) {
                                    figureTimeFinish = new Date().getTime();
                                    long finalTime = figureTimeFinish - figureTimeStart;
                                    randomizedPlayers[i].getFigures()[whichFigure].setTime(
                                            randomizedPlayers[i].getFigures()[whichFigure].getTime() + finalTime);
                                    continue;
                                }
                                /*else {
                                    figureTimeFinish = new Date().getTime();
                                }
                                long finalTime = figureTimeFinish - figureTimeStart;
                                randomizedPlayers[i].getFigures()[whichFigure].setTime(
                                        randomizedPlayers[i].getFigures()[whichFigure].getTime() + finalTime);*/
                            }
                            sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.WARNING, ex.fillInStackTrace().toString());
                            ex.printStackTrace();
                        }
                    }
                }
            }
            try {
                writeToFiles(startTime, randomizedPlayers, figureMap);
            } catch (IOException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.WARNING, ex.fillInStackTrace().toString());
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            Logger.getLogger(Game.class.getName()).log(Level.WARNING, ex.fillInStackTrace().toString());
            ex.printStackTrace();
        }
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

    private boolean processSimpleCard(Player[] randomizedPlayers, ArrayList<HashMap<Player, Integer>> list, HashMap<Figure, String> figureMap, int i, int whichFigure, SimpleCard card) throws InterruptedException {
        int positionToGoTo = card.getNumberOfFieldsToCross();
        int playerPosition;
        synchronized (randomizedPlayers[i].getFigures()[whichFigure]) {
            playerPosition = randomizedPlayers[i].getFigures()[whichFigure].getPosition();
        }
        //System.out.println("PLAYER POSITION: " + playerPosition);
        if (randomizedPlayers[i].getFigures()[whichFigure] instanceof SuperFast) {
            positionToGoTo *= 2;
        }
        int fullPosition = playerPosition + positionToGoTo;
        if (fullPosition >= GameMatrix.getMapTraversal().size()) fullPosition = GameMatrix.getMapTraversal().size() - 1;
        for (int pos = playerPosition; pos <= fullPosition; pos++) {
            if (pos < GameMatrix.getMapTraversal().size() - 1 && GameMatrix.getMapTraversal().get(pos) instanceof Figure) {
                if (pos == fullPosition) {
                    fullPosition++;
                } else continue;
            } else if (pos < GameMatrix.getMapTraversal().size() && GameMatrix.getMapTraversal().get(pos) instanceof Hole &&
                    (randomizedPlayers[i].getFigures()[whichFigure] instanceof IDroppable)) {
                System.out.println("FIGURE EATEN!");
                GameMatrix.setMapTraversal(pos, null);
                list.get(i).replace(randomizedPlayers[i], list.get(i).get(randomizedPlayers[i]) - 1);
                sleep(1000);
                return true;
            } else if (pos < GameMatrix.getMapTraversal().size() && GameMatrix.getMapTraversal().get(pos) instanceof Bonus) {
                System.out.println("BONUS!");
                GameMatrix.setMapTraversal(pos, null);
                if (fullPosition != GameMatrix.getMapTraversal().size() - 1)
                    fullPosition++;
            }
            if (pos == GameMatrix.getMapTraversal().size() - 1) {
                list.get(i).replace(randomizedPlayers[i], list.get(i).get(randomizedPlayers[i]) - 1);
                String newString = figureMap.get(randomizedPlayers[i].getFigures()[whichFigure]) + "" +
                        GameMatrix.getOriginalMap().get(pos);
                figureMap.replace(randomizedPlayers[i].getFigures()[whichFigure], newString);
                randomizedPlayers[i].getFigures()[whichFigure].setPosition(pos);
                return true;
            }
            if (pos < GameMatrix.getMapTraversal().size()) {
                if (pos != GameMatrix.getMapTraversal().size() - 1) {
                    String newString = figureMap.get(randomizedPlayers[i].getFigures()[whichFigure]) + "" +
                            GameMatrix.getOriginalMap().get(pos) + "-";
                    figureMap.replace(randomizedPlayers[i].getFigures()[whichFigure], newString);
                }
            }
        }
        if (fullPosition < GameMatrix.getMapTraversal().size()) {
            synchronized (randomizedPlayers[i].getFigures()[whichFigure]) {
                randomizedPlayers[i].getFigures()[whichFigure].setPosition(fullPosition);
                GameMatrix.setMapTraversal(fullPosition, randomizedPlayers[i].getFigures()[whichFigure]);
            }
        }
        return false;
    }

    private void processSpecialCard(Random random, int i) {
        Hole[] holes = new Hole[GameMatrix.NUMBER_OF_HOLES];
        for (int h = 0; h < holes.length; h++) {
            holes[i] = new Hole();
        }
        List<Integer> holePositions = new ArrayList<>();
        int temp = GameMatrix.NUMBER_OF_HOLES;
        while (temp > 0) {
            Integer position = random.nextInt(GameMatrix.getMapTraversal().size());
            if (!holePositions.contains(position)) {
                holePositions.add(position);
                temp--;
            }
        }
        for (int k = 0; k < holePositions.size(); k++) {
            if (!(GameMatrix.getMapTraversal().get(k) instanceof Figure) && !(GameMatrix.getMapTraversal().get(k) instanceof Bonus))
                GameMatrix.setMapTraversal(k, holes[k]);
        }
    }

    private void writeToFiles(long startTime, Player[] randomizedPlayers, HashMap<Figure, String> figureMap) throws IOException, InterruptedException {
        int outerCounter = 0;
        int innerCounter = 0;
        for (Player p : randomizedPlayers) {
            for (Figure f : p.getFigures()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                Date date = new Date();
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                        "IGRA_" + sdf.format(date))));
                String helpString = null;
                if (f.didFigureFinish()) {
                    helpString = "DA";
                } else {
                    helpString = "NE";
                }
                pw.println("Igrac " + (outerCounter + 1) + " - " + p.getName() + "\n" +
                        "Figura " + innerCounter++ + " (" + f.checkTypeOfFigure() + ", " + f.getColour() + ") - " +
                        "predjeni put (" + figureMap.get(f) + ") - stigla do cilja " + helpString + "\n" +
                        "Ukupno vrijeme trajanja igre: " + f.getTime());
                pw.close();
                sleep(1000);
            }
            outerCounter++;
        }
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                "IGRA_VRIJEME_TRAJANJA")));
        pw.println((new Date().getTime() - startTime) / (double)1000);
        pw.close();
    }
}
