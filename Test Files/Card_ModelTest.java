import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the Card_Model class.
 * Validates the functionality of the Card_Model class methods, cunstructors, getters and toString
 * 
 * @author Lasya Erukulla
 * @version 3.0 - Milestone 3
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
        assertEquals("GREEN_NINE", card.toString());
    }

    /** Uno Flip tests */
    /**
     * Tests the flip constructor of the Card_Model class
     * - verifies that the card value is equal to the expected value by the constructor
     * - verifies that the card colour is equal to the expected colour by the constructor
     * - verfifies that the default side is LIGHT
     */
    @Test
    public void test_flipConstructor() {
        System.out.println("Testing Flip Constructor...");
        card = new Card_Model(Card_Model.CardValue.EIGHT, Card_Model.CardColour.RED,Card_Model.CardValue.EIGHT, Card_Model.CardColour.TEAL);
        assertEquals(Card_Model.CardValue.EIGHT, card.getCardValue());
        assertEquals(Card_Model.CardColour.RED, card.getColour());
        assertEquals(Card_Model.CardSide.LIGHT_SIDE, card.getCurrentCardSide());
    }

    /**
     * Tests the getCurrentCardSide method of the Card_Model class
     * - verifies that the returned current card side is equal to the expected default side LIGHT
     */
    @Test
    public void test_getCurrentCardSide() {
        System.out.println("Testing Get Current Card Side...");
        card = new Card_Model(Card_Model.CardValue.EIGHT, Card_Model.CardColour.RED,Card_Model.CardValue.EIGHT, Card_Model.CardColour.TEAL);
        assertEquals(Card_Model.CardSide.LIGHT_SIDE, card.getCurrentCardSide());
    }

    /**
     * Tests the setCurrentCardSide method of the Card_Model class
     * - verifies that after setting the current card side to DARK_SIDE
     * - verfifeis the card value is equal to the expected dark side value
     * - verifies the card colout is equal to the expected dark side colour
     * - verifies the current card side is equal to DARK_SIDE
     */
    @Test
    public void test_setCurrentCardSide() {
        System.out.println("Testing Set Current Card Side...");
        card = new Card_Model(Card_Model.CardValue.EIGHT, Card_Model.CardColour.RED,Card_Model.CardValue.EIGHT, Card_Model.CardColour.TEAL);
        card.setCurrentCardSide(Card_Model.CardSide.DARK_SIDE);
        assertEquals(Card_Model.CardValue.EIGHT, card.getCardValue());
        assertEquals(Card_Model.CardColour.TEAL, card.getColour());
        assertEquals(Card_Model.CardSide.DARK_SIDE, card.getCurrentCardSide());
    }

    /**
     * Tests the getCardScore method of the Card_Model class
     * - verifies that the returned light side score is equal to the expected value
     */
    @Test
    public void test_lightSideScore() {
        System.out.println("Testing Light Side Score...");
        card = new Card_Model(Card_Model.CardValue.FIVE, Card_Model.CardColour.RED,Card_Model.CardValue.NINE, Card_Model.CardColour.TEAL);
        assertEquals(5, card.getCardScore(false));
    }

    /**
     * Tests the getCardScore method of the Card_Model class
     * - verifies that the returned dark side score is equal to the expected value
     */
    @Test
    public void test_darkSideScore() {
        System.out.println("Testing Dark Side Score...");
        card = new Card_Model(Card_Model.CardValue.FIVE, Card_Model.CardColour.RED,Card_Model.CardValue.DRAW_FIVE, Card_Model.CardColour.TEAL);
        assertEquals(20, card.getCardScore(true));
    }

    /**
     * Tests the flipCardSide method of the Card_Model class
     * - verifies that after flipping the card side, the current card side is equla to DARK_SIDE
     */
    @Test
    public void test_flipCardSide() {
        System.out.println("Testing Flip Card Side...");
        card = new Card_Model(Card_Model.CardValue.FIVE, Card_Model.CardColour.RED,Card_Model.CardValue.DRAW_FIVE, Card_Model.CardColour.TEAL);
        card.flipCardSide();
        assertEquals(Card_Model.CardSide.DARK_SIDE, card.getCurrentCardSide());
    }

    /**
     * Tests the isFlipCard method of the Card_Model class
     * - verifies that a non-flip card return false
     * - verfies that a flip card returns true
     */
    @Test
    public void test_isFlipCard() {
        System.out.println("Testing Flip Card Side...");
        card = new Card_Model(Card_Model.CardValue.FIVE, Card_Model.CardColour.GREEN);
        assertFalse(card.isFlipCard());
        card = new Card_Model(Card_Model.CardValue.FLIP, Card_Model.CardColour.RED);
        assertTrue(card.isFlipCard());
    }

    /**
     * Tests the isWildCard method of the Card_Model class
     * - verifies that a WILD card return true
     * - verifies that a WILD_DRAW_TWO card returns true
     * - verifies that a WILD_DRAW_COLOUR card returns true
     */
    @Test
    public void test_isWildCard() {
        System.out.println("Testing Flip Card Side...");
        card = new Card_Model(Card_Model.CardValue.WILD, Card_Model.CardColour.GREEN);
        assertTrue(card.isWildCard());
        card = new Card_Model(Card_Model.CardValue.WILD_DRAW_TWO, Card_Model.CardColour.GREEN);
        assertTrue(card.isWildCard());
        card = new Card_Model(Card_Model.CardValue.WILD_DRAW_COLOUR, Card_Model.CardColour.GREEN);
        assertTrue(card.isWildCard());
        card = new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.GREEN);
        assertFalse(card.isWildCard());
    }

    /**
     * Tests the isDarkSideColour static method of the Card_Model class
     * - verfies that teal, orange, pink, purple and wild colours return true
     */
    @Test
    public void test_isDarkSideCard() {
        System.out.println("Testing Dark Side Card Side...");
        assertTrue(Card_Model.isDarkSideColour(Card_Model.CardColour.TEAL));
        assertTrue(Card_Model.isDarkSideColour(Card_Model.CardColour.ORANGE));
        assertTrue(Card_Model.isDarkSideColour(Card_Model.CardColour.PINK));
        assertTrue(Card_Model.isDarkSideColour(Card_Model.CardColour.PURPLE));
        assertTrue(Card_Model.isDarkSideColour(Card_Model.CardColour.WILD));
    }


     /**
     * Tests the isLightSideColour static method of the Card_Model class
     * - verfies that red, blue, green, yellow and wild colours return true
     */
    @Test
    public void test_isLightSideCard() {
        System.out.println("Testing Light Side Card Side...");
        assertTrue(Card_Model.isLightSideColour(Card_Model.CardColour.RED));
        assertTrue(Card_Model.isLightSideColour(Card_Model.CardColour.BLUE));
        assertTrue(Card_Model.isLightSideColour(Card_Model.CardColour.GREEN));
        assertTrue(Card_Model.isLightSideColour(Card_Model.CardColour.YELLOW));
        assertTrue(Card_Model.isLightSideColour(Card_Model.CardColour.WILD));
    }
}