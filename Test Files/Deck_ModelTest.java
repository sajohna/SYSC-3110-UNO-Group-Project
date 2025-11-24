import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.smartcardio.Card;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the Deck_Model class.
 * Validates the functionality of the Deck_Model class methods, cunstructors, creation of the draw pile, and card drawing
 *
 * @author Lasya Erukulla
 * @version 1.0 - Milestone 1
 */

public class Deck_ModelTest {

    private static Deck_Model pile;
    private static int counter;

    public Deck_ModelTest() {
    }

    /**
     * Sets test pile and counter to default values before each test
     */
    @BeforeEach
    public void setUp() {
        pile = null;
    }

    /**
     * Displays the counter value for each test after they are complete
     */
    @AfterEach
    public void summary() {
        System.out.println("Tests Completed!\n");
    }

    /**
     * Tests the default constructor of the Deck_Model class
     * - verfies that the number of cards in the draw pile are as expected
     */
    @Test
    public void test_DefaultConstructor() {
        System.out.println("Testing Default Constructor...");
        pile = new Deck_Model();
        assertTrue(pile.getNumDrawCards() > 50);
    }

    /**
     * Tests the overloaded constructor of the Deck_Model class
     * - verfies that the number of cards in the draw pile equal the size of the test pile
     * - verifies that each card in the draw pile matches the respective card in the test pile
     */

    @Test
    public void test_OverloadedConstructor() {
        System.out.println("Testing Overloaded Constructor...");
        ArrayList<Card_Model> test_pile = new ArrayList<>();
        test_pile.add(new Card_Model(Card_Model.CardValue.EIGHT, Card_Model.CardColour.GREEN));
        test_pile.add(new Card_Model(Card_Model.CardValue.WILD, Card_Model.CardColour.RED));
        test_pile.add(new Card_Model(Card_Model.CardValue.NINE, Card_Model.CardColour.WILD));
        test_pile.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.RED));
        pile = new Deck_Model(test_pile);
        assertEquals(test_pile.size(), pile.getNumDrawCards());
        for (int i = 0; i < test_pile.size(); i++) {
            assertEquals(test_pile.get(i), pile.getCard(i));
        }
    }

    /**
     * Tests the makePile method of the Deck_Model class
     * - verifies that the number of cards in the draw pile is equal to the size of the test pile
     * - verifies that the draw pile decrements correctly when a card is drawn
     */
    @Test
    public void test_getNumDrawCards() {
        System.out.println("Testing Method getNumDrawCards");
        ArrayList<Card_Model> test_pile = new ArrayList<>();
        test_pile.add(new Card_Model(Card_Model.CardValue.SIX, Card_Model.CardColour.YELLOW));
        test_pile.add(new Card_Model(Card_Model.CardValue.WILD_DRAW_TWO, Card_Model.CardColour.GREEN));
        test_pile.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.RED));
        pile = new Deck_Model(test_pile);
        assertEquals(3, pile.getNumDrawCards());
        pile.draw();
        assertEquals(2, pile.getNumDrawCards());
    }

    /**
     * Tests the getCard method of the Deck_Model class
     * - verifies that the returned card at index i matches the expected colour and value
     */
    @Test
    public void test_getCard() {
        System.out.println("Testing Method getCard");
        ArrayList<Card_Model> test_pile = new ArrayList<>();
        test_pile.add(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.BLUE));
        test_pile.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.GREEN));
        test_pile.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.RED));
        pile = new Deck_Model(test_pile);
        assertEquals(Card_Model.CardValue.THREE, pile.getCard(0).getCardValue());
        assertEquals(Card_Model.CardColour.BLUE, pile.getCard(0).getColour());
    }

    /**
     * Tests the getCards method of the Deck_Model class
     * - verifies that the returned ArrayList of cards matches the expected test pile
     */
    @Test
    public void test_getCards() {
        System.out.println("Testing Method getCards");
        ArrayList<Card_Model> test_pile = new ArrayList<>();
        test_pile.add(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.GREEN));
        test_pile.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.BLUE));
        test_pile.add(new Card_Model(Card_Model.CardValue.WILD, Card_Model.CardColour.YELLOW));
        pile = new Deck_Model(test_pile);
        assertEquals(test_pile, pile.getCards());
    }

    /**
     * Tests the draw method of the Deck_Model clas
     * - verifies that a card is drawn from the draw pile
     * - verifies that the drawn card is not present in the draw pile
     * - verifies that the remaining number of cards in the draw pile is decremented by one
     */
    @Test
    public void test_draw() {
        System.out.println("Testing Method draw...");
        ArrayList<Card_Model> test_pile = new ArrayList<>();
        test_pile.add(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        test_pile.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.GREEN));
        test_pile.add(new Card_Model(Card_Model.CardValue.WILD, Card_Model.CardColour.BLUE));
        pile = new Deck_Model(test_pile);
        Card_Model drawn_card = pile.draw();
        assertNotNull(drawn_card);
        assertFalse(pile.getCards().contains(drawn_card));
        assertEquals(2, pile.getNumDrawCards());
    }

    /**
     * Tests the isEmpty method of the Deck_Model class
     * - verifies that the draw pile is empty when created
     * - verifies that the draw pile is empty after all cards have been drawn
     */
    @Test
    public void test_isEmpty() {
        System.out.println("Testing Method isEmpty...");
        ArrayList<Card_Model> test_pile = new ArrayList<>();
        pile = new Deck_Model(test_pile);
        assertTrue(pile.isEmpty());
        test_pile.add(new Card_Model(Card_Model.CardValue.NINE, Card_Model.CardColour.RED));
        pile = new Deck_Model(test_pile);
        pile.draw();
        assertTrue(pile.isEmpty());
    }

    /**
     * Tests the makePile method of the Deck_Model class
     * - verifies that the number of cards in the draw pile is equal to 104 after the pile is created
     */
    @Test
    public void test_makePile() {
        System.out.println("Testing Method makePile...");
        pile = new Deck_Model();
        assertTrue(pile.getNumDrawCards() > 50);
    }

    // UNO FLIP tests
    /**
     * Tests the getDiscardPile mehtod of the Deck_Model class
     * - verifies that the discard pile is empty when the deck is created
     */
    @Test
    public void test_getDiscardPile() {
        System.out.println("Testing Method getDiscardPile...");
        pile = new Deck_Model();
        assertEquals(0, pile.getDiscardPile().size());

    }

    /**
     * Tests the getIsDarkSide method of the Deck_Model class
     * - verifies that the deck is initialized to the light side when created
     */
    @Test
    public void test_getIsDarkSide() {
        System.out.println("Testing Method getIsDarkSide...");
        pile = new Deck_Model();
        assertFalse(pile.getIsDarkSide());
    }

    /**
     * Tests the flipDeck method of the Deck_Model class
     * - verifies that the deck is flippped to the dark side
     * - verifies that cards in the pile are changed to the dark side too
     */

    @Test
    public void test_flipDeck() {
        System.out.println("Testing Method flipDeck...");
        pile = new Deck_Model();
        assertFalse(pile.getIsDarkSide());
        Card_Model.CardSide initSide = pile.getCard(0).getCurrentCardSide();
        pile.flipDeck();
        assertTrue(pile.getIsDarkSide());
        assertNotEquals(initSide, pile.getCard(0).getCurrentCardSide()); // test method changes cards in pile to darkside
    }

    /**
     * Tests the addToDiscardPile method of the Deck_Model class
     * - verifies that a card is added to the discard pile
     */
    @Test
    public void test_addToDiscardPile() {
        System.out.println("Testing Method addToDiscardPile...");
        pile = new Deck_Model();
        Card_Model card = new Card_Model(Card_Model.CardValue.NINE, Card_Model.CardColour.RED);
        pile.addToDiscardPile(card);
        assertTrue(pile.getDiscardPile().contains(card));
    }

    /**
     * Tests the drawUntilColour method of the Deck_Model class
     * - verifies that cards are drawn until a card of the specified colour is drawn
     * - verifies that drawn card matches the expected colour and value
     */
    @Test
    public void test_drawUntilColour() {
        System.out.println("Testing Method drawUntilColour...");
        ArrayList<Card_Model> test_pile = new ArrayList<>();
        test_pile.add(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        test_pile.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.GREEN));
        test_pile.add(new Card_Model(Card_Model.CardValue.WILD, Card_Model.CardColour.BLUE));
        test_pile.add(new Card_Model(Card_Model.CardValue.FLIP, Card_Model.CardColour.TEAL));
        pile = new Deck_Model(test_pile);

        Card_Model drawnCard =  pile.drawUntilColour(Card_Model.CardColour.GREEN);
        assertEquals(drawnCard.getColour(), Card_Model.CardColour.GREEN);
        assertEquals(drawnCard.getCardValue(), Card_Model.CardValue.REVERSE);
    }

    /**
     * Tests the drawCardsUntilColour method of the Deck_Model class
     * - verifies that cards are drawn until a card of the specified colour is drawn
     * - verifies that all drawn cards are returned
     */
    @Test
    public void test_drawCardsUntilColour(){
        System.out.println("Testing Method drawCardsUntilColour...");
        ArrayList<Card_Model> test_pile = new ArrayList<>();
        test_pile.add(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        test_pile.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.GREEN));
        test_pile.add(new Card_Model(Card_Model.CardValue.WILD, Card_Model.CardColour.BLUE));
        test_pile.add(new Card_Model(Card_Model.CardValue.FLIP, Card_Model.CardColour.TEAL));
        pile = new Deck_Model(test_pile);

        ArrayList<Card_Model> drawnCards =  pile.drawCardsUntilColour(Card_Model.CardColour.GREEN);
        assertEquals(2, drawnCards.size());
        assertEquals(Card_Model.CardColour.GREEN, drawnCards.get(1).getColour());
    }

    /**
     * Test the reshuffleFromDiscard method of the Deck_Model class
     * - verifies that all cards except the latest are moved from the discard pile to the draw pile
     */
    @Test
    public void test_reshuffleFromDiscard() {
        System.out.println("Testing Method reshuffleFromDiscard...");
        pile = new Deck_Model();
        Card_Model card1 = new Card_Model(Card_Model.CardValue.NINE, Card_Model.CardColour.ORANGE);
        pile.addToDiscardPile(card1);
        Card_Model card2 = new Card_Model(Card_Model.CardValue.FIVE, Card_Model.CardColour.RED);
        pile.addToDiscardPile(card2);
        Card_Model card3 = new Card_Model(Card_Model.CardValue.DRAW_FIVE, Card_Model.CardColour.TEAL);
        pile.addToDiscardPile(card3);
        pile.reshuffleFromDiscard();
        assertTrue(pile.getDiscardPile().contains(card3));
        assertFalse(pile.getDiscardPile().contains(card2));
        assertFalse(pile.getDiscardPile().contains(card1));
        // verify cards except the latest were added to the drawPile
        assertTrue(pile.getCards().contains(card2));
        assertTrue(pile.getCards().contains(card1));
    }

}