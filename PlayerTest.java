import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
public class Player_ModelTest
{
    private static Player_Model player;
    private static int count;

    public Player_ModelTest() {}

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
        ArrayList<Card_Model> temp_list = new ArrayList<>();
        player = new Player_Model();
        assertEquals(0, player.getNumCards());
        assertEquals(temp_list, player.getHand());
        count = 2;
    }
    
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

    @Test
    public void test_getScore() 
    {
        System.out.println("Testing the getScore() method");
        player = new Player_Model();
        player.setScore(10);
        assertEquals(10, player.getScore());
        count = 1;
    }

    @Test
    public void test_getName() 
    {
        System.out.println("Testing the getName() method");
        player = new Player_Model();
        player.setName("Lucas");
        assertEquals("Lucas", player.getName());
        count = 1;
    }

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

    @Test
    public void test_setName() 
    {
        System.out.println("Testing the setName() method");
        player = new Player_Model();
        player.setName("Bobby");
        assertEquals("Bobby", player.getName());
        count = 1;
    }

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
}