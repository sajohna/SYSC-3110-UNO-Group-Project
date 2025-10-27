/**
 * Model class representing an UNO card with its value and colour
 * Manages the properties and behaviors of individual UNO cards.
 * 
 * This class includes:
 * - Enums for card values and colours
 * - Creation of UNO cards with specific values and colours
 * - Methods to retrieve card properties
 * - String representation of the card
 * 
 * Data Structure Design:
 * - Enum of CardVlue: Represents the differnet values of a UNO card
 *   ENUM chose for:
 *    * Fixed set of constants: Card values are predfined/determined set to not change
 *    * Allows for types safety: prevents invalid values from being assigned to cards
 *    * Readability: makes code more understandable by using descriptive names for card values
 * - Enum of CardColour: Represents the different colours of a UNO card
 *   ENUM chose for:
 *    * Fixed set of constants: Card colours are predfined/determined set to not change
 *    * Allows for types safety: prevents invalid colours from being assigned to cards
 *    * Readability: makes code more understandable by using descriptive names for card colours
 * - final CardValue Value: Stores the value of the card
 *   final chose for:
 *    * Immutability: ensure that once a card is created, its value cannot be changed
 *    * Consistency: maintains the card's properties throughout its lifecycle
 * - final CardColour Colour: Stores the colour of the card 
 *   final chose for:
 *    * Immutability: ensure that once a card is created, its colour cannot be changed
 *    * Consistency: maintains the card's properties throughout its lifecycle  
 * 
 * Incomplete Implementation Documentation - This model is made so that it works on a text based interface. 
 * Ideally, the model should only have the deck logic which is then implemented to be used with the game logic
 * with a GUI through the Controller and Viewer. This will be implemented in the next milestone.
 * 
 * @author Lasya Erukulla
 * @version 1.0 - Milestone 1
 */

public class Card_Model {
    /**
     * Enum for the different colours of Uno cards
     */
    public enum CardColour {RED, BLUE, GREEN, YELLOW, WILD}

    /**
     * Enum for the different values of Uno cards
     */
    public enum CardValue {
        ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), REVERSE(20), 
        DRAW_ONE(10), SKIP(20), WILD(40), WILD_DRAW_TWO(50);

        int cardScore;
        CardValue(int cardScore) {
            this.cardScore = cardScore;
        }

    }
    
    private final CardValue VALUE;
    private final CardColour COLOUR;

    /**
     * Creates a new Uno card with the specified card value and color.
     *
     * @param cardValue The value of the card (e.g., NUMBER, SKIP, REVERSE).
     * @param cardColour The colour of the card (e.g., RED, BLUE, GREEN, YELLOW, or WILD).
     */
    public Card_Model(CardValue cardValue, CardColour cardColour) {
        this.VALUE = cardValue;
        this.COLOUR = cardColour;
    }

    /**
     * Gets the card value of this Uno card.
     *
     * @return The card value.
     */
    public CardValue getCardValue() {

        return this.VALUE;
    }

    /**
     * Gets the color of the card.
     *
     * @return The card color.
     */
    public CardColour getColour() {

        return this.COLOUR;
    }


    /**
     * Returns a string representation of the card, including its color and value.
     *
     * @return A string representation of the card in the format "COLOR VALUE" (e.g., "RED NUMBER").
     */
    @Override
    public String toString() {
        return " " + COLOUR + " " + VALUE;
    }
}