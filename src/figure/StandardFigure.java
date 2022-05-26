package figure;

import FigureInterface.IDroppable;
import exceptions.IncorrectColour;

public class StandardFigure extends Figure implements IDroppable {

    public StandardFigure(String colour, int numOfFields) throws IncorrectColour {
        super(colour, numOfFields);
    }


}
