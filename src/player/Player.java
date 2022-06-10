package player;
import figure.Figure;

public class Player {
    private final String name;
    private static int id = 0;
    private final Figure[] figures;

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
}
