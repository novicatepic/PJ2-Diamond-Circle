package cards;

public class CardPicture {
    static int id = 0;

    public CardPicture() {
        id++;
    }

    public int getId() {
        return id;
    }
}
