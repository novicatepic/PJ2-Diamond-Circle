package figure;

import bonus.Bonus;
import bonus.Diamond;
import hole.Hole;
import main.Game;
import main.GameMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GhostFigure extends Thread {
    private Diamond[] diamonds;
    private int numberOfDiamonds;
    private List<Integer> randomPositions;

    private boolean isGhostAlive = true;

    public GhostFigure() {
        super();
        Random random = new Random();
        do {
                //System.out.println("GHOST: " + GameMatrix.getNumberOfFreePositionsInMatrix());
            numberOfDiamonds = random.nextInt(GameMatrix.getMatrixDimensions() - 2) + 2;
        } while (numberOfDiamonds > GameMatrix.getNumberOfFreePositionsInMatrix() &&
                GameMatrix.getNumberOfFreePositionsInMatrix() >= 2);

        randomPositions = new ArrayList<>();
        int temp = numberOfDiamonds;
        if(GameMatrix.getNumberOfFreePositionsInMatrix() < 2) {
            temp = 0;
        }
        while(temp > 0) {
            int positionFromMatrix = random.nextInt(GameMatrix.getMapTraversal().size());
            if(!randomPositions.contains(positionFromMatrix) && !(GameMatrix.getMapTraversal().get(positionFromMatrix) instanceof Figure)
                    && !(GameMatrix.getMapTraversal().get(positionFromMatrix) instanceof Bonus)) {
                randomPositions.add(positionFromMatrix);
                temp--;
            }
        }
        randomPositions = randomPositions.stream().sorted().collect(Collectors.toList());
    }

    public void setGhostAlive() {
        isGhostAlive = false;
    }

    @Override
    public void run() {
        try {
            while(isGhostAlive) {
                clearDiamonds();
                for(int i = 0; i < randomPositions.size(); i++) {
                    //System.out.println("GHOST");
                    Integer randomPosition = randomPositions.get(i);
                    Bonus bonus = new Diamond();
                    if(!(GameMatrix.getMapTraversal().get(randomPosition) instanceof Figure) &&
                            !(GameMatrix.getMapTraversal().get(randomPosition) instanceof Hole)) {
                        GameMatrix.setMapTraversal(randomPosition, bonus);
                        //sleep(5000);
                    }
                }
            }
        }
        catch(/*InterruptedException*/ Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearDiamonds() {
        for(int i = 0; i < GameMatrix.getMapTraversal().size(); i++) {
            if(GameMatrix.getMapTraversal().get(i) instanceof Bonus) {
                GameMatrix.setMapTraversal(i, null);
            }
        }
    }
}
