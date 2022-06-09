package game;

import bonus.Bonus;
import exceptions.IncorrectColour;
import exceptions.InvalidMatrixDimension;
import exceptions.InvalidNumberOfPlayers;
import figure.*;
import hole.Hole;
import pair.Pair;
import path.PathClass;
import player.Player;
import java.util.*;

public class GameMatrix {

    public static final Integer NUMBER_OF_FIGURES = 4;
    private static final int MATRIX_DIMENSIONS = 7;
    private static Object[][] MATRIX;
    private static final int NUMBER_OF_PLAYERS = 2;
    private static Player[] players;
    private static ArrayList<Object> mapTraversal;
    private static final ArrayList<Object> originalMap = new ArrayList<>();
    public static final int NUMBER_OF_HOLES = 20;
    String[] colours = new String[]{"yellow", "blue", "red", "green"};
    ArrayList<String> coloursList = new ArrayList<>(Arrays.asList(colours));

    GameMatrix() throws InvalidMatrixDimension, InvalidNumberOfPlayers, IncorrectColour {
        if(MATRIX_DIMENSIONS < 7 || MATRIX_DIMENSIONS > 10) {
            throw new InvalidMatrixDimension();
        }
        if(NUMBER_OF_PLAYERS < 2 || NUMBER_OF_PLAYERS > 4) {
            throw new InvalidNumberOfPlayers();
        }
        //MATRIX_DIMENSIONS = loadMatrixDimensions();
        //NUMBER_OF_PLAYERS = loadNumberOfPlayers();
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

    public static Object[][] getMATRIX() {
        return MATRIX;
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

    public static Player[] getPlayers() {
        return players;
    }

    public static int getNumberOfPlayers() {
        return NUMBER_OF_PLAYERS;
    }

    public static void setMapTraversal(int positon, Object element) {
        mapTraversal.set(positon, element);
    }

    /*private int loadMatrixDimensions() throws InvalidMatrixDimension {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter matrix dimension (7, 8, 9 or 10): ");
        String userInput = scanner.nextLine();
        if (!userInput.equals("7") && !userInput.equals("8") && !userInput.equals("9") && !userInput.equals("10")) {
            throw new InvalidMatrixDimension();
        }
        return Integer.parseInt(userInput);
    }

    private int loadNumberOfPlayers() throws InvalidNumberOfPlayers {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of players (2, 3 or 4): ");
        String userInput = scanner.nextLine();
        if (!userInput.equals("2") && !userInput.equals("3") && !userInput.equals("4")) {
            throw new InvalidNumberOfPlayers();
        }
        return Integer.parseInt(userInput);
    }*/

    private Figure[] generateFigures() throws IncorrectColour {
        Random random = new Random();
        String colour;
        Figure[] resultFigures = new Figure[NUMBER_OF_FIGURES];
        int randomColour = random.nextInt(coloursList.size());
        colour = coloursList.get(randomColour);
        coloursList.remove(colour);
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

    public static Pair getMatrixPositionOfElement(int position) {
        int element = (Integer)originalMap.get(position);
        for(int i = 0; i < MATRIX_DIMENSIONS; i++) {
            for(int j = 0; j < MATRIX_DIMENSIONS; j++) {
                if((Integer)MATRIX[i][j] == element) {
                    return new Pair(i, j);
                }
            }
        }
        return null;
    }

    public static int getNumberOfFreePositionsInMatrix() {
        int freePositionCounter = 0;
        for(int i = 0; i < GameMatrix.getMapTraversal().size(); i++) {
            if(!(GameMatrix.getMapTraversal().get(i) instanceof Bonus) && !(GameMatrix.getMapTraversal().get(i) instanceof Figure)
                    && !(GameMatrix.getMapTraversal().get(i) instanceof Hole)) {
                freePositionCounter++;
            }
        }
        return freePositionCounter;
    }

}