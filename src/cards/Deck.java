package cards;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Deck {
    private final int NUMBER_OF_CARDS = 52;
    private final int NUMBER_OF_SPECIAL_CARDS = 12;
    private final int NUMBER_OF_SIMPLE_CARDS = 40;
    private final int HOW_MANY_FOR_EACH_IN_SIMPLE_CARDS = 10;
    Queue<Card> cardQueue = new LinkedList<>();

    public Deck() {
        Random random = new Random();
        int special = 0, simple = 0;
        int i = 0;
        int numberOfOnes = 0, numberOfTwos = 0, numberOfThrees = 0, numberOfFours = 0;
        boolean isSpecialLast = false;
        while (i != NUMBER_OF_CARDS) {
            boolean nextBoolean = random.nextBoolean();
            if (nextBoolean && simple != NUMBER_OF_SIMPLE_CARDS) {
                int cardNumber = random.nextInt(4) + 1;
                switch (cardNumber) {
                    case 1:
                        if (numberOfOnes != HOW_MANY_FOR_EACH_IN_SIMPLE_CARDS) {
                            addSimpleCardToQueue(cardNumber);
                            i++;
                            numberOfOnes++;
                            simple++;
                            isSpecialLast = false;
                            break;
                        } else {
                            cardNumber++;
                        }
                    case 2:
                        if (numberOfTwos != HOW_MANY_FOR_EACH_IN_SIMPLE_CARDS) {
                            addSimpleCardToQueue(cardNumber);
                            i++;
                            numberOfTwos++;
                            simple++;
                            isSpecialLast = false;
                            break;
                        } else {
                            cardNumber++;
                        }
                    case 3:
                        if (numberOfThrees != HOW_MANY_FOR_EACH_IN_SIMPLE_CARDS) {
                            addSimpleCardToQueue(cardNumber);
                            i++;
                            numberOfThrees++;
                            simple++;
                            isSpecialLast = false;
                            break;
                        } else {
                            cardNumber++;
                        }
                    case 4:
                        if (numberOfFours != HOW_MANY_FOR_EACH_IN_SIMPLE_CARDS) {
                            addSimpleCardToQueue(cardNumber);
                            i++;
                            numberOfFours++;
                            simple++;
                            isSpecialLast = false;
                        }
                        break;
                }
            } else if (!nextBoolean && special != NUMBER_OF_SPECIAL_CARDS && !isSpecialLast) {
                cardQueue.add(new SpecialCard());
                special++;
                i++;
                isSpecialLast = true;
            }
        }
    }

    private void addSimpleCardToQueue(int cardNumber) {
        cardQueue.add(new SimpleCard(cardNumber));
    }

    public Card pullOutACard() {
        Card pullOut = cardQueue.remove();
        cardQueue.add(pullOut);
        return pullOut;
    }
}
