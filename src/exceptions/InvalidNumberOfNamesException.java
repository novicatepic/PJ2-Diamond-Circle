package exceptions;

public class InvalidNumberOfNamesException extends Exception {
    public InvalidNumberOfNamesException() {
        super();
        System.out.println("Too many or too little player names, check configuration file!");
    }

    public InvalidNumberOfNamesException(String message) {
        super(message);
    }
}
