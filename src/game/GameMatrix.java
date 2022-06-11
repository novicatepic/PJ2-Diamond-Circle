package game;

import bonus.Bonus;
import exceptions.*;
import figure.*;
import hole.Hole;
import pair.Pair;
import path.PathClass;
import player.Player;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class GameMatrix {

    public static final Integer NUMBER_OF_FIGURES = 4;
    public static final int NUMBER_OF_HOLES = 20;
    private final String propertyPath = "./config.properties";
    private static int MATRIX_DIMENSIONS;
    private static Object[][] MATRIX;
    private static int NUMBER_OF_PLAYERS;
    private static Player[] players;
    private static ArrayList<Object> mapTraversal;
    private static final ArrayList<Object> originalMap = new ArrayList<>();
    String[] colours = new String[]{"yellow", "blue", "red", "green"};
    ArrayList<String> coloursList = new ArrayList<>(Arrays.asList(colours));

    GameMatrix() throws InvalidMatrixDimension, InvalidNumberOfPlayers,
            IncorrectColour, IOException, InvalidNumberOfNamesException, DuplicateNamesException {
        FileInputStream fis = new FileInputStream(propertyPath);
        Properties properties = new Properties();
        properties.load(fis);
        MATRIX_DIMENSIONS = Integer.parseInt(properties.getProperty("MATRIX-DIMENSIONS"));
        NUMBER_OF_PLAYERS = Integer.parseInt(properties.getProperty("NUMBER_OF_PLAYERS"));
        if(MATRIX_DIMENSIONS < 7 || MATRIX_DIMENSIONS > 10) {
            throw new InvalidMatrixDimension();
        }
        if(NUMBER_OF_PLAYERS < 2 || NUMBER_OF_PLAYERS > 4) {
            throw new InvalidNumberOfPlayers();
        }
        MATRIX = new Object[MATRIX_DIMENSIONS][MATRIX_DIMENSIONS];
        putIntegersToMatrix();
        PathClass pathClass = new PathClass(MATRIX, MATRIX_DIMENSIONS);
        mapTraversal = new ArrayList<>();
        mapTraversal = pathClass.getPath();
        originalMap.addAll(mapTraversal);
        players = new Player[NUMBER_OF_PLAYERS];

        String[] playerNames = properties.get("playerNames").toString().split(":");
        if(playerNames.length != NUMBER_OF_PLAYERS) {
            throw new InvalidNumberOfNamesException();
        }

        checkIfSameNamesExist(playerNames);

        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            Figure[] figures = generateFigures();
            players[i] = new Player(playerNames[i], figures);
        }
    }

    private void checkIfSameNamesExist(String[] playerNames) throws DuplicateNamesException {
        for(int i = 0; i < playerNames.length - 1; i++) {
            for(int j = i + 1; j < playerNames.length; j++) {
                if(playerNames[i].equalsIgnoreCase(playerNames[j])) {
                    throw new DuplicateNamesException();
                }
            }
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

    public static void setMapTraversal(int position, Object element) {
        mapTraversal.set(position, element);
    }

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

    /*public static void printMatrix() {
        for(int i = 0; i < originalMap.size(); i++) {
            System.out.print(originalMap.get(i) + " ");
        }
    }*/

}