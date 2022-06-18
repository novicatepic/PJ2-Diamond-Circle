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

    @Override
    public String toString() {
        String resultString = "Igrac " + " - " + getName() + "\n" ;
        for (Figure f : figures) {
            String helpString;
            if (f.didFigureFinish()) {
                helpString = "DA";
            } else {
                helpString = "NE";
            }
            resultString += ("Figura " + " (" + f.checkTypeOfFigure() + ", " + f.getColour() + ") - " +
                    "predjeni put (" + f.getFigurePath() + ") - stigla do cilja " + helpString + "\n" +
                    "Ukupno vrijeme igranja sa figurom: " + f.getTime() / (double) 1000 + "s\n");
        }
        return resultString;
    }

}
