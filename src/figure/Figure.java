package figure;

import exceptions.IncorrectColour;
import game.GameMatrix;

import java.awt.*;

abstract public class Figure {
    private String colour;
    private int position;
    private long time;

    public Figure() {
        position = 0;
        time = 0;
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

    public Color getRealColour() {
        if("yellow".equals(colour)) {
            return Color.YELLOW;
        }
        else if("blue".equals(colour)) {
            return Color.BLUE;
        }
        else if("red".equals(colour)) {
            return Color.RED;
        }
        else if("green".equals(colour)) {
            return Color.green;
        }
        else {
            return null;
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String checkTypeOfFigure() {
        if(this instanceof FlyingFigure) {
            return "FF";
        }
        else if(this instanceof StandardFigure) {
            return "SF";
        }
        else {
            return "SF";
        }
    }

    public boolean didFigureFinish() {
        return (position == GameMatrix.getMapTraversal().size() - 1);
    }
}
