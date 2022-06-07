package figure;

import bonus.Bonus;
import bonus.Diamond;
import game.Game;
import gui.FileForm;
import gui.MainFrame;
import hole.Hole;
import game.GameMatrix;
import pair.Pair;

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
    private Diamond[] diamonds;
    private int numberOfDiamonds;
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
        //findPositionsForGhost();
    }

    private void findPositionsForGhost() {
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
                System.out.println("GHOST");
                for (int i = 0; i < randomPositions.size(); i++) {
                    synchronized (Game.getGames()[Game.getI()]) {
                        synchronized (Game.getMainFrame()) {
                            //synchronized (GameMatrix.getMapTraversal().get(i)) {
                                System.out.println("ENTERED LOOP");
                                Integer randomPosition = randomPositions.get(i);
                                Bonus bonus = new Diamond();

                                /*if (GameMatrix.getMapTraversal().get(randomPosition) instanceof Figure) {
                                    //System.out.println("FIGURE AUTOMATICALLY PICKED UP BONUS!");
                                    Figure f = (Figure) GameMatrix.getMapTraversal().get(randomPosition);
                                    f.setBonusCount(f.getBonusCount() + 1);
                                    //Pair oldMatrixPosition = GameMatrix.getMatrixPositionOfElement(randomPosition);
                                    MainFrame.setBonusLabel(randomPosition, true);
                                }*/

                                if (!(GameMatrix.getMapTraversal().get(randomPosition) instanceof Figure) &&
                                        !(GameMatrix.getMapTraversal().get(randomPosition) instanceof Hole)) {
                                    System.out.println("BONUS SET!");
                                    //Pair oldMatrixPosition = GameMatrix.getMatrixPositionOfElement(randomPosition);
                                    MainFrame.setBonusLabel(randomPosition);
                                    GameMatrix.setMapTraversal(randomPosition, bonus);
                                }
                            //}
                        }
                    }
                }
                if (Game.pause) {
                    synchronized (this) {
                        wait();
                    }
                }

                sleep(5000);

                synchronized (Game.getGames()[Game.getI()]) {
                    synchronized (Game.getMainFrame()) {
                        synchronized (GameMatrix.getMapTraversal()) {
                            System.out.println("MAINFRAME CLEAR");
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
        System.out.println("CLEAR DIAMONDS");
        synchronized (Game.getGames()[Game.getI()]) {
            synchronized (Game.getMainFrame()) {
                synchronized (GameMatrix.getMapTraversal()) {
                    for (int i = 0; i < GameMatrix.getMapTraversal().size(); i++) {
                        //synchronized (GameMatrix.getMapTraversal().get(i)) {
                            if (GameMatrix.getMapTraversal().get(i) instanceof Bonus && !(GameMatrix.getMapTraversal().get(i) instanceof Figure)) {
                                GameMatrix.setMapTraversal(i, null);
                            }
                        //}

                    }
                }
            }
        }
    }
}
