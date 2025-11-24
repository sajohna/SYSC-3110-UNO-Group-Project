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
 * @version 3.0 - Milestone 3
 */

public class Card_Model {
    /**
     * Enum for the different colours of Uno cards
     */
    public enum CardColour {
        // Light side colors
        RED, BLUE, GREEN, YELLOW, WILD,
        //Dark side colors
        TEAL, PURPLE, ORANGE, PINK
    }

    /**
     * Enum for the different values of Uno cards
     */
    public enum CardValue {
        ONE(1, 1), TWO(2, 2), THREE(3, 3), FOUR(4, 4), FIVE(5, 5), SIX(6, 6), SEVEN(7, 7), EIGHT(8,8), NINE(9,9),
        //Light side special cards
        REVERSE(20, 20), DRAW_ONE(10, 10), SKIP(20, 20), WILD(40, 40), WILD_DRAW_TWO(50, 50),
        //Dark side speical cards
        FLIP(20, 20), DRAW_FIVE(20,20), SKIP_EVERYONE(30, 30), WILD_DRAW_COLOUR(60,60);

        int lightSideScore;
        int darkSideScore;

        CardValue(int lightSideScore, int darkSideScore) {
            this.lightSideScore = lightSideScore;
            this.darkSideScore = darkSideScore;
        }

        public int getCardScore(boolean isDarkSide){ return isDarkSide ? lightSideScore : darkSideScore; }
    }

    public enum CardSide{ LIGHT_SIDE, DARK_SIDE}

    private final CardValue LIGHT_SIDE_VALUE;
    private final CardColour LIGHT_SIDE_COLOUR;
    private final CardValue DARK_SIDE_VALUE;
    private final CardColour DARK_SIDE_COLOUR;
    private CardSide currentCardSide;

    /**
     * Creates a new Uno card with the specified card value and color, LIGHT SIDE ONLY.
     *
     * @param cardValue The value of the card (e.g., NUMBER, SKIP, REVERSE, DRAW_ONE, WILD, WILD_DRAW_TWO).
     * @param cardColour The colour of the card (e.g., RED, BLUE, GREEN, YELLOW, or WILD).
     */
    public Card_Model(CardValue cardValue, CardColour cardColour) {
        this.LIGHT_SIDE_VALUE = cardValue;
        this.LIGHT_SIDE_COLOUR = cardColour;
        this.DARK_SIDE_VALUE = cardValue;
        this.DARK_SIDE_COLOUR = cardColour;
        this.currentCardSide = CardSide.LIGHT_SIDE;
    }

    /**
     * Creates a new Uno card with the specified card value and color for LIGHT SIDE and DARK SIDE
     *
     * @param lightCardValue The value of the card (e.g., NUMBER, SKIP, REVERSE, DRAW_ONE, WILD, WILD_DRAW_TWO).
     * @param lightCardColour The colour of the card (e.g., RED, BLUE, GREEN, YELLOW, or WILD).
     */
    public Card_Model(CardValue lightCardValue, CardColour lightCardColour, CardValue darkCardValue, CardColour darkCardColour) {
        this.LIGHT_SIDE_VALUE = lightCardValue;
        this.LIGHT_SIDE_COLOUR = lightCardColour;
        this.DARK_SIDE_VALUE = darkCardValue;
        this.DARK_SIDE_COLOUR = darkCardColour;
        this.currentCardSide = CardSide.LIGHT_SIDE;
    }


    /**
     * Gets the card value of this Uno card.
     *
     * @return The card value.
     */
    public CardValue getCardValue() {

        return currentCardSide == CardSide.LIGHT_SIDE ? this.LIGHT_SIDE_VALUE : this.DARK_SIDE_VALUE;
    }

    /**
     * Gets the color of the card.
     *
     * @return The card color.
     */
    public CardColour getColour() {

        return currentCardSide == CardSide.LIGHT_SIDE ? this.LIGHT_SIDE_COLOUR : this.DARK_SIDE_COLOUR;
    }

    public CardSide getCurrentCardSide(){
        return this.currentCardSide;
    }

    public int getCardScore(boolean isDarkSide){
        CardValue value = isDarkSide ? this.DARK_SIDE_VALUE : this.LIGHT_SIDE_VALUE;
        return value.getCardScore(isDarkSide);
    }

    public void flipCardSide(){
        currentCardSide = (currentCardSide == CardSide.LIGHT_SIDE) ? CardSide.DARK_SIDE : CardSide.LIGHT_SIDE;
    }

    public void setCurrentCardSide(CardSide side){
        this.currentCardSide = side;
    }

    public boolean isFlipCard() {
        return getCardValue() == CardValue.FLIP;
    }

    public boolean isWildCard() {
        return getCardValue() == CardValue.WILD || getCardValue() == CardValue.WILD_DRAW_COLOUR || getCardValue() == CardValue.WILD_DRAW_TWO;
    }

    public static boolean isLightSideColour(CardColour colour) {
        return colour == CardColour.RED || colour == CardColour.BLUE || colour == CardColour.GREEN || colour == CardColour.YELLOW || colour == CardColour.WILD;
    }

    public static boolean isDarkSideColour(CardColour colour) {
        return colour == CardColour.TEAL || colour == CardColour.PURPLE || colour == CardColour.PINK || colour == CardColour.ORANGE || colour == CardColour.WILD;
    }

    /**
     * Returns a string representation of the card, including its color and value.
     *
     * @return A string representation of the card in the format "COLOR VALUE" (e.g., "RED NUMBER").
     */
    @Override
    public String toString() {
        return  getColour() + "_" + getCardValue();
    }
}