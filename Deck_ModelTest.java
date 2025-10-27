/**
 * Deck Class Testing
 */

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class Deck_ModelTest {

    private static Deck_Model deck;
    private static int counter;

    public Deck_ModelTest() {
    }

    /**
     * Sets test deck and counter to default values before each test
     */
    @BeforeEach
    public void setUp() {
        deck = null;
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
    public void test_DefaultConstructor() {
        System.out.println("Testing Default Constructor...");
        deck = new Deck_Model();
        assertEquals(104, deck.getNumDrawCards());
        // Default constructor uses makeCards method for adding to deck, tested on its own
        counter = 1;
    }

    @Test
    public void test_OverloadedConstructor() {
        System.out.println("Testing Overloaded Constructor...");
        ArrayList<Card_Model> test_deck = new ArrayList<>();
        test_deck.add(new Card_Model(Card_Model.CardValue.FOUR, Card_Model.CardColour.GREEN));
        test_deck.add(new Card_Model(Card_Model.CardValue.FIVE, Card_Model.CardColour.YELLOW));
        test_deck.add(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        test_deck.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.BLUE));
        deck = new Deck_Model(test_deck);
        assertEquals(test_deck.size(), deck.getNumDrawCards());
        counter += 1;
        for (int i = 0; i < test_deck.size(); i++) {
            assertEquals(test_deck.get(i), deck.getCard(i));
            counter++;
        }
    }

    @Test
    public void test_getNumDrawCards() {
        System.out.println("Testing Method getNumDrawCards");
        ArrayList<Card_Model> test_deck = new ArrayList<>();
        test_deck.add(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        test_deck.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.GREEN));
        deck = new Deck_Model(test_deck);
        assertEquals(2, deck.getNumDrawCards());
        deck.draw();
        assertEquals(1, deck.getNumDrawCards());
        counter = 2;
    }

    @Test
    public void test_getCard() {
        System.out.println("Testing Method getCard");
        ArrayList<Card_Model> test_deck = new ArrayList<>();
        test_deck.add(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        test_deck.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.GREEN));
        deck = new Deck_Model(test_deck);
        assertEquals(Card_Model.CardValue.THREE, deck.getCard(0).getCardValue());
        assertEquals(Card_Model.CardColour.RED, deck.getCard(0).getColour());
        counter = 2;
    }

    @Test
    public void test_getCards() {
        System.out.println("Testing Method getCards");
        ArrayList<Card_Model> test_deck = new ArrayList<>();
        test_deck.add(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        test_deck.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.GREEN));
        deck = new Deck_Model(test_deck);
        assertEquals(test_deck, deck.getCards());
    }

    @Test
    public void test_draw() {
        System.out.println("Testing Method draw...");
        ArrayList<Card_Model> test_deck = new ArrayList<>();
        test_deck.add(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        test_deck.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.GREEN));
        deck = new Deck_Model(test_deck);
        Card_Model drawn = deck.draw();
        assertNotNull(drawn);
        assertFalse(deck.getCards().contains(drawn));
        assertEquals(1, deck.getNumDrawCards());
        counter = 3;
    }

    @Test
    public void test_isEmpty() {
        System.out.println("Testing Method isEmpty...");
        ArrayList<Card_Model> test_deck = new ArrayList<>();
        deck = new Deck_Model(test_deck);
        assertTrue(deck.isEmpty());
        test_deck = new ArrayList<>();
        test_deck.add(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        deck = new Deck_Model(test_deck);
        deck.draw();
        assertTrue(deck.isEmpty());
        counter = 2;
    }

    @Test
    public void test_makeCards() {
        System.out.println("Testing Method makeCards...");
        deck = new Deck_Model();
        assertEquals(104, deck.getNumDrawCards());
        counter = 1;
    }
}