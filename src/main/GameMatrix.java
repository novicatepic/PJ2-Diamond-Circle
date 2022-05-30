package main;

import exceptions.IncorrectColour;
import exceptions.InvalidMatrixDimension;
import exceptions.InvalidNumberOfPlayers;
import figure.*;
import player.Player;
import java.util.*;

public class GameMatrix {

    public static final Integer NUMBER_OF_FIGURES = 4;
    private static int MATRIX_DIMENSIONS;
    private final Object[][] MATRIX;
    private static int NUMBER_OF_PLAYERS;
    private static Player[] players;
    private static ArrayList<Object> mapTraversal;
    private static final ArrayList<Object> originalMap = new ArrayList<>();
    public static final int NUMBER_OF_HOLES = 15;

    GameMatrix() throws InvalidMatrixDimension, InvalidNumberOfPlayers, IncorrectColour {
        MATRIX_DIMENSIONS = loadMatrixDimensions();
        NUMBER_OF_PLAYERS = loadNumberOfPlayers();
        MATRIX = new Object[MATRIX_DIMENSIONS][MATRIX_DIMENSIONS];
        putIntegersToMatrix();
        PathClass pathClass = new PathClass(MATRIX, MATRIX_DIMENSIONS);
        mapTraversal = new ArrayList<>();
        mapTraversal = pathClass.getPath();
        originalMap.addAll(mapTraversal);
        players = new Player[NUMBER_OF_PLAYERS];
        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            Figure[] figures = generateFigures();
            players[i] = new Player("name", figures);
        }
    }

    public static int getMatrixDimensions() {
        return MATRIX_DIMENSIONS;
    }
    public static ArrayList<Object> getMapTraversal() {
        return mapTraversal;
    }

    public static ArrayList<Object> getOriginalMap() {
        return originalMap;
    }

    public static Player[] getPlayers() {return players;}
    public static int getNumberOfPlayers() {return NUMBER_OF_PLAYERS;}
    public static void setMapTraversal(int positon, Object element) {
        mapTraversal.set(positon, element);
    }


    private int loadMatrixDimensions() throws InvalidMatrixDimension {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter matrix dimension (7, 8, 9 or 10): ");
        String userInput = scanner.nextLine();
        if (!userInput.equals("7") && !userInput.equals("8") && !userInput.equals("9") && !userInput.equals("10")) {
            throw new InvalidMatrixDimension();
        }
        return Integer.parseInt(userInput);
    }

    protected static int loadNumberOfPlayers() throws InvalidNumberOfPlayers {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of players (2, 3 or 4): ");
        String userInput = scanner.nextLine();
        if (!userInput.equals("2") && !userInput.equals("3") && !userInput.equals("4")) {
            throw new InvalidNumberOfPlayers();
        }
        return Integer.parseInt(userInput);
    }

    public static Figure[] generateFigures() throws IncorrectColour {
        Random random = new Random();
        String colour = null;
        Figure[] resultFigures = new Figure[NUMBER_OF_FIGURES];
        int randomColour = random.nextInt(4) + 1;
        switch (randomColour) {
            case 1:
                colour = "red";
                break;
            case 2:
                colour = "green";
                break;
            case 3:
                colour = "blue";
                break;
            case 4:
                colour = "yellow";
                break;
        }
        for (int i = 0; i < NUMBER_OF_FIGURES; i++) {
            int randomFigure = random.nextInt(3) + 1;
            switch (randomFigure) {
                case 1:
                    resultFigures[i] = new StandardFigure(colour);
                    break;
                case 2:
                    resultFigures[i] = new FlyingFigure(colour);
                    break;
                case 3:
                    resultFigures[i] = new SuperFastFigure(colour);
                    break;
            }
        }
        return resultFigures;
    }

    private void putIntegersToMatrix() {
        for (int i = 0; i < MATRIX_DIMENSIONS; i++) {
            for (int j = 0; j < MATRIX_DIMENSIONS; j++) {
                MATRIX[i][j] = i * MATRIX_DIMENSIONS + j + 1;
            }
        }
    }

    public static Player[] randomizePlayers(Player[] players) {
        Player[] newPlayers = new Player[players.length];
        List<Integer> randomNumbers = new ArrayList<>();
        Random random = new Random();
        while (randomNumbers.size() != players.length) {
            int randomNumber = random.nextInt(players.length);
            if (!randomNumbers.contains(randomNumber)) {
                randomNumbers.add(randomNumber);
            }
        }
        int k = 0;
        for (int number : randomNumbers) {
            newPlayers[k++] = players[number];
        }
        return newPlayers;
    }

    /*public static void main(String[] args) throws Exception {
        GameMatrix diamondCircle = new GameMatrix();
        System.out.println("Game is about to start, if you want to pause it, input PAUSE, if you want to continue" +
                "input CONTINUE, if you want to end input --exit");
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
    }*/

    /*private boolean checkIfAllKeysAreEmpty(ArrayList<HashMap<Player, Integer>> list, Player[] randomizedPlayers) {
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
    }*/

    /*@Override
    public void run() {
        long startTime = new Date().getTime();
        Player[] randomizedPlayers = randomizePlayers(players);
        Deck deck = new Deck();
        Random random = new Random();
        boolean helpBool = true;
        ArrayList<HashMap<Player, Integer>> list = new ArrayList<>();
        HashMap<Figure, String> figureMap = new HashMap<>();
        initializeHelpCollections(randomizedPlayers, list, figureMap);
        try {
            while (!checkIfAllKeysAreEmpty(list, randomizedPlayers)) {
                outerloop:
                for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
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
                                if (processSimpleCard(randomizedPlayers, list, figureMap, i, whichFigure, (SimpleCard) card))
                                    continue outerloop;
                            }
                            sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            try {
                writeToFiles(startTime, randomizedPlayers, figureMap);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initializeHelpCollections(Player[] randomizedPlayers, ArrayList<HashMap<Player, Integer>> list, HashMap<Figure, String> figureMap) {
        for (Player p : randomizedPlayers) {
            for (Figure f : p.getFigures()) {
                figureMap.put(f, "");
            }
        }
        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            Player p = randomizedPlayers[i];
            HashMap<Player, Integer> map = new HashMap<>();
            map.put(p, NUMBER_OF_FIGURES);
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
        if (fullPosition >= mapTraversal.size()) fullPosition = mapTraversal.size() - 1;
        for (int pos = playerPosition; pos <= fullPosition; pos++) {
            if (pos < mapTraversal.size() - 1 && mapTraversal.get(pos) instanceof Figure) {
                if (pos == fullPosition) {
                    fullPosition++;
                } else continue;
            } else if (pos < mapTraversal.size() && mapTraversal.get(pos) instanceof Hole &&
                    !(randomizedPlayers[i].getFigures()[whichFigure] instanceof FlyingFigure)) {
                System.out.println("FIGURE EATEN!");
                mapTraversal.set(pos, null);
                list.get(i).replace(randomizedPlayers[i], list.get(i).get(randomizedPlayers[i]) - 1);
                sleep(1000);
                return true;
            } else if (pos < mapTraversal.size() && mapTraversal.get(pos) instanceof Bonus) {
                System.out.println("BONUS!");
                mapTraversal.set(pos, null);
                if (fullPosition != mapTraversal.size() - 1)
                    fullPosition++;
            }
            if (pos == mapTraversal.size() - 1) {
                list.get(i).replace(randomizedPlayers[i], list.get(i).get(randomizedPlayers[i]) - 1);
                String newString = figureMap.get(randomizedPlayers[i].getFigures()[whichFigure]) + "" +
                        originalMap.get(pos);
                figureMap.replace(randomizedPlayers[i].getFigures()[whichFigure], newString);
                randomizedPlayers[i].getFigures()[whichFigure].setPosition(pos);
                return true;
            }
            if (pos < mapTraversal.size()) {
                if (pos != mapTraversal.size() - 1) {
                    String newString = figureMap.get(randomizedPlayers[i].getFigures()[whichFigure]) + "" +
                            originalMap.get(pos) + "-";
                    figureMap.replace(randomizedPlayers[i].getFigures()[whichFigure], newString);
                }
            }
        }
        if (fullPosition < mapTraversal.size()) {
            synchronized (randomizedPlayers[i].getFigures()[whichFigure]) {
                randomizedPlayers[i].getFigures()[whichFigure].setPosition(fullPosition);
                mapTraversal.set(fullPosition, randomizedPlayers[i].getFigures()[whichFigure]);
            }
        }
        return false;
    }

    private void processSpecialCard(Random random, int i) {
        Hole[] holes = new Hole[NUMBER_OF_HOLES];
        for (int h = 0; h < holes.length; h++) {
            holes[i] = new Hole();
        }
        List<Integer> holePositions = new ArrayList<>();
        int temp = NUMBER_OF_HOLES;
        while (temp > 0) {
            Integer position = random.nextInt(mapTraversal.size());
            if (!holePositions.contains(position)) {
                holePositions.add(position);
                temp--;
            }
        }
        for (int k = 0; k < holePositions.size(); k++) {
            if (!(mapTraversal.get(k) instanceof Figure) && !(mapTraversal.get(k) instanceof Bonus))
                mapTraversal.set(k, holes[k]);
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
                pw.println("Igrac " + outerCounter + " - " + p.getName() + "\n" +
                        "Figura " + innerCounter++ + " (" + f.checkTypeOfFigure() + ", " + f.getColour() + ") - " +
                        "predjeni put (" + figureMap.get(f) + ") - stigla do cilja " + helpString);
                pw.close();
                sleep(1000);
            }
            outerCounter++;
        }
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                "IGRA_VRIJEME_TRAJANJA")));
        pw.println((new Date().getTime() - startTime) / (double)1000);
        pw.close();
    }*/
}
