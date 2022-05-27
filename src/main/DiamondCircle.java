package main;

import bonus.Diamond;
import exceptions.IncorrectColour;
import exceptions.InvalidMatrixDimension;
import exceptions.InvalidNumberOfPlayers;
import figure.Figure;
import figure.FlyingFigure;
import figure.StandardFigure;
import figure.SuperFastFigure;
import player.Player;

import java.util.*;

public class DiamondCircle {

    public static final int NUMBER_OF_FIGURES = 4;
    private static int MATRIX_DIMENSIONS;
    private static Object[][] MATRIX;

    public static int getMatrixDimensions() {
        return MATRIX_DIMENSIONS;
    }

    public static Object[][] getMatrix() {
        return MATRIX;
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

    private static ArrayList<Integer> getMiddleElements() {
        ArrayList<Integer> middleElements = new ArrayList<>();
        int column = getMatrixDimensions() / 2;
        for(int i = 0; i < MATRIX_DIMENSIONS / 2 + 1; i++) {
            for(int j = 0; j < MATRIX_DIMENSIONS; j++) {
                if(j == column) {
                    middleElements.add((Integer)(MATRIX[i][j]));
                }
            }
        }
        return middleElements;
    }

    static int counter = 1;
    private static Object[][] shrinkMatrix() {
        Object[][] newMatrix = new Object[MATRIX_DIMENSIONS - counter][MATRIX_DIMENSIONS - counter];
        int tmp1 = 0, tmp2 = 0;
        for(int i = counter; i < MATRIX_DIMENSIONS; i++) {
            for(int j = counter; j < MATRIX_DIMENSIONS; j++) {
                newMatrix[tmp1][tmp2++] = (Integer)MATRIX[i][j];
            }
            tmp1++;
            tmp2 = 0;
        }
        counter++;
        return newMatrix;
    }

    private static ArrayList<Integer> getUnevenMatrixValidPositions() {
        putIntegersToMatrix();
        boolean countable = true;
        int countableCounter = 0;
        ArrayList<Integer> resultSet = new ArrayList<>();
        ArrayList<Integer> middleElements = getMiddleElements();
        int startPositionElement = getMatrixDimensions() / 2 + 1;
        int[] boundaries = findBoundaries();
        int realStartingPos = startPositionElement;
        int initialValue = boundaries[0];
        int row = 0, column = startPositionElement - 1;
        boolean firstFlag = true;
        int tmpCounter = 0;

        while(/*true*/startPositionElement != realStartingPos || firstFlag) {
            System.out.println("Row: " + row);
            System.out.println("Column: " + column);
            tmpCounter++;
            if(firstFlag) {
                firstFlag = false;
            }
            if(!doesElementExist(row, column)) {
                if(initialValue == boundaries[0] /*|| tmpCounter == countableCounter*/) {
                    initialValue = boundaries[1];
                }
                else if(initialValue == boundaries[1] /*|| tmpCounter == countableCounter*/) {
                    initialValue = boundaries[2];
                }
                else if(initialValue == boundaries[2] /*|| tmpCounter == countableCounter*/) {
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
                if(countable) {
                    countableCounter++;
                }
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
                else {
                    countableCounter--;
                    countable = false;
                }
            }

            if(middleElements.contains(startPositionElement + 1) && !resultSet.contains(startPositionElement + 1)) {
                initialValue = boundaries[0];
                startPositionElement += 1;
                countableCounter--;
                resultSet.add(startPositionElement);
            }
            else if(middleElements.get(middleElements.size() / 2) == startPositionElement + 1
            && resultSet.contains(startPositionElement + 1)) {
                break;
            }
        }
        System.out.println(countableCounter);
        return resultSet;
    }

    public static void main(String[] args) throws Exception {
        MATRIX_DIMENSIONS = loadMatrixDimensions();
        final int NUMBER_OF_PLAYERS = loadNumberOfPlayers();
        MATRIX = new Object[MATRIX_DIMENSIONS][MATRIX_DIMENSIONS];
        Player[] players = new Player[NUMBER_OF_PLAYERS];
        Figure[] figures = generateFigures();
        for(int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            players[i] = new Player("name", figures);
        }
        putIntegersToMatrix();
        printMatrix();
        ArrayList<Integer> array = getUnevenMatrixValidPositions();
        for(int element : array) {
            System.out.print(element + " ");
        }
        /*System.out.println();
        MATRIX = shrinkMatrix();
        ArrayList<Integer> otherArray = getUnevenMatrixValidPositions();
        for(int element : otherArray) {
            System.out.print(element + " ");
        }*/
    }
}
