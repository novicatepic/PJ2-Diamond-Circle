package figure;

import FigureInterface.IDroppable;
import FigureInterface.SuperFast;
import exceptions.IncorrectColour;

public class SuperFastFigure extends Figure implements IDroppable, SuperFast {
    public SuperFastFigure(String colour, int numOfFields) throws IncorrectColour {
        super(colour, numOfFields * 2);
    }
}
