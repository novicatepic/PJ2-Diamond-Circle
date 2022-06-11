package figure;

import bonus.Bonus;
import bonus.Diamond;
import game.Game;
import gui.MainFrame;
import game.GameMatrix;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GhostFigure extends Thread {
    private List<Integer> randomPositions;
    private boolean isGhostAlive = true;

    public GhostFigure() {
        super();
    }

    private void findPositionsForGhost() {
        int numberOfDiamonds;
        Random random = new Random();
        do {
            numberOfDiamonds = random.nextInt(GameMatrix.getMatrixDimensions() - 2) + 2;
        } while (numberOfDiamonds > GameMatrix.getNumberOfFreePositionsInMatrix() &&
                GameMatrix.getNumberOfFreePositionsInMatrix() >= 2);

        randomPositions = new ArrayList<>();
        int temp = numberOfDiamonds;
        if (GameMatrix.getNumberOfFreePositionsInMatrix() < 2) {
            temp = 0;
        }
        while (temp > 0) {
            int positionFromMatrix = random.nextInt(GameMatrix.getMapTraversal().size());
            if (!randomPositions.contains(positionFromMatrix) && !(GameMatrix.getMapTraversal().get(positionFromMatrix) instanceof Figure)
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
            while (isGhostAlive) {
                findPositionsForGhost();
                clearDiamonds();
                for (int i = 0; i < randomPositions.size(); i++) {
                    if (Game.pause) {
                        synchronized (this) {
                            wait();
                        }
                    }

                    Integer randomPosition = randomPositions.get(i);
                    Bonus bonus = new Diamond();

                    synchronized (Game.getMainFrame()) {
                        synchronized (GameMatrix.getMapTraversal()) {
                            if (GameMatrix.getMapTraversal().get(randomPosition) instanceof Figure) {
                                Figure f = (Figure) GameMatrix.getMapTraversal().get(randomPosition);
                                f.setBonusCount(f.getBonusCount() + 1);
                            }
                            else {
                                if (!MainFrame.checkIfFieldIsBlack(randomPosition)) {
                                    MainFrame.setBonusLabel(randomPosition);
                                    GameMatrix.setMapTraversal(randomPosition, bonus);
                                }
                            }
                        }
                    }
                }

                sleep(5000);

                if (Game.pause) {
                    synchronized (this) {
                        wait();
                    }
                }

                synchronized (Game.getMainFrame()) {
                    synchronized (GameMatrix.getMapTraversal()) {
                        MainFrame.clearBonuses();
                    }
                }
            }
        } catch (InterruptedException ex) {
            Game.log(ex);
        }
    }

    private void clearDiamonds() {
        for (int i = 0; i < GameMatrix.getMapTraversal().size(); i++) {
            synchronized (Game.getMainFrame()) {
                synchronized (GameMatrix.getMapTraversal()) {
                    if (GameMatrix.getMapTraversal().get(i) instanceof Bonus && !(GameMatrix.getMapTraversal().get(i) instanceof Figure)) {
                        GameMatrix.setMapTraversal(i, null);
                    }
                }
            }
        }
    }
}
