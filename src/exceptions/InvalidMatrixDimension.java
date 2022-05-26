package exceptions;

public class InvalidMatrixDimension extends  Exception {
    public  InvalidMatrixDimension() {
        System.out.println("Invalid matrix dimensions!");
    }

    public InvalidMatrixDimension(String message) {
        super(message);
    }
}
