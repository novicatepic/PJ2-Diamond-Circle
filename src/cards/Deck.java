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
                        }
                        break;
                    case 2:
                        if(numberOfTwos != HOW_MANY_FOR_EACH_IN_SIMPLE_CARDS) {
                            cards[i++] = new SimpleCard(cardNumber);
                            numberOfTwos++;
                            simple++;
                        }
                        break;
                    case 3:
                        if(numberOfThrees != HOW_MANY_FOR_EACH_IN_SIMPLE_CARDS) {
                            cards[i++] = new SimpleCard(cardNumber);
                            numberOfThrees++;
                            simple++;
                        }
                        break;
                    case 4:
                        if(numberOfFours != HOW_MANY_FOR_EACH_IN_SIMPLE_CARDS) {
                            cards[i++] = new SimpleCard(cardNumber);
                            numberOfFours++;
                            simple++;
                        }
                        break;
                    /*default:
                        System.out.println("Entered default!");*/
                }
            }
            else if(!nextBoolean && special != NUMBER_OF_SPECIAL_CARDS){
                cards[i++] = new SpecialCard();
                special++;
            }
        }
    }

    public Card pullOutACard() {
        Random random = new Random();
        return cards[random.nextInt(52)];
    }
}
