package path;
import java.util.ArrayList;

public class PathClass {

    private Object[][] MATRIX;
    private Integer MATRIX_DIMENSIONS;

    public PathClass(Object[][] matrix, Integer matrixDimensions) {
        MATRIX = matrix;
        MATRIX_DIMENSIONS = matrixDimensions;
    }

    private int[] findBoundaries() {
        int[] boundaries = new int[4];
        boundaries[0] = (Integer) MATRIX[1][1] - (Integer) MATRIX[0][0];
        boundaries[1] = (Integer) MATRIX[1][0] - (Integer) MATRIX[0][1];
        boundaries[2] = -boundaries[0];
        boundaries[3] = -boundaries[1];
        return boundaries;
    }

    private Object[][] shrinkMatrix() {
        int newBoundaries;
        if (MATRIX_DIMENSIONS - 2 < 0) {
            newBoundaries = 1;
        } else {
            newBoundaries = MATRIX_DIMENSIONS - 2;
        }
        Object[][] newMatrix = new Object[newBoundaries][newBoundaries];
        int tmp1 = 0, tmp2 = 0;
        for (int i = 1; i < MATRIX_DIMENSIONS - 1; i++) {
            for (int j = 1; j < MATRIX_DIMENSIONS - 1; j++) {
                newMatrix[tmp1][tmp2++] = MATRIX[i][j];
            }
            tmp1++;
            tmp2 = 0;
        }
        return newMatrix;
    }

    private boolean doesElementExist(int row, int column) {
        return(!(row >= MATRIX_DIMENSIONS || column >= MATRIX_DIMENSIONS || column < 0 || row < 0));
        /*if (row >= MATRIX_DIMENSIONS || column >= MATRIX_DIMENSIONS || column < 0 || row < 0) {
            return false;
        }
        return true;*/
    }

    private ArrayList<Integer> getUnevenMatrixValidPositions(int[] boundaries) {
        ArrayList<Integer> resultSet = new ArrayList<>();
        int startPositionElementIndex = MATRIX_DIMENSIONS / 2 + 1;
        int startPositionElement = (Integer) MATRIX[0][startPositionElementIndex - 1];
        int realStartingPos = startPositionElement;
        int initialValue = boundaries[0];
        int row = 0, column = startPositionElementIndex - 1;
        boolean firstFlag = true;

        while (startPositionElement != realStartingPos || firstFlag) {
            if (firstFlag) {
                firstFlag = false;
            }
            if (!doesElementExist(row, column)) {
                if (initialValue == boundaries[0]) {
                    initialValue = boundaries[1];
                } else if (initialValue == boundaries[1]) {
                    initialValue = boundaries[2];
                } else if (initialValue == boundaries[2]) {
                    initialValue = boundaries[3];
                }
                if (row >= MATRIX_DIMENSIONS) {
                    row -= 2;
                }
                if (column >= MATRIX_DIMENSIONS) {
                    if (initialValue == boundaries[1]) {
                        column -= 2;
                    }
                    if (initialValue == boundaries[3]) {
                        column += 2;
                    }
                }
                if (column < 0) {
                    column += 2;
                }
                startPositionElement += initialValue;
                if (!resultSet.contains(startPositionElement)) {
                    resultSet.add(startPositionElement);
                }
            } else {
                if (!resultSet.contains(startPositionElement)) {
                    resultSet.add(startPositionElement);
                }
                if (initialValue == boundaries[0]) {
                    row++;
                    column++;
                }
                if (initialValue == boundaries[1]) {
                    row++;
                    column--;
                }
                if (initialValue == boundaries[2]) {
                    row--;
                    column--;
                }
                if (initialValue == boundaries[3]) {
                    row--;
                    column++;
                }
                if (doesElementExist(row, column)) {
                    startPositionElement += initialValue;
                }
            }
        }
        return resultSet;
    }

    public ArrayList<Object> getPath() {
        ArrayList<Object> mapTraversal = new ArrayList<>();
        Object[][] tmpMatrix;
        tmpMatrix = MATRIX;
        int oldDimensions = MATRIX_DIMENSIONS;
        int[] boundaries = findBoundaries();
        while (MATRIX_DIMENSIONS > 0) {
            ArrayList<Integer> array = getUnevenMatrixValidPositions(boundaries);
            mapTraversal.addAll(array);
            MATRIX = shrinkMatrix();
            MATRIX_DIMENSIONS -= 2;
        }
        MATRIX = tmpMatrix;
        MATRIX_DIMENSIONS = oldDimensions;
        return mapTraversal;
    }
}
