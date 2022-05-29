package figure;

import bonus.Bonus;
import bonus.Diamond;
import hole.Hole;
import main.DiamondCircle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GhostFigure extends Thread {
    private Diamond[] diamonds;
    private int numberOfDiamonds;
    private List<Integer> randomPositions;

    public GhostFigure() {
        super();
        Random random = new Random();
        numberOfDiamonds = random.nextInt(DiamondCircle.getMatrixDimensions() - 2) + 2;
        randomPositions = new ArrayList<>();
        int temp = numberOfDiamonds;
        //System.out.println("NUMBER OF ELEMS " + DiamondCircle.getMapTraversal().size());
        while(temp > 0) {
            int positionFromMatrix = random.nextInt(DiamondCircle.getMapTraversal().size());
            if(!randomPositions.contains(positionFromMatrix)) {
                randomPositions.add(positionFromMatrix);
                temp--;
            }
        }
        randomPositions = randomPositions.stream().sorted().collect(Collectors.toList());
        /*for(int position : randomPositions) {
            System.out.println(position + " ");
        }*/
    }

    @Override
    public void run() {
        try {
            for(int i = 0; i < randomPositions.size(); i++) {
                Integer randomPosition = randomPositions.get(i);
                Bonus bonus = new Diamond();
                if(!(DiamondCircle.getMapTraversal().get(randomPosition) instanceof Figure) &&
                        !(DiamondCircle.getMapTraversal().get(randomPosition) instanceof Hole)) {
                    DiamondCircle.setMapTraversal(randomPosition, bonus);
                    sleep(5000);
                }
            }
        }
        catch(InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
