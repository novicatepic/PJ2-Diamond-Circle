package player;
import figure.Figure;
import game.GameMatrix;

public class Player {
    private String name;
    private static int id = 0;
    private Figure[] figures = new Figure[GameMatrix.NUMBER_OF_FIGURES];
    private int bonusCount = 0;

    public Player(String name, Figure[] figures) {
        this.name = name + id++;
        this.figures = figures;
    }

    public String getName() {
        return name;
    }

    public Figure[] getFigures() {
        return figures;
    }

    public int getBonusCount() {
        return bonusCount;
    }

    public void setBonusCount(int bonusCount) {
        this.bonusCount = bonusCount;
    }
}
