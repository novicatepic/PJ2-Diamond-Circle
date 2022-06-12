package cards;

import java.util.Random;

public class Deck {
    private final int NUMBER_OF_CARDS = 52;
    private final int NUMBER_OF_SPECIAL_CARDS = 12;
    private final int NUMBER_OF_SIMPLE_CARDS = 40;
    private final int HOW_MANY_FOR_EACH_IN_SIMPLE_CARDS = 10;
    Card[] cards = new Card[NUMBER_OF_CARDS];

    public Deck() {
        Random random = new Random();
        int special = 0, simple = 0;
        int i = 0;
        int numberOfOnes = 0, numberOfTwos = 0, numberOfThrees = 0, numberOfFours = 0;
        while(i != NUMBER_OF_CARDS) {
            boolean nextBoolean = random.nextBoolean();
            if(nextBoolean && simple != NUMBER_OF_SIMPLE_CARDS) {
                int cardNumber = random.nextInt(4) + 1;
                switch (cardNumber) {
                    case 1:
                        if(numberOfOnes != HOW_MANY_FOR_EACH_IN_SIMPLE_CARDS) {
                            cards[i++] = new SimpleCard(cardNumber);
                            numberOfOnes++;
                            simple++;
                            break;
                        }
                        else {
                            cardNumber++;
                        }
                    case 2:
                        if(numberOfTwos != HOW_MANY_FOR_EACH_IN_SIMPLE_CARDS) {
                            cards[i++] = new SimpleCard(cardNumber);
                            numberOfTwos++;
                            simple++;
                            break;
                        }
                        else {
                            cardNumber++;
                        }
                    case 3:
                        if(numberOfThrees != HOW_MANY_FOR_EACH_IN_SIMPLE_CARDS) {
                            cards[i++] = new SimpleCard(cardNumber);
                            numberOfThrees++;
                            simple++;
                            break;
                        }
                        else {
                            cardNumber++;
                        }
                    case 4:
                        if(numberOfFours != HOW_MANY_FOR_EACH_IN_SIMPLE_CARDS) {
                            cards[i++] = new SimpleCard(cardNumber);
                            numberOfFours++;
                            simple++;
                        }
                        break;
                }
            }
            else if(!nextBoolean && special != NUMBER_OF_SPECIAL_CARDS){
                if(i > 0 && !(cards[i-1] instanceof SpecialCard) && i != NUMBER_OF_CARDS - 1) {
                    cards[i++] = new SpecialCard();
                    special++;
                }
            }
        }
    }

    public Card pullOutACard() {
        Card pullOut = cards[0];

        for(int i = 1; i < NUMBER_OF_CARDS; i++) {
            cards[i - 1] = cards[i];
        }
        cards[NUMBER_OF_CARDS - 1] = pullOut;

        return pullOut;
    }
}
