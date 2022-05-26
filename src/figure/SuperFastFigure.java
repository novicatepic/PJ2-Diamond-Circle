package figure;

import FigureInterface.IDroppable;
import exceptions.IncorrectColour;

public class SuperFastFigure extends Figure implements IDroppable {
    public SuperFastFigure(String colour, int numOfFields) throws IncorrectColour {
        super(colour, numOfFields * 2);
    }
}
