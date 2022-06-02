package main;

import FigureInterface.IDroppable;
import FigureInterface.IFly;
import FigureInterface.SuperFast;
import bonus.Bonus;
import cards.Card;
import cards.Deck;
import cards.SimpleCard;
import cards.SpecialCard;
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
    private GhostFigure ghostFigure = new GhostFigure();
    public static Handler handler;
    private boolean isItOverFlag = false;

    static {
        try {
            handler = new FileHandler("game.log");
            Logger.getLogger(Game.class.getName()).addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfAllKeysAreEmpty(ArrayList<HashMap<Player, Integer>> list, Player[] randomizedPlayers) {
        //System.out.println("4");
        for (HashMap<Player, Integer> elem : list) {
            for (Player p : randomizedPlayers) {
                //System.out.println(p.getName() + " " + elem.get(p));
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
        //System.out.println("5");
        HashMap<Player, Integer> map = list.get(pos);
        int value = map.get(p);
        //System.out.println("INT VALUE: " + value);
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

        //Deck deck = new Deck();

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
        int i = 0;
        try {
            while (!checkIfAllKeysAreEmpty(list, randomizedPlayers)) {
                while(i != GameMatrix.getNumberOfPlayers()) {
                    if(isItOverFlag) {
                        //System.out.println("OVER!");
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
                    if (getWhichFigureIsPlayerPlayingWith(list, i, randomizedPlayers[i]) != -1) {
                        //System.out.println("RANDOMIZED PLAYERS: " + randomizedPlayers[i].getName());
                        //System.out.println("FIGURE: " + getWhichFigureIsPlayerPlayingWith(list, i, randomizedPlayers[i]));

                        try {
                            int whichFigure = getWhichFigureIsPlayerPlayingWith(list, i, randomizedPlayers[i]);

                            if(randomizedPlayers[i].getFigures()[whichFigure] instanceof FlyingFigure) {
                                //System.out.println("YES");
                            }

                            if (helpBool) {
                                ghostFigure.start();
                                helpBool = false;
                            }
                            Card card = deck.pullOutACard();
                            removeHoles();
                            if (card instanceof SpecialCard) {
                                //System.out.println("SPECIAL");
                                processSpecialCard(random, i, list, randomizedPlayers);
                                //sleep(1000);
                            } else if (card instanceof SimpleCard) {
                                //System.out.println("SIMPLE");
                                if (processSimpleCard(randomizedPlayers, list, figureMap, i, whichFigure, (SimpleCard) card)) {
                                    //sleep(1000);
                                    updateFigureTimeInThread(figureTimeFinish, figureTimeStart, randomizedPlayers[i], whichFigure);
                                }
                                else {
                                    //sleep(1000);
                                    updateFigureTimeInThread(figureTimeFinish, figureTimeStart, randomizedPlayers[i], whichFigure);
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
        //System.out.println("3");
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
        int positionToGoTo = card.getNumberOfFieldsToCross();
        int playerPosition;
        synchronized (randomizedPlayers[i].getFigures()[whichFigure]) {
            playerPosition = randomizedPlayers[i].getFigures()[whichFigure].getPosition();
        }
        //System.out.println("FIGURE " + randomizedPlayers[i].getFigures()[whichFigure].checkTypeOfFigure());
        //System.out.println("PLAYER POSITION: " + playerPosition);
        if (randomizedPlayers[i].getFigures()[whichFigure] instanceof SuperFast) {
            positionToGoTo *= 2;
        }
        int fullPosition = playerPosition + positionToGoTo + randomizedPlayers[i].getBonusCount();
        randomizedPlayers[i].setBonusCount(0);
        if (fullPosition >= GameMatrix.getMapTraversal().size()) fullPosition = GameMatrix.getMapTraversal().size() - 1;
        for (int pos = playerPosition; pos <= fullPosition; pos++) {
            if (pos < GameMatrix.getMapTraversal().size() - 1 && GameMatrix.getMapTraversal().get(pos) instanceof Figure) {
                if (pos == fullPosition) {
                    fullPosition++;
                } else continue;
            } /*else if (pos < GameMatrix.getMapTraversal().size() && GameMatrix.getMapTraversal().get(pos) instanceof Hole &&
                    (randomizedPlayers[i].getFigures()[whichFigure] instanceof IDroppable)) {
                //System.out.println("FIGURE " + ((Figure) GameMatrix.getMapTraversal().get(pos)).checkTypeOfFigure() + " EATEN!");
                GameMatrix.setMapTraversal(pos, null);
                list.get(i).replace(randomizedPlayers[i], list.get(i).get(randomizedPlayers[i]) - 1);
                //System.out.println("GET1: " + list.get(i).get(randomizedPlayers[i]));
                if(list.get(i).get(randomizedPlayers[i]) == 0) {
                    //System.out.println("OVER1");
                    isItOverFlag = true;
                }
                //sleep(1000);
                return true;
            }*/ else if (pos < GameMatrix.getMapTraversal().size() && GameMatrix.getMapTraversal().get(pos) instanceof Bonus) {
                //System.out.println("BONUS!");
                GameMatrix.setMapTraversal(pos, null);
                randomizedPlayers[i].setBonusCount(randomizedPlayers[i].getBonusCount() + 1);
            }
            if (pos == GameMatrix.getMapTraversal().size() - 1) {
                GameMatrix.setMapTraversal(randomizedPlayers[i].getFigures()[whichFigure].getPosition(), null);
                list.get(i).replace(randomizedPlayers[i], list.get(i).get(randomizedPlayers[i]) - 1);
               // System.out.println("GET2: " + list.get(i).get(randomizedPlayers[i]));
                if(list.get(i).get(randomizedPlayers[i]) == 0) {
                    //System.out.println("OVER2");
                    isItOverFlag = true;
                }
                String newString = figureMap.get(randomizedPlayers[i].getFigures()[whichFigure]) + "" +
                        GameMatrix.getOriginalMap().get(pos);
                figureMap.replace(randomizedPlayers[i].getFigures()[whichFigure], newString);
                randomizedPlayers[i].getFigures()[whichFigure].setPosition(pos);
                //sleep(1000);
                return true;
            }
            if (pos < GameMatrix.getMapTraversal().size()) {
                if (pos != GameMatrix.getMapTraversal().size() - 1 && (pos == fullPosition || pos == 0)) {
                    String newString = figureMap.get(randomizedPlayers[i].getFigures()[whichFigure]) + "" +
                            GameMatrix.getOriginalMap().get(pos) + "-";
                    figureMap.replace(randomizedPlayers[i].getFigures()[whichFigure], newString);
                }
                //sleep(1000);
            }
        }
        if (fullPosition < GameMatrix.getMapTraversal().size()) {
            synchronized (randomizedPlayers[i].getFigures()[whichFigure]) {
                GameMatrix.setMapTraversal(randomizedPlayers[i].getFigures()[whichFigure].getPosition(), null);
                randomizedPlayers[i].getFigures()[whichFigure].setPosition(fullPosition);
                GameMatrix.setMapTraversal(fullPosition, randomizedPlayers[i].getFigures()[whichFigure]);
                //System.out.println("FIGURE POS: " + fullPosition);
            }
        }
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
        /*if(temp > GameMatrix.getNumberOfFreePositionsInMatrix()) {
            temp = GameMatrix.getNumberOfFreePositionsInMatrix();
        }*/

        while (temp > 0) {
            int position = random.nextInt(GameMatrix.getMapTraversal().size() - 1) + 1;
            if (!(GameMatrix.getMapTraversal().get(position) instanceof Bonus) &&
                    position != GameMatrix.getMapTraversal().size() - 1 &&
                    !holePositions.contains(position)) {
                //System.out.println("BONUS");
                //System.out.println("HOLE POSITION: " + position);
                holePositions.add(position);
                temp--;
            }
        }

        /*System.out.println("POSITIONS: ");
        for(int position : holePositions) {
            System.out.println(position);
        }
        System.out.println("!");*/

        for (int k = 0; k < holePositions.size(); k++) {
            if (!(GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof Bonus)) {
                if(!(GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof IFly)) {
                    //System.out.println("HOLE POSITION: " + holePositions.get(k));
                    //GameMatrix.setMapTraversal(holePositions.get(k), holes[k]);
                    //System.out.println("GET3: " + list.get(i).get(randomizedPlayers[i]));
                    if(GameMatrix.getMapTraversal().get(holePositions.get(k)) instanceof IDroppable) {
                        System.out.println("FIGURE EATEN!");
                        GameMatrix.setMapTraversal(holePositions.get(k), null);
                        list.get(i).replace(randomizedPlayers[i], list.get(i).get(randomizedPlayers[i]) - 1);
                        if(list.get(i).get(randomizedPlayers[i]) <= 0) {
                            //System.out.println("OVER3");
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
        /*PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                "IGRA_VRIJEME_TRAJANJA")));*/
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

    private void printFigures() {
        for(int i = 1; i < GameMatrix.getMapTraversal().size(); i++) {
            if(GameMatrix.getMapTraversal().get(i) instanceof Figure) {
                System.out.println("Figure at " + i);
            }
        }
        System.out.println("!");
    }
}
