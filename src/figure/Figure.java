package figure;

import exceptions.IncorrectColour;

abstract public class Figure {
    private String colour;
    int numberOfFields;

    public Figure() {

    }

    public Figure(String colour, int numberOfFields) throws IncorrectColour {
        if(!"red".equalsIgnoreCase(colour) && !"green".equalsIgnoreCase(colour) &&
            !"blue".equalsIgnoreCase(colour) && !"yellow".equalsIgnoreCase(colour)) {
            throw new IncorrectColour();
        }
        this.colour = colour;
        this.numberOfFields = numberOfFields;
    }

    public String getColour() {
        return colour;
    }

    void setColour(String colour) {
        this.colour = colour;
    }
}
