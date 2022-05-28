package cards;

public class SimpleCard extends Card {
    private int numberOfFieldsToCross;

    SimpleCard() {

    }

    SimpleCard(CardPicture picture, int fieldNumber) {
        super(picture);
        this.numberOfFieldsToCross = fieldNumber;
    }

    public int getNumberOfFieldsToCross() {
        return numberOfFieldsToCross;
    }

    public void setNumberOfFieldsToCross(int numberOfFieldsToCross) {
        this.numberOfFieldsToCross = numberOfFieldsToCross;
    }
}
