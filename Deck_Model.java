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
 * @version 3.0 - Milestone 3
 */
public class Deck_Model {

    private ArrayList<Card_Model> drawPile;
    private ArrayList<Card_Model> discardPile;
    private boolean isDarkSide;
    private int numDrawCards;

    /**
     * Constructs a new Uno drawPile by creating and populating it with Uno cards.
     */
    public Deck_Model() {
        drawPile = new ArrayList<Card_Model>();
        discardPile = new ArrayList<Card_Model>();
        isDarkSide = false;
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
        discardPile = new ArrayList<Card_Model>();
        isDarkSide = false;
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
        Card_Model.CardColour[] lightSideColours = {
            Card_Model.CardColour.RED,
            Card_Model.CardColour.BLUE,
            Card_Model.CardColour.GREEN,
            Card_Model.CardColour.YELLOW
        };
        
        Card_Model.CardColour[] darkSideColours = {
            Card_Model.CardColour.TEAL,
            Card_Model.CardColour.PURPLE,
            Card_Model.CardColour.ORANGE,
            Card_Model.CardColour.PINK
        };

        Card_Model.CardValue[] cardNumberValues = {
            Card_Model.CardValue.ONE,
            Card_Model.CardValue.TWO,
            Card_Model.CardValue.THREE,
            Card_Model.CardValue.FOUR,
            Card_Model.CardValue.FIVE,
            Card_Model.CardValue.SIX,
            Card_Model.CardValue.SEVEN,
            Card_Model.CardValue.EIGHT,
            Card_Model.CardValue.NINE
        };

        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 2; j++){
                // Number cards two of each number per colour
                for (Card_Model.CardValue value : cardNumberValues){
                    drawPile.add(new Card_Model(value, lightSideColours[i], value, darkSideColours[i]));
                    numDrawCards++;
                }
                // Special cards two of each per colour
                drawPile.add(new Card_Model(Card_Model.CardValue.REVERSE, lightSideColours[i], Card_Model.CardValue.REVERSE, darkSideColours[i]));
                drawPile.add(new Card_Model(Card_Model.CardValue.DRAW_ONE, lightSideColours[i], Card_Model.CardValue.DRAW_FIVE, darkSideColours[i]));
                drawPile.add(new Card_Model(Card_Model.CardValue.SKIP, lightSideColours[i], Card_Model.CardValue.SKIP_EVERYONE, darkSideColours[i]));
                drawPile.add(new Card_Model(Card_Model.CardValue.FLIP, lightSideColours[i], Card_Model.CardValue.FLIP, darkSideColours[i]));
                numDrawCards +=4;
            }
        }

        // Wild cards 
        for( int i =0; i< 4; i++){
            drawPile.add(new Card_Model(Card_Model.CardValue.WILD, Card_Model.CardColour.WILD, Card_Model.CardValue.WILD, Card_Model.CardColour.WILD));
            drawPile.add(new Card_Model(Card_Model.CardValue.WILD_DRAW_TWO, Card_Model.CardColour.WILD, Card_Model.CardValue.WILD_DRAW_COLOUR, Card_Model.CardColour.WILD));
            numDrawCards +=2;
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

    public ArrayList<Card_Model> getDiscardPile() {
        return discardPile;
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

    public boolean getIsDarkSide() {
        return isDarkSide;
    }


    public void flipDeck(){
        isDarkSide = !isDarkSide;
        for (Card_Model card : drawPile){
            card.flipCardSide();
        }
        for (Card_Model card : discardPile){
            card.flipCardSide();
        }
    }

    /**
     * Draws a card from the drawPile, and reduces the count of cards in the drawPile.
     *
     * @return The drawn UNO card.
     */
    public Card_Model draw() {
        if(isEmpty()) reshuffleFromDiscard();
        if(isEmpty()) return null;
        Card_Model card = drawPile.remove(0);
        numDrawCards--;
        return card;
    }

    public Card_Model drawUntilColour(Card_Model.CardColour colour) {
        Card_Model card = null;
        while (!isEmpty()) {
            card = draw();
            if (card == null) break;
            numDrawCards--;
            if (card.getColour() == colour || card.getColour() == Card_Model.CardColour.WILD) {
                break;
            }
        }
        return card;
    }

    public ArrayList<Card_Model> drawCardsUntilColour(Card_Model.CardColour colour) {
        ArrayList<Card_Model> drawnCards = new ArrayList<>();

        while (!isEmpty()) {
            Card_Model card = draw();
            if (card == null) break;
            drawnCards.add(card);
            numDrawCards--;
            if (card.getColour() == colour || card.getColour() == Card_Model.CardColour.WILD) {
                break;
            }
        }
        return drawnCards;
    }

    public void addToDiscardPile(Card_Model card) {
        discardPile.add(card);
    }


    public void reshuffleFromDiscard() {
        if (discardPile.size() < 1) return;
        Card_Model topCard = discardPile.remove(discardPile.size() - 1);
        drawPile.addAll(discardPile);
        discardPile.clear();
        discardPile.add(topCard);
        numDrawCards = drawPile.size();
        Collections.shuffle(drawPile);
    }
}