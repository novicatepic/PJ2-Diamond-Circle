package figure;

import FigureInterface.IFly;
import exceptions.IncorrectColour;

public class FlyingFigure extends Figure implements IFly {

    public FlyingFigure(String colour, int numOfFields) throws IncorrectColour {
        super(colour, numOfFields);
    }

}
