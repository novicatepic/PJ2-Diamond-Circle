package figure;

import exceptions.IncorrectColour;
import game.GameMatrix;

import java.awt.*;

abstract public class Figure {
    private final String colour;
    private int position = 0;
    private long time;
    private int bonusCount = 0;
    private String figurePath = "";
    private long startTime;

    public Figure(String colour) throws IncorrectColour {
        startTime = 0;
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
        if("yellow".equalsIgnoreCase(colour)) {
            return Color.YELLOW;
        }
        else if("blue".equalsIgnoreCase(colour)) {
            return Color.BLUE;
        }
        else if("red".equalsIgnoreCase(colour)) {
            return Color.RED;
        }
        else if("green".equalsIgnoreCase(colour)) {
            return Color.GREEN;
        }
        else {
            return null;
        }
    }

    public String getFigurePath() {
        return figurePath;
    }

    public void setFigurePath(String figurePath) {
        this.figurePath = figurePath;
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

    abstract public String checkTypeOfFigure();

    public boolean didFigureFinish() {
        return (position == GameMatrix.getMapTraversal().size());
    }

    public int getBonusCount() {
        return bonusCount;
    }

    public void setBonusCount(int bonusCount) {
        this.bonusCount = bonusCount;
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
