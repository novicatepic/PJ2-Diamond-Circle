package exceptions;

public class InvalidMatrixDimension extends  Exception {
    public  InvalidMatrixDimension() {
        super("Invalid matrix dimensions!");
    }

    public InvalidMatrixDimension(String message) {
        super(message);
    }
}
