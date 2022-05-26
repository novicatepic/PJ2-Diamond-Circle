package exceptions;

public class InvalidNumberOfPlayers extends Exception {
    public InvalidNumberOfPlayers() {
        System.out.println("Invalid number of players!");
    }

    public InvalidNumberOfPlayers(String message) {
        super(message);
    }
}
