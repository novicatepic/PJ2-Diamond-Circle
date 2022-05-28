package main;

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
import figure.*;
import hole.Hole;
import player.Player;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class DiamondCircle extends Thread {

    public static final Integer NUMBER_OF_FIGURES = 4;
    private static int MATRIX_DIMENSIONS;
    private static Object[][] MATRIX;
    private static int NUMBER_OF_PLAYERS;
    private static Player[] players;
    private static ArrayList<Object> mapTraversal;
    private static final int NUMBER_OF_HOLES = 4;

    public static int getMatrixDimensions() {
        return MATRIX_DIMENSIONS;
    }

    public static Object[][] getMatrix() {
        return MATRIX;
    }
    public static ArrayList<Object> getMapTraversal() { return mapTraversal; }
    public static void setMapTraversal(int positon, Object element) {
        mapTraversal.set(positon, element);
    }

    private static int loadMatrixDimensions() throws InvalidMatrixDimension {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter matrix dimension (7, 8, 9 or 10): ");
        String userInput = scanner.nextLine();
        if(!userInput.equals("7") && !userInput.equals("8") && !userInput.equals("9") && !userInput.equals("10")) {
            //scanner.close();
            throw new InvalidMatrixDimension();
        }
        //scanner.close();
        return Integer.parseInt(userInput);
    }

    private static int loadNumberOfPlayers() throws InvalidNumberOfPlayers {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of players (2, 3 or 4): ");
        String userInput = scanner.nextLine();
        if(!userInput.equals("2") && !userInput.equals("3") && !userInput.equals("4")) {
            scanner.close();
            throw new InvalidNumberOfPlayers();
        }
        scanner.close();
        return Integer.parseInt(userInput);
    }

    private static Figure[] generateFigures() throws IncorrectColour {
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
        for(int i = 0; i < NUMBER_OF_FIGURES; i++) {
            int randomFigure = random.nextInt(3) + 1;
            int randomNumOfFields = random.nextInt(10) + 1;
            switch (randomFigure) {
                case 1:
                    resultFigures[i] = new StandardFigure(colour, randomNumOfFields);
                    break;
                case 2:
                    resultFigures[i] = new FlyingFigure(colour, randomNumOfFields);
                    break;
                case 3:
                    resultFigures[i] = new SuperFastFigure(colour, randomNumOfFields);
                    break;
            }
        }

        return resultFigures;
    }

    private static void printMatrix() {
        for(int i = 0; i < MATRIX_DIMENSIONS; i++) {
            for(int j = 0; j < MATRIX_DIMENSIONS; j++) {
                System.out.print(MATRIX[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void putIntegersToMatrix() {
        for(int i = 0; i < MATRIX_DIMENSIONS; i++) {
            for(int j = 0; j < MATRIX_DIMENSIONS; j++) {
                MATRIX[i][j] = i * MATRIX_DIMENSIONS + j + 1;
            }
        }
    }

    private static boolean doesElementExist(int row, int column) {
        if(row >= MATRIX_DIMENSIONS || column >= MATRIX_DIMENSIONS || column < 0 || row < 0) {
            return false;
        }
        return true;
    }

    private static int[] findBoundaries() {
        int[] boundaries = new int[4];
        boundaries[0] = (Integer)MATRIX[1][1] - (Integer)MATRIX[0][0];
        boundaries[1] = (Integer)MATRIX[1][0] - (Integer)MATRIX[0][1];
        boundaries[2] = -boundaries[0];
        boundaries[3] = -boundaries[1];
        return boundaries;
    }

    private static Object[][] shrinkMatrix() {
        int newBoundaries = 0;
        if(MATRIX_DIMENSIONS - 2 < 0) {
            newBoundaries = 1;
        }
        else {
            newBoundaries = MATRIX_DIMENSIONS - 2;
        }
        Object[][] newMatrix = new Object[newBoundaries][newBoundaries];
        /*System.out.println("DIMENSIONS: ");
        System.out.println(MATRIX_DIMENSIONS - counter - 1);*/
        int tmp1 = 0, tmp2 = 0;
        for(int i = 1; i < MATRIX_DIMENSIONS - 1; i++) {
            for(int j = 1; j < MATRIX_DIMENSIONS - 1; j++) {
                newMatrix[tmp1][tmp2++] = MATRIX[i][j];
            }
            tmp1++;
            tmp2 = 0;
        }
        return newMatrix;
    }


    private static ArrayList<Integer> getUnevenMatrixValidPositions(int[] boundaries) {
        ArrayList<Integer> resultSet = new ArrayList<>();
        int startPositionElementIndex = getMatrixDimensions() / 2 + 1;
        int startPositionElement = (Integer)MATRIX[0][startPositionElementIndex - 1];
        int realStartingPos = startPositionElement;
        int initialValue = boundaries[0];
        int row = 0, column = startPositionElementIndex - 1;
        boolean firstFlag = true;

        while(startPositionElement != realStartingPos || firstFlag) {
            if(firstFlag) {
                firstFlag = false;
            }
            if(!doesElementExist(row, column)) {
                if(initialValue == boundaries[0]) {
                    initialValue = boundaries[1];
                }
                else if(initialValue == boundaries[1]) {
                    initialValue = boundaries[2];
                }
                else if(initialValue == boundaries[2]) {
                    initialValue = boundaries[3];
                }
                if(row >= MATRIX_DIMENSIONS) {
                    row -= 2;
                }
                if(column >= MATRIX_DIMENSIONS) {
                    if(initialValue == boundaries[1]) {
                        column -= 2;
                    }
                    if(initialValue == boundaries[3]) {
                        column += 2;
                    }
                }
                if(column < 0) {
                    column += 2;
                }
                startPositionElement += initialValue;
                if(!resultSet.contains(startPositionElement)) {
                    resultSet.add(startPositionElement);
                }
            }
            else {
                if(!resultSet.contains(startPositionElement)) {
                    resultSet.add(startPositionElement);
                }
                if(initialValue == boundaries[0]) {
                    row++;
                    column++;
                }
                if(initialValue == boundaries[1]) {
                    row++;
                    column--;
                }
                if(initialValue == boundaries[2]) {
                    row--;
                    column--;
                }
                if(initialValue == boundaries[3]) {
                    row--;
                    column++;
                }
                if(doesElementExist(row, column)) {
                    startPositionElement += initialValue;
                }
            }
        }
        return resultSet;
    }

    private static void getPath(ArrayList<Object> mapTraversal) {
        Object[][] tmpMatrix;
        tmpMatrix = MATRIX;
        int oldDimensions = MATRIX_DIMENSIONS;
        int[] boundaries = findBoundaries();
        while(MATRIX_DIMENSIONS > 0) {
            ArrayList<Integer> array = getUnevenMatrixValidPositions(boundaries);
            mapTraversal.addAll(array);
            MATRIX = shrinkMatrix();
            MATRIX_DIMENSIONS -= 2;
        }
        MATRIX = tmpMatrix;
        MATRIX_DIMENSIONS = oldDimensions;
    }

    private static Player[] randomizePlayers(Player[] players) {
        Player[] newPlayers = new Player[players.length];
        List<Integer> randomNumbers = new ArrayList<>();
        Random random = new Random();
        while (randomNumbers.size() != players.length) {
            int randomNumber = random.nextInt(players.length);
            if(!randomNumbers.contains(randomNumber)) {
                randomNumbers.add(randomNumber);
            }
        }
        int k = 0;
        for(int number : randomNumbers) {
            newPlayers[k++] = players[number];
        }
        return newPlayers;
    }

    public static void main(String[] args) throws Exception {
        MATRIX_DIMENSIONS = loadMatrixDimensions();
        NUMBER_OF_PLAYERS = loadNumberOfPlayers();
        MATRIX = new Object[MATRIX_DIMENSIONS][MATRIX_DIMENSIONS];
        players = new Player[NUMBER_OF_PLAYERS];
        Figure[] figures = generateFigures();
        for(int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            players[i] = new Player("name", figures);
        }
        putIntegersToMatrix();
        mapTraversal = new ArrayList<>();
        getPath(mapTraversal);

        //GhostFigure ghostFigure = new GhostFigure();

        new DiamondCircle().start();
    }

    private static boolean checkIfAllKeysAreEmpty(ArrayList<HashMap<Player, Integer>> list, Player[] randomizedPlayers) {
        for(HashMap<Player,Integer> elem : list) {
            for(Player p : randomizedPlayers) {
                System.out.println("ELEM GET P" + elem.get(p));
                if(elem.get(p) != 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int getWhichFigureIsPlayerPlayingWith(ArrayList<HashMap<Player, Integer>> list, int pos, Player p) {
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
            default:
                return -1;
        }
    }

    @Override
    public void run() {
        Player[] randomizedPlayers = randomizePlayers(players);
        Deck deck = new Deck();
        Random random = new Random();
        boolean helpBool = true;
        ArrayList<HashMap<Player, Integer>> list = new ArrayList<>();
        HashMap<Figure, String> figureMap = new HashMap<>();
        for(Player p : randomizedPlayers) {
            for(Figure f : p.getFigures()) {
                figureMap.put(f, "");
            }
        }
        for(int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            Player p = randomizedPlayers[i];
            HashMap<Player, Integer> map = new HashMap<>();
            map.put(p, NUMBER_OF_FIGURES);
            list.add(map);
        }
        try {
            while(!checkIfAllKeysAreEmpty(list, randomizedPlayers)) {
                outerloop:
                for(int i = 0; i < NUMBER_OF_PLAYERS; i++)
                {
                    try {
                        int whichFigure = getWhichFigureIsPlayerPlayingWith(list, i, randomizedPlayers[i]);
                        if(helpBool) {
                            new GhostFigure().start();
                            helpBool = false;
                        }
                        Card card = deck.pullOutACard();
                        if(card instanceof SpecialCard) {
                            Hole[] holes = new Hole[NUMBER_OF_HOLES];
                            for(int h = 0; h < holes.length; h++) {
                                holes[i] = new Hole();
                            }
                            List<Integer> holePositions = new ArrayList<>();
                            int temp = NUMBER_OF_HOLES;
                            while(temp > 0) {
                                Integer position = random.nextInt(mapTraversal.size());
                                if (!holePositions.contains(position)) {
                                    holePositions.add(position);
                                    temp--;
                                }
                            }
                            for(int k = 0; k < holePositions.size(); k++) {
                                mapTraversal.set(k, holes[k]);
                            }
                        }
                        else if(card instanceof SimpleCard) {
                            int positionToGoTo = ((SimpleCard) card).getNumberOfFieldsToCross();
                            int playerPosition = randomizedPlayers[i].getFigures()[whichFigure].getPosition();
                            if(randomizedPlayers[i].getFigures()[whichFigure] instanceof SuperFast) {
                                positionToGoTo *= 2;
                            }
                            int fullPosition = playerPosition + positionToGoTo;
                            for(int pos = playerPosition; pos <= fullPosition; pos++) {
                                if(fullPosition < mapTraversal.size() && mapTraversal.get(pos) instanceof Figure) {
                                    if(pos == fullPosition) {
                                        fullPosition++;
                                    }
                                }
                                else if(fullPosition < mapTraversal.size() && mapTraversal.get(pos) instanceof Hole &&
                                        !(randomizedPlayers[i].getFigures()[whichFigure] instanceof IFly)) {
                                    list.get(i).replace(randomizedPlayers[i], list.get(i).get(randomizedPlayers[i]) - 1);
                                    sleep(1000);
                                    continue outerloop;
                                }
                                if(fullPosition < mapTraversal.size() && mapTraversal.get(pos) instanceof Bonus) {
                                    fullPosition++;
                                }
                                if(fullPosition < mapTraversal.size()) {
                                    String newString = figureMap.get(randomizedPlayers[i].getFigures()[whichFigure]) + "" +
                                            mapTraversal.get(pos) + "-";
                                    figureMap.replace(randomizedPlayers[i].getFigures()[whichFigure], newString);
                                }
                            }
                            randomizedPlayers[i].getFigures()[whichFigure].setPosition(fullPosition);
                            if(fullPosition == mapTraversal.size() - 1) {
                                list.get(i).replace(randomizedPlayers[i], list.get(i).get(randomizedPlayers[i]) - 1);
                            }
                            else if(fullPosition < mapTraversal.size()){

                                mapTraversal.set(fullPosition, randomizedPlayers[i].getFigures()[whichFigure]);
                            }
                        }
                        sleep(1000);
                    }
                    catch(InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            try {
                int outerCounter = 0;
                int innerCounter = 0;
                for(Player p : randomizedPlayers) {
                    for(Figure f : p.getFigures()) {
                        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                                "IGRA_" + String.valueOf(new Date().getTime()))));
                        String helpString = null;
                        if(f.didFigureFinish()) {
                            helpString = "DA";
                        }
                        else {
                            helpString = "NE";
                        }
                        pw.println("Igrac " + outerCounter + " - " + p.getName() + "\n" +
                                "Figura " + innerCounter++ + " (" + f.checkTypeOfFigure() + ", " + f.getColour() + ") - " +
                                "predjeni put (" + figureMap.get(f) + ") - stigla do cilja " + helpString);
                        pw.close();
                    }
                    outerCounter++;
                }
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

    }

}
