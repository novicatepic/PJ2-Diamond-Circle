package cards;

import java.util.Random;

public class Deck {
    Card[] cards = new Card[52];


    public Deck() {
        Random random = new Random();
        for(int i = 0; i < 52; i++) {
            CardPicture picture = new CardPicture();
            int whichCard = random.nextInt(52);
            if(whichCard >= 39) {
                cards[i] = new SpecialCard(picture);
            }
            else {
                int randomNumberOfFields = random.nextInt(5) + 1;
                cards[i] = new SimpleCard(picture, randomNumberOfFields);
            }
        }
    }

    public Card pullOutACard() {
        Random random = new Random();
        return cards[random.nextInt(52)];
    }
}
