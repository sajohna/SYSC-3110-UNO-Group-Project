import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private ArrayList<Card> drawPile;

    private int numDrawCards;

    /**
     * Constructs a new Uno drawPile by creating and populating it with Uno cards.
     */
    public Deck() {
        drawPile = new ArrayList<Card>();
        makePile();
    }

    /**
     * Constructs a drawPile of Uno cards from the provided ArrayList of cards.
     *
     * @param cards An ArrayList of Uno cards to initialize the drawPile.
     */
    public Deck(ArrayList<Card> cards) {
        this.drawPile = cards;
        numDrawCards = cards.size();
    }

    /**
     * Gets the number of cards remaining in the drawPile.
     *
     * @return The count of cards in the drawPile.
     */
    public int getNumDrawCards() {
        return numDrawCards;
    }

    /**
     * Gets a specific card from the drawPile at index i
     *
     * @param i, the index of the card
     * @return the Card from the drawPile
     */
    public Card getCard(int i) {
        return drawPile.get(i);
    }


    /**
     * Returns the all the cards in the drawPile as an ArrayList
     *
     * @return an ArrayList of Cards
     */
    public ArrayList<Card> getCards() {
        return drawPile;
    }

    /**
     * Draws a card from the drawPile, and reduces the count of cards in the drawPile.
     *
     * @return The drawn Uno card.
     */
    public Card draw() {
        Card card = drawPile.remove(0);
        numDrawCards--;
        return card;
    }

    /**
     * Checks if the drawPile is empty (contains no more cards).
     *
     * @return True if the drawPile is empty, otherwise false.
     */
    public boolean isEmpty() {
        if (numDrawCards == 0) {
            return true;
        }
        return false;
    }

    /**
     * Adds a full drawPile of Uno cards and shuffles the drawPile.
     */
    public void makePile() {

        for (Card.CardColour Colour : Card.CardColour.values()) {
            if (!Colour.equals(Card.Colour.WILD)) {
                for (Card.CardValue Type : Card.CardValue.values()) {
                    if (!Type.equals(Card.CardValue.WILD) && !Type.equals(Card.CardValue.WILD_DRAW_TWO)) {
                        drawPile.add(new Card(Type, Colour));
                        drawPile.add(new Card(Type, Colour));
                        numDrawCards += 2;
                    }
                }
            } else {
                for (int i = 4; i > 0; i--) {
                    drawPile.add(new Card(Card.CardValue.WILD, Card.CardColour.WILD));
                    drawPile.add(new Card(Card.CardValue.WILD_DRAW_TWO, Card.CardColour.WILD));
                    numDrawCards += 2;
                }

            }
        }
        Collections.shuffle(drawPile);
    }
}