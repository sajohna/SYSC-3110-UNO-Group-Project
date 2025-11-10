import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a deck of Uno cards which is the draw pile in the game that the users draw from.
 * Manages the creation, shiffling and drawing of cards from the draw pile
 * 
 * This class handles:
 * - Creation of a UNO deck
 * - Shuffling the deck
 * - Drawing a card from the deck
 * - Check if the deck is empty
 * - Get the number of remaining cards in the deck
 * - Get all cards or specific card in the deck 
 * 
 * Data Structure Design:
 * - ArrayList of Card_Model: Stores the cards in the draw pile
 *   ArrayList choosen for:
 *      * Dynamic sizing: can grow/shrink as cards are drawn from the draw pile
 *      * Easy access: allows efficient access to cards by index
 *      * Built-in methods: provides useful methods like shuffle to shuffle cards in the draw pile
 * - int numDrawCards: Keeps track of the number of cards remaining in the draw pile
 *      * Simple counter to track the number of cards remaining in the draw pile
 * 
 * Incomplete Implementation Documentation - This model is made so that it works on a text based interface. 
 * Ideally, the model should only have the deck logic which is then implemented to be used with the game logic
 * with a GUI through the Controller and Viewer. This will be implemented in the next milestone.
 * 
 * @author Lasya Erukulla
 * @version 1.0 - Milestone 1
 */
public class Deck_Model {

    private ArrayList<Card_Model> drawPile;

    private int numDrawCards;

    /**
     * Constructs a new Uno drawPile by creating and populating it with Uno cards.
     */
    public Deck_Model() {
        drawPile = new ArrayList<Card_Model>();
        makePile();
    }

    /**
     * Constructs a drawPile of Uno cards from the provided ArrayList of cards.
     *
     * @param cards An ArrayList of Uno cards to initialize the drawPile.
     */
    public Deck_Model(ArrayList<Card_Model> cards) {
        this.drawPile = cards;
        numDrawCards = cards.size();
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
     * Adds a full drawPile of UNO cards and shuffles the drawPile.
     */
    public void makePile() {

        for (Card_Model.CardColour Colour : Card_Model.CardColour.values()) {
            if (!Colour.equals(Card_Model.CardColour.WILD)) {
                for (Card_Model.CardValue Type : Card_Model.CardValue.values()) {
                    if (!Type.equals(Card_Model.CardValue.WILD) && !Type.equals(Card_Model.CardValue.WILD_DRAW_TWO)) {
                        drawPile.add(new Card_Model(Type, Colour));
                        drawPile.add(new Card_Model(Type, Colour));
                        numDrawCards += 2;
                    }
                }
            } else {
                for (int i = 4; i > 0; i--) {
                    drawPile.add(new Card_Model(Card_Model.CardValue.WILD, Card_Model.CardColour.WILD));
                    drawPile.add(new Card_Model(Card_Model.CardValue.WILD_DRAW_TWO, Card_Model.CardColour.WILD));
                    numDrawCards += 2;
                }

            }
            for (Card_Model card: drawPile) {
                card.setPathToImageFile();
            }
        }
        Collections.shuffle(drawPile);
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
     * Returns the all the cards in the drawPile as an ArrayList
     *
     * @return an ArrayList of Cards
     */
    public ArrayList<Card_Model> getCards() {
        return drawPile;
    }

    /**
     * Gets a specific card from the drawPile at index i
     *
     * @param i, the index of the card
     * @return the Card from the drawPile
     */
    public Card_Model getCard(int i) {
        return drawPile.get(i);
    }


    /**
     * Draws a card from the drawPile, and reduces the count of cards in the drawPile.
     *
     * @return The drawn UNO card.
     */
    public Card_Model draw() {
        Card_Model card = drawPile.remove(0);
        numDrawCards--;
        return card;
    }
}