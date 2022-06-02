package cards;

public class SimpleCard extends Card {
    private int numberOfFieldsToCross;

    SimpleCard() {

    }

    SimpleCard(int fieldNumber) {
        this.numberOfFieldsToCross = fieldNumber;
    }

    public int getNumberOfFieldsToCross() {
        return numberOfFieldsToCross;
    }

}
