package figure;

import exceptions.IncorrectColour;
import main.DiamondCircle;

abstract public class Figure {
    private String colour;
    private int position;
    private final Object lock = new Object();

    public Figure() {
        position = 0;
    }

    public Figure(String colour) throws IncorrectColour {
        if(!"red".equalsIgnoreCase(colour) && !"green".equalsIgnoreCase(colour) &&
            !"blue".equalsIgnoreCase(colour) && !"yellow".equalsIgnoreCase(colour)) {
            throw new IncorrectColour();
        }
        this.colour = colour;
    }

    public String getColour() {
        return colour;
    }

    void setColour(String colour) {
        this.colour = colour;
    }

    public int getPosition() {
        //synchronized (lock) {
            return position;
        //}
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String checkTypeOfFigure() {
        if(this instanceof FlyingFigure) {
            return "Lebdeca figura";
        }
        else if(this instanceof StandardFigure) {
            return "Standardna figura";
        }
        else {
            return "Super brza figura";
        }
    }

    public boolean didFigureFinish() {
        return (position == DiamondCircle.getMapTraversal().size() - 1);
    }
}
