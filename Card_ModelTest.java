/**
 * Card Class Testing
 */

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        counter = 0;
    }

    /**
     * Displays the counter value for each test after they are complete
     */
    @AfterEach
    public void summary() {
        System.out.println("Number of Tests Completed: " + counter + "\n");
    }

    @Test
    public void test_Constructor() {
        System.out.println("Testing Constructor...");
        card = new Card_Model(Card_Model.CardValue.SIX, Card_Model.CardColour.BLUE);
        assertEquals(Card_Model.CardValue.SIX, card.getCardValue());
        assertEquals(Card_Model.CardColour.BLUE, card.getColour());
        counter = 2;
    }

    @Test
    public void test_getCardValue() {
        System.out.println("Testing Method getCardValue...");
        card = new Card_Model(Card_Model.CardValue.DRAW_ONE, Card_Model.CardColour.RED);
        assertEquals(Card_Model.CardValue.DRAW_ONE, card.getCardValue());
        counter = 1;
    }

    @Test
    public void test_getColour() {
        System.out.println("Testing Method getColour...");
        card = new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.YELLOW);
        assertEquals(Card_Model.CardColour.YELLOW, card.getColour());
        counter = 1;
    }

    @Test
    public void test_toString() {
        System.out.println("Testing Method toString");
        card = new Card_Model(Card_Model.CardValue.SEVEN, Card_Model.CardColour.RED);
        assertEquals(" RED SEVEN", card.toString());
        counter = 1;
    }
}