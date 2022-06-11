package player;
import figure.Figure;

public class Player {
    private final String name;
    private final Figure[] figures;

    public Player(String name, Figure[] figures) {
        this.name = name;
        this.figures = figures;
    }

    public String getName() {
        return name;
    }

    public Figure[] getFigures() {
        return figures;
    }
}
