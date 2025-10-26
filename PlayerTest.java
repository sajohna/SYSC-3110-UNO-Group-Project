import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
public class PlayerTest
{
    private static Player player;
    private static int count;

    public PlayerTest() {}

    /*
     * Sets test player and counter to default values before each test
     */
    @BeforeEach
    public void setUp()
    {
        player = null;
    }

    /*
     * Displays the counter value for each test after they are complete
     */
    @AfterEach
    public void summary() 
    {
        System.out.println("Number of Tests Completed: " + count + "\n");
    }

    @Test
    public void test_DefaultConstructor() 
    {
        System.out.println("Testing the default constructor");
        ArrayList<Card> temp_list = new ArrayList<>();
        player = new Player();
        assertEquals(0, player.getNumCards());
        assertEquals(temp_list, player.getHand());
        count = 2;
    }
    
    @Test
    public void test_getHand() 
    {
        System.out.println("Testing the getHand() method");
        ArrayList<Card> temp_list = new ArrayList<>();
        temp_list.add(new Card(Card.CardValue.THREE, Card.Colour.RED));
        temp_list.add(new Card(Card.CardValue.REVERSE, Card.Colour.GREEN));
        temp_list.add(new Card(Card.CardValue.SKIP, Card.Colour.BLUE));

        player = new Player();
        player.addCard(new Card(Card.CardValue.THREE, Card.Colour.RED));
        player.addCard(new Card(Card.CardValue.REVERSE, Card.Colour.GREEN));
        player.addCard(new Card(Card.CardValue.SKIP, Card.Colour.BLUE));
        for (int i = 0; i < temp_list.size(); i++) {
            assertEquals(temp_list.get(i).getCardValue(), player.getHand().get(i).getCardValue());
            assertEquals(temp_list.get(i).getColour(), player.getHand().get(i).getColour());
            count += 2;
        }
    }
    
    @Test
    public void test_getNumCards() 
    {
        System.out.println("Testing the getNumCards() method");
        player = new Player();
        player.addCard(new Card(Card.CardValue.EIGHT, Card.Colour.YELLOW));
        player.addCard(new Card(Card.CardValue.ONE, Card.Colour.RED));
        assertEquals(2, player.getNumCards());
        player.addCard(new Card(Card.CardValue.WILD, Card.Colour.WILD));
        assertEquals(3, player.getNumCards());
        count = 2;
    }

    @Test
    public void test_getScore() 
    {
        System.out.println("Testing the getScore() method");
        player = new Player();
        player.setScore(10);
        assertEquals(10, player.getScore());
        count = 1;
    }

    @Test
    public void test_getName() 
    {
        System.out.println("Testing the getName() method");
        player = new Player();
        player.setName("Lucas");
        assertEquals("Lucas", player.getName());
        count = 1;
    }

    @Test
    public void test_setScore() 
    {
        System.out.println("Testing the setScore() method");
        player = new Player();
        player.setScore(20);
        assertEquals(20, player.getScore());
        player.setScore(0);
        assertEquals(0, player.getScore());
        count = 2;
    }

    @Test
    public void test_setName() 
    {
        System.out.println("Testing the setName() method");
        player = new Player();
        player.setName("Bobby");
        assertEquals("Bobby", player.getName());
        count = 1;
    }

    @Test
    public void test_addCard() 
    {
        System.out.println("Testing the addCard() method");
        player = new Player();
        player.addCard(new Card(Card.CardValue.FOUR, Card.Colour.YELLOW));
        assertEquals(1, player.getNumCards());
        assertEquals(Card.CardValue.FOUR, player.getHand().get(0).getCardValue());
        assertEquals(Card.Colour.YELLOW, player.getHand().get(0).getColour());
        count = 3;
    }

    @Test
    public void test_drawCard() 
    {
        System.out.println("Testing the drawCard() method");
        Deck deck = new Deck();
        player = new Player();
        player.drawCard(deck);
        assertEquals(1, player.getNumCards());
        assertEquals(103, deck.getNumDrawCards());
        player.drawCard(deck);
        assertEquals(2, player.getNumCards());
        assertEquals(102, deck.getNumDrawCards());
        count = 4;
    }

    @Test
    public void test_removeCard() 
    {
        System.out.println("Testing the removeCard() method");
        player = new Player();
        player.addCard(new Card(Card.CardValue.SEVEN, Card.Colour.RED));
        player.addCard(new Card(Card.CardValue.REVERSE, Card.Colour.BLUE));
        player.addCard(new Card(Card.CardValue.SKIP, Card.Colour.YELLOW));
        player.addCard(new Card(Card.CardValue.TWO, Card.Colour.RED));
        player.removeCard(1);
        assertEquals(3, player.getNumCards());
        assertEquals(Card.CardValue.SEVEN, player.getHand().get(0).getCardValue());
        assertEquals(Card.Colour.RED, player.getHand().get(0).getColour());
        assertEquals(Card.CardValue.SKIP, player.getHand().get(1).getCardValue());
        assertEquals(Card.Colour.YELLOW, player.getHand().get(1).getColour());
        assertEquals(Card.CardValue.TWO, player.getHand().get(2).getCardValue());
        assertEquals(Card.Colour.RED, player.getHand().get(2).getColour());
        count = 7;
    }

    @Test
    public void test_playCard() 
    {
        System.out.println("Testing the playCard() method");
        player = new Player();
        player.addCard(new Card(Card.CardValue.FOUR, Card.Colour.BLUE));
        player.addCard(new Card(Card.CardValue.REVERSE, Card.Colour.BLUE));
        Card played = player.playCard(1);
        assertEquals(Card.CardValue.REVERSE, played.getCardValue());
        assertEquals(Card.Colour.BLUE, played.getColour());
        count = 2;
    }
}