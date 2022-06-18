package exceptions;

public class DuplicateNamesException extends Exception {
    public DuplicateNamesException() {
        super("Duplicate names not allowed!");
    }

    public DuplicateNamesException(String message) {
        super(message);
    }
}
