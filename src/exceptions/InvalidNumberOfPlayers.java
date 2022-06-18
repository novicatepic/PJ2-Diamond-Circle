package exceptions;

public class InvalidNumberOfPlayers extends Exception {
    public InvalidNumberOfPlayers() {
        super("Invalid number of players!");
    }

    public InvalidNumberOfPlayers(String message) {
        super(message);
    }
}
