package cards;

public class SimpleCard extends Card {
    private final int numberOfFieldsToCross;

    SimpleCard(int fieldNumber) {
        this.numberOfFieldsToCross = fieldNumber;
    }

    public int getNumberOfFieldsToCross() {
        return numberOfFieldsToCross;
    }

    @Override
    public String getCardType() {
        return "SIMPLE";
    }

}
