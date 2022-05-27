package cards;

public class SimpleCard extends Card {
    private int fieldNumber;

    SimpleCard() {

    }

    SimpleCard(CardPicture picture, int fieldNumber) {
        super(picture);
        this.fieldNumber = fieldNumber;
    }

    public int getFieldNumber() {
        return fieldNumber;
    }

    public void setFieldNumber(int fieldNumber) {
        this.fieldNumber = fieldNumber;
    }
}
