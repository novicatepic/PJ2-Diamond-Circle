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

import java.util.Random;
import java.util.Scanner;

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
            scanner.close();
            throw new InvalidMatrixDimension();
        }
        scanner.close();
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



    public static void main(String[] args) throws Exception {
        MATRIX_DIMENSIONS = loadMatrixDimensions();
        final int NUMBER_OF_PLAYERS = loadNumberOfPlayers();
        MATRIX = new Object[MATRIX_DIMENSIONS][MATRIX_DIMENSIONS];
        Player[] players = new Player[NUMBER_OF_PLAYERS];
        Figure[] figures = generateFigures();
        for(int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            players[i] = new Player("name", figures);
        }
    }
}
