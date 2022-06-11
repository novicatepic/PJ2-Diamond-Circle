package exceptions;

public class DuplicateNamesException extends Exception {
    public DuplicateNamesException() {
        super();
        System.out.println("Duplicate names not allowed!");
    }

    public DuplicateNamesException(String message) {
        super(message);
    }
}
