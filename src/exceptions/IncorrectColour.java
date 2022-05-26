package exceptions;

public class IncorrectColour extends Exception {
    public IncorrectColour() {
        System.out.println("Colour not correct!");
    }

    public IncorrectColour(String message) {
        super(message);
    }
}
