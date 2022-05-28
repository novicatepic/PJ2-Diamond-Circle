package cards;

import java.util.Random;

public class Deck {
    Card[] cards = new Card[52];


    public Deck() {
        Random random = new Random();
        for(int i = 0; i < 13; i++) {
            for(int j = 0; j < 4; j++) {
                CardPicture picture = new CardPicture();
                if(random.nextBoolean()) {
                    cards[i + j] = new SpecialCard(picture);
                }
                else {
                    cards[i + j] = new SimpleCard(picture, i + j);
                }
            }
        }
    }

    public Card pullOutACard() {
        Random random = new Random();
        return cards[random.nextInt(52)];
    }

}
