package figure;

import bonus.Diamond;
import main.DiamondCircle;

import java.util.Random;

public class GhostFigure extends Thread {
    private Diamond[] diamonds;
    private int numberOfDiamonds;
    private int randomPositions;

    public GhostFigure() {
        super();
        Random random = new Random();
        numberOfDiamonds = random.nextInt(DiamondCircle.getMatrixDimensions() - 2) + 2;
    }

    @Override
    public void run() {

    }
}
