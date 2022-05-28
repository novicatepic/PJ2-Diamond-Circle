package player;
import figure.Figure;
import main.DiamondCircle;

public class Player {
    private String name;
    private static int id = 0;
    private Figure[] figures = new Figure[DiamondCircle.NUMBER_OF_FIGURES];

    public Player(String name, Figure[] figures) {
        this.name = name + id;
        this.figures = figures;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Figure[] getFigures() {
        return figures;
    }

    public void setFigures(Figure[] figures) {
        this.figures = figures;
    }

}
