package exceptions;

public class IncorrectColour extends Exception {
    public IncorrectColour() {
        super("Colour not correct!");
    }

    public IncorrectColour(String message) {
        super(message);
    }
}
