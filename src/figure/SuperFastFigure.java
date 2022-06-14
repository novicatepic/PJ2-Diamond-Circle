package figure;

import FigureInterface.IDroppable;
import FigureInterface.SuperFast;
import exceptions.IncorrectColour;

public class SuperFastFigure extends Figure implements IDroppable, SuperFast {
    public SuperFastFigure(String colour) throws IncorrectColour {
        super(colour);
    }

    @Override
    public String checkTypeOfFigure() {
        return "SUPER";
    }
}
