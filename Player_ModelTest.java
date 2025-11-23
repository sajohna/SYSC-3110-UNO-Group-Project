import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
public class Player_ModelTest
{
    private static Player_Model player;
    private Player_Model aiPlayer;
    private static int count;

    /**
     * Test suite for the Player_Model class.
     * Validates the functionality of the Player_Model class methods, cunstructors, getters, setters,
     * and card functionality
     *
     * @author Lucas Baker
     * @version 3.0 - Milestone 3
     */
    public Player_ModelTest() {}

    /**
     * Sets test player and counter to default values before each test
     */
    @BeforeEach
    public void setUp()
    {
        player = null;
    }

    /**
     * Displays the counter value for each test after they are complete
     */
    @AfterEach
    public void summary() 
    {
        System.out.println("Number of Tests Completed: " + count + "\n");
    }

    /**
     * Tests the default constructor of the Player_Model Class
     * Asserts all variables are defaulted correctly
     */
    @Test
    public void test_DefaultConstructor() 
    {
        System.out.println("Testing the default constructor");
        ArrayList<Card_Model> temp_list = new ArrayList<>();
        player = new Player_Model();
        assertEquals(0, player.getNumCards());
        assertEquals(temp_list, player.getHand());
        count = 2;
    }

    /**
     * Tests the getHand method of the Player_Model Class
     * Asserts the getter returns the proper value and colour of the cards in the player's hand
     */
    @Test
    public void test_getHand() 
    {
        System.out.println("Testing the getHand() method");
        ArrayList<Card_Model> temp_list = new ArrayList<>();
        temp_list.add(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        temp_list.add(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.GREEN));
        temp_list.add(new Card_Model(Card_Model.CardValue.SKIP, Card_Model.CardColour.BLUE));

        player = new Player_Model();
        player.addCard(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        player.addCard(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.GREEN));
        player.addCard(new Card_Model(Card_Model.CardValue.SKIP, Card_Model.CardColour.BLUE));
        for (int i = 0; i < temp_list.size(); i++) {
            assertEquals(temp_list.get(i).getCardValue(), player.getHand().get(i).getCardValue());
            assertEquals(temp_list.get(i).getColour(), player.getHand().get(i).getColour());
            count += 2;
        }
    }

    /**
     * Tests the getNumCards method of the Player_Model Class
     * Asserts the getter returns the proper number of cards in the player's hand
     */
    @Test
    public void test_getNumCards() 
    {
        System.out.println("Testing the getNumCards() method");
        player = new Player_Model();
        player.addCard(new Card_Model(Card_Model.CardValue.EIGHT, Card_Model.CardColour.YELLOW));
        player.addCard(new Card_Model(Card_Model.CardValue.ONE, Card_Model.CardColour.RED));
        assertEquals(2, player.getNumCards());
        player.addCard(new Card_Model(Card_Model.CardValue.WILD, Card_Model.CardColour.WILD));
        assertEquals(3, player.getNumCards());
        count = 2;
    }

    /**
     * Tests the getScore method of the Player_Model Class
     * Asserts the getter returns the proper score of the player
     */
    @Test
    public void test_getScore() 
    {
        System.out.println("Testing the getScore() method");
        player = new Player_Model();
        player.setScore(10);
        assertEquals(10, player.getScore());
        count = 1;
    }

    /**
     * Tests the getName method of the Player_Model Class
     * Asserts the getter returns the proper name of the player
     */
    @Test
    public void test_getName() 
    {
        System.out.println("Testing the getName() method");
        player = new Player_Model();
        player.setName("Lucas");
        assertEquals("Lucas", player.getName());
        count = 1;
    }

    /**
     * Tests the setScore method of the Player_Model Class
     * Asserts the method sets the score of the player
     */
    @Test
    public void test_setScore() 
    {
        System.out.println("Testing the setScore() method");
        player = new Player_Model();
        player.setScore(20);
        assertEquals(20, player.getScore());
        player.setScore(0);
        assertEquals(0, player.getScore());
        count = 2;
    }

    /**
     * Tests the setName method of the Player_Model Class
     * Asserts the method sets the name of the player
     */
    @Test
    public void test_setName() 
    {
        System.out.println("Testing the setName() method");
        player = new Player_Model();
        player.setName("Bobby");
        assertEquals("Bobby", player.getName());
        count = 1;
    }

    /**
     * Tests the addCard method of the Player_Model Class
     * Asserts the method adds the proper card to the player's hand
     */
    @Test
    public void test_addCard() 
    {
        System.out.println("Testing the addCard() method");
        player = new Player_Model();
        player.addCard(new Card_Model(Card_Model.CardValue.FOUR, Card_Model.CardColour.YELLOW));
        assertEquals(1, player.getNumCards());
        assertEquals(Card_Model.CardValue.FOUR, player.getHand().get(0).getCardValue());
        assertEquals(Card_Model.CardColour.YELLOW, player.getHand().get(0).getColour());
        count = 3;
    }

    /**
     * Tests the drawCard method of the Player_Model Class
     * Asserts the method adds a card to the player's hand and removes one from the deck
     */
    @Test
    public void test_drawCard() 
    {
        System.out.println("Testing the drawCard() method");
        Deck_Model deck = new Deck_Model();
        player = new Player_Model();
        player.drawCard(deck);
        assertEquals(1, player.getNumCards());
        assertEquals(103, deck.getNumDrawCards());
        player.drawCard(deck);
        assertEquals(2, player.getNumCards());
        assertEquals(102, deck.getNumDrawCards());
        count = 4;
    }

    /**
     * Tests the removeCard method of the Player_Model Class
     * Asserts the method removes the proper card from the player's hand
     */
    @Test
    public void test_removeCard() 
    {
        System.out.println("Testing the removeCard() method");
        player = new Player_Model();
        player.addCard(new Card_Model(Card_Model.CardValue.SEVEN, Card_Model.CardColour.RED));
        player.addCard(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.BLUE));
        player.addCard(new Card_Model(Card_Model.CardValue.SKIP, Card_Model.CardColour.YELLOW));
        player.addCard(new Card_Model(Card_Model.CardValue.TWO, Card_Model.CardColour.RED));
        player.removeCard(1);
        assertEquals(3, player.getNumCards());
        assertEquals(Card_Model.CardValue.SEVEN, player.getHand().get(0).getCardValue());
        assertEquals(Card_Model.CardColour.RED, player.getHand().get(0).getColour());
        assertEquals(Card_Model.CardValue.SKIP, player.getHand().get(1).getCardValue());
        assertEquals(Card_Model.CardColour.YELLOW, player.getHand().get(1).getColour());
        assertEquals(Card_Model.CardValue.TWO, player.getHand().get(2).getCardValue());
        assertEquals(Card_Model.CardColour.RED, player.getHand().get(2).getColour());
        count = 7;
    }

    /**
     * Tests the playCard method of the Player_Model Class
     * Asserts the method plays the proper card from the player's hand
     */
    @Test
    public void test_playCard() 
    {
        System.out.println("Testing the playCard() method");
        player = new Player_Model();
        player.addCard(new Card_Model(Card_Model.CardValue.FOUR, Card_Model.CardColour.BLUE));
        player.addCard(new Card_Model(Card_Model.CardValue.REVERSE, Card_Model.CardColour.BLUE));
        Card_Model played = player.playCard(1);
        assertEquals(Card_Model.CardValue.REVERSE, played.getCardValue());
        assertEquals(Card_Model.CardColour.BLUE, played.getColour());
        count = 2;
    }

    // AI PLAYER TESTS
    @Test
    public void test_AIConstructor() {
        System.out.println("Testing AI Constructor...");
        aiPlayer = new Player_Model("TestAI", true);
        assertTrue(aiPlayer.isAI());
        assertEquals("TestAI", aiPlayer.getName());
        count = 2;
    }

    @Test
    public void test_HumanNotAI() {
        System.out.println("Testing Human Not AI...");
        player = new Player_Model("Human", false);
        assertFalse(player.isAI());
        count = 1;
    }

    @Test
    public void test_SetAI() {
        System.out.println("Testing Set AI...");
        player = new Player_Model("Test");
        assertFalse(player.isAI());
        player.setAI(true);
        assertTrue(player.isAI());
        count = 2;
    }

    @Test
    public void test_AIStrategy() {
        System.out.println("Testing AI Strategy...");
        aiPlayer = new Player_Model("AI", true, Player_Model.AIStrategy.HIGHEST_SCORE);
        assertEquals(Player_Model.AIStrategy.HIGHEST_SCORE, aiPlayer.getAIStrategy());
        aiPlayer.setAIStrategy(Player_Model.AIStrategy.FIRST_VALID);
        assertEquals(Player_Model.AIStrategy.FIRST_VALID, aiPlayer.getAIStrategy());
        count = 2;
    }

    @Test
    public void test_GetValidCardIndices() {
        System.out.println("Testing Get Valid Card Indices...");
        player = new Player_Model("Test");
        player.addCard(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        player.addCard(new Card_Model(Card_Model.CardValue.FIVE, Card_Model.CardColour.BLUE));
        player.addCard(new Card_Model(Card_Model.CardValue.WILD, Card_Model.CardColour.WILD));

        Card_Model activeCard = new Card_Model(Card_Model.CardValue.TWO, Card_Model.CardColour.RED);
        List<Integer> valid = player.getValidCardIndices(activeCard, Card_Model.CardColour.RED,
                Card_Model.CardValue.TWO);

        assertTrue(valid.contains(0)); // RED matches
        assertFalse(valid.contains(1)); // BLUE doesn't match
        assertTrue(valid.contains(2)); // WILD always valid
        count = 3;
    }

    @Test
    public void test_AISelectCard() {
        System.out.println("Testing AI Select Card...");
        aiPlayer = new Player_Model("AI", true, Player_Model.AIStrategy.FIRST_VALID);
        aiPlayer.addCard(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        aiPlayer.addCard(new Card_Model(Card_Model.CardValue.FIVE, Card_Model.CardColour.BLUE));

        int selected = aiPlayer.selectCardToPlay(
                new Card_Model(Card_Model.CardValue.TWO, Card_Model.CardColour.RED),
                Card_Model.CardColour.RED, Card_Model.CardValue.TWO, false);

        assertEquals(0, selected); // First valid is RED THREE
        count = 1;
    }

    @Test
    public void test_AISelectCardNoValid() {
        System.out.println("Testing AI Select Card No Valid...");
        aiPlayer = new Player_Model("AI", true);
        aiPlayer.addCard(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.BLUE));
        aiPlayer.addCard(new Card_Model(Card_Model.CardValue.FIVE, Card_Model.CardColour.GREEN));

        int selected = aiPlayer.selectCardToPlay(
                new Card_Model(Card_Model.CardValue.TWO, Card_Model.CardColour.RED),
                Card_Model.CardColour.RED, Card_Model.CardValue.TWO, false);

        assertEquals(-1, selected);
        count = 1;
    }

    @Test
    public void test_HumanSelectCard() {
        System.out.println("Testing Human Select Card...");
        player = new Player_Model("Human", false);
        player.addCard(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));

        int selected = player.selectCardToPlay(
                new Card_Model(Card_Model.CardValue.TWO, Card_Model.CardColour.RED),
                Card_Model.CardColour.RED, Card_Model.CardValue.TWO, false);

        assertEquals(-1, selected);
        count = 1;
    }

    @Test
    public void test_AISelectWildColour() {
        System.out.println("Testing AI Select Wild Colour...");
        aiPlayer = new Player_Model("AI", true);
        aiPlayer.addCard(new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED));
        aiPlayer.addCard(new Card_Model(Card_Model.CardValue.FIVE, Card_Model.CardColour.RED));
        aiPlayer.addCard(new Card_Model(Card_Model.CardValue.ONE, Card_Model.CardColour.BLUE));

        Card_Model.CardColour selected = aiPlayer.selectWildColour();

        assertNotNull(selected);
        assertNotEquals(Card_Model.CardColour.WILD, selected);
        // Should select RED as most common
        assertEquals(Card_Model.CardColour.RED, selected);
        count = 3;
    }

    @Test
    public void test_HumanSelectWildColour() {
        System.out.println("Testing Human Select Wild Colour...");
        player = new Player_Model("Human", false);
        assertNull(player.selectWildColour());
        count = 1;
    }

    @Test
    public void test_FlipAllCards() {
        System.out.println("Testing Flip All Cards...");
        player = new Player_Model("Test");
        Card_Model card = new Card_Model(Card_Model.CardValue.ONE, Card_Model.CardColour.RED,
                Card_Model.CardValue.TWO, Card_Model.CardColour.TEAL);
        player.addCard(card);

        assertEquals(Card_Model.CardSide.LIGHT, card.getCurrentSide());
        player.flipAllCards();
        assertEquals(Card_Model.CardSide.DARK, card.getCurrentSide());
        count = 2;
    }

    @Test
    public void test_AIHighestScoreStrategy() {
        System.out.println("Testing AI Highest Score Strategy...");
        aiPlayer = new Player_Model("AI", true, Player_Model.AIStrategy.HIGHEST_SCORE);
        aiPlayer.addCard(new Card_Model(Card_Model.CardValue.ONE, Card_Model.CardColour.RED)); // Score 1
        aiPlayer.addCard(new Card_Model(Card_Model.CardValue.NINE, Card_Model.CardColour.RED)); // Score 9
        aiPlayer.addCard(new Card_Model(Card_Model.CardValue.SKIP, Card_Model.CardColour.RED)); // Score 20

        int selected = aiPlayer.selectCardToPlay(
                new Card_Model(Card_Model.CardValue.TWO, Card_Model.CardColour.RED),
                Card_Model.CardColour.RED, Card_Model.CardValue.TWO, false);

        assertEquals(2, selected); // SKIP has highest score
        count = 1;
    }

    @Test
    public void test_AIStrategicStrategy() {
        System.out.println("Testing AI Strategic Strategy...");
        aiPlayer = new Player_Model("AI", true, Player_Model.AIStrategy.STRATEGIC);
        aiPlayer.addCard(new Card_Model(Card_Model.CardValue.ONE, Card_Model.CardColour.RED));
        aiPlayer.addCard(new Card_Model(Card_Model.CardValue.WILD, Card_Model.CardColour.WILD));

        int selected = aiPlayer.selectCardToPlay(
                new Card_Model(Card_Model.CardValue.TWO, Card_Model.CardColour.RED),
                Card_Model.CardColour.RED, Card_Model.CardValue.TWO, false);

        assertEquals(0, selected);
        count = 1;
    }

    @Test
    public void test_AISelectDarkSideColour() {
        System.out.println("Testing AI Select Dark Side Colour...");
        aiPlayer = new Player_Model("AI", true);
        Card_Model tealCard = new Card_Model(Card_Model.CardValue.ONE, Card_Model.CardColour.RED,
                Card_Model.CardValue.ONE, Card_Model.CardColour.TEAL);
        Card_Model purpleCard = new Card_Model(Card_Model.CardValue.TWO, Card_Model.CardColour.BLUE,
                Card_Model.CardValue.TWO, Card_Model.CardColour.PURPLE);
        tealCard.flip();
        purpleCard.flip();
        aiPlayer.addCard(tealCard);
        aiPlayer.addCard(purpleCard);
        aiPlayer.addCard(purpleCard);

        Card_Model.CardColour selected = aiPlayer.selectWildColour();

        assertTrue(Card_Model.isDarkSideColour(selected));
        count = 1;
    }


}