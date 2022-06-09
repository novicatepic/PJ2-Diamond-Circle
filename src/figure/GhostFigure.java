package figure;

import bonus.Bonus;
import bonus.Diamond;
import game.Game;
import gui.MainFrame;
import hole.Hole;
import game.GameMatrix;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GhostFigure extends Thread {
    private List<Integer> randomPositions;
    private static Handler ghostHandler;
    private boolean isGhostAlive = true;

    static {
        try {
            ghostHandler = new FileHandler("ghostlog.log");
            Logger.getLogger(GhostFigure.class.getName()).addHandler(ghostHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                    synchronized (Game.getGame()/*s()[Game.getI()]*/) {
                        synchronized (Game.getMainFrame()) {
                            Integer randomPosition = randomPositions.get(i);
                            Bonus bonus = new Diamond();

                            if(GameMatrix.getMapTraversal().get(randomPosition) instanceof Figure) {
                                Figure f = (Figure)GameMatrix.getMapTraversal().get(randomPosition);
                                f.setBonusCount(f.getBonusCount() + 1);
                            }

                            if (!(GameMatrix.getMapTraversal().get(randomPosition) instanceof Figure) &&
                                    !(GameMatrix.getMapTraversal().get(randomPosition) instanceof Hole)) {
                                //Pair oldMatrixPosition = GameMatrix.getMatrixPositionOfElement(randomPosition);
                                if (!MainFrame.checkIfFieldIsBlack(randomPosition)) {
                                    MainFrame.setBonusLabel(randomPosition);
                                    GameMatrix.setMapTraversal(randomPosition, bonus);
                                }
                            }
                        }
                    }
                }
                if (Game.pause) {
                    synchronized (this) {
                        wait();
                    }
                }

                sleep(5000);

                synchronized (Game.getGame()/*s()[Game.getI()]*/) {
                    synchronized (Game.getMainFrame()) {
                        synchronized (GameMatrix.getMapTraversal()) {
                            MainFrame.clearBonuses();
                        }
                    }
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(GhostFigure.class.getName()).log(Level.WARNING, ex.toString());
        }
    }

    private void clearDiamonds() {
        synchronized (Game.getGame()) {
            synchronized (Game.getMainFrame()) {
                synchronized (GameMatrix.getMapTraversal()) {
                    for (int i = 0; i < GameMatrix.getMapTraversal().size(); i++) {
                        if (GameMatrix.getMapTraversal().get(i) instanceof Bonus && !(GameMatrix.getMapTraversal().get(i) instanceof Figure)) {
                            GameMatrix.setMapTraversal(i, null);
                        }
                    }
                }
            }
        }
    }
}
