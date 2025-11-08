import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for the Card_Model class.
 * Validates the functionality of the Card_Model class methods, cunstructors, getters and toString
 * 
 * @author Lasya Erukulla
 * @version 1.0 - Milestone 1
 */
public class Card_ModelTest {
    private static Card_Model card;
    private static int counter;

    public Card_ModelTest() {
    }

    /**
     * Sets test card and counter to default values before each test
     */
    @BeforeEach
    public void setUp() {
        card = null;
    }

    /**
     * Displays the counter value for each test after they are complete
     */
    @AfterEach
    public void summary() {
        System.out.println("Tests Completed! \n");
    }

    /**
     * Tests the constructor of the Card_Model class 
     * - verifies that the card value is equal to the expected value by the constructor
     * - verifies that the card colour is equal to the expected colour by the constructor
     */
    @Test
    public void test_Constructor() {
        System.out.println("Testing Constructor...");
        card = new Card_Model(Card_Model.CardValue.EIGHT, Card_Model.CardColour.RED);
        assertEquals(Card_Model.CardValue.EIGHT, card.getCardValue());
        assertEquals(Card_Model.CardColour.RED, card.getColour());
    }

    /**
     * Tests the getCardValue method of the Card_Model class 
     * - verifies that the returned card value is equal to the expected card value by the constructor
     */
    @Test
    public void test_getCardValue() {
        System.out.println("Testing Method getCardValue...");
        card = new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.GREEN);
        assertEquals(Card_Model.CardValue.REVERSE, card.getCardValue());
    }

    /**
     * Tests the getColour method of the Card_Model class 
     * - verifies that the returned card colour is equal to the expected card value by the constructor
     */
    @Test
    public void test_getColour() {
        System.out.println("Testing Method getColour...");
        card = new Card_Model(Card_Model.CardValue.WILD_DRAW_TWO, Card_Model.CardColour.BLUE);
        assertEquals(Card_Model.CardColour.BLUE, card.getColour());
    }

    /**
     * Test the toString method of the Card_Model class
     * - verifies that the returned string representation of the card is equal to the expected format
     */
    @Test
    public void test_toString() {
        System.out.println("Testing Method toString");
        card = new Card_Model(Card_Model.CardValue.NINE, Card_Model.CardColour.GREEN);
        assertEquals(" GREEN NINE", card.toString());
    }
}