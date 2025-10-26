public class Card_Model {
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
    
    /**
     * Enum for the different colours of Uno cards
     */
    public enum CardColour {RED, BLUE, GREEN, YELLOW, WILD}

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
     * Sets the color of the card.
     *
     * @param colour the card color.
     */
    public void setColour(CardColour colour){
        this.COLOUR = colour;
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