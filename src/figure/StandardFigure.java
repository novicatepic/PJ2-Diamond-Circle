package figure;

import FigureInterface.IDroppable;
import exceptions.IncorrectColour;

public class StandardFigure extends Figure implements IDroppable {

    public StandardFigure(String colour) throws IncorrectColour {
        super(colour);
    }

    @Override
    public String checkTypeOfFigure() {
        return "STANDARD";
    }

}
