package cards;

import java.util.Random;

public class Deck {
    Card[] cards = new Card[52];


    public Deck() {
        Random random = new Random();

        for(int i = 0; i < 16; i++) {
            for(int j = 0; j < 4; j++) {
                Boolean isSpecial = random.nextBoolean();
                CardPicture picture = new CardPicture();
                if(isSpecial) {
                    cards[i + j] = new SpecialCard(picture);
                }
                else {
                    cards[i + j] = new SimpleCard(picture, i + j);
                }
            }
        }

    }

}
