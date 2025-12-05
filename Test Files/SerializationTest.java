import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tets suite for Save/Load serialization funcitionality
 * test game saving, laoding, error handling and state preservations
 *
 * @author Lasya Erukulla
 * @version 4.0 - milestone 4
 */
public class SerializationTest
{
    public Uno_Model uno;
    public Uno_Controller controller;
    private static final String TEST_SAVE_FILE = "test_save.txt";

    @BeforeEach
    public void setUp()
    {
        uno = new Uno_Model();
        controller = new Uno_Controller(uno);
        controller.createPlayers(2,0);
        controller.initializeGame();
    }

    @AfterEach
    public void tearDown(){
        File file = new File(TEST_SAVE_FILE);
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * Tests the basic save functionality
     * - verifies that file is actually created in dir
     */
    @Test
    public void testSaveGame(){
        boolean saved = controller.saveGame(TEST_SAVE_FILE);
        assertTrue(saved);
        File file = new File(TEST_SAVE_FILE);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    /**
     * Tests the basic load functionality
     * - verifies that valid svae file can be loaded without errors
     */
    @Test
    public void testLoadGame(){
        controller.saveGame(TEST_SAVE_FILE);
        boolean loaded = controller.loadGame(TEST_SAVE_FILE);
        assertTrue(loaded);
    }

    /**
     * Tests that the game state is preserved after save/laod clicked
     * - verfies active card, color and side are as expected
     */
    @Test
    public void testGameStatePreservation(){
        System.out.println("Testing game state preservation");
        Player_Model initialPlayer = controller.getCurrentPlayer();
        String initialPlayerName = initialPlayer.getName();
        int initialPlayerCards = initialPlayer.getNumCards();
        Card_Model intialActive = controller.getActiveCard();
        Card_Model.CardColour initialColour = controller.getMatchColour();
        boolean initialDarkSide = controller.isDarkSide();
        int initialDeckSize = controller.getRemainingDrawPileCards();

        controller.saveGame(TEST_SAVE_FILE);
        controller.loadGame(TEST_SAVE_FILE);
        assertEquals(initialPlayerName, controller.getCurrentPlayer().getName());
        assertEquals(initialPlayerCards, controller.getCurrentPlayer().getNumCards());
        assertEquals(initialColour, controller.getMatchColour());
        assertEquals(initialDarkSide, controller.isDarkSide());
        assertEquals(initialDeckSize, controller.getRemainingDrawPileCards());
    }

    /**
     * Tests that the players scores are preserved after load/save clicked
     * - verifies score matches expected value
     */
    @Test
    public void testScorePreservation(){
        System.out.println("Testing players score preservation");
        controller.getParticipants().get(0).setScore(100);
        controller.getParticipants().get(1).setScore(200);

        controller.saveGame(TEST_SAVE_FILE);
        controller.loadGame(TEST_SAVE_FILE);
        assertEquals(100, controller.getParticipants().get(0).getScore());
        assertEquals(200, controller.getParticipants().get(1).getScore());
    }

    /**
     * Tests that player hands are preserved after laod/save click
     * - verfies card size is as expected after preservation
     * - verifies the first card is as expected
     */
    @Test
    public void testHandPreservation(){
        System.out.println("Testing player hand preservation");
        Player_Model currentPlayer = controller.getCurrentPlayer();
        int intialHandSize = currentPlayer.getNumCards();
        String firstCardInfo = currentPlayer.getHand().get(0).toString();

        controller.saveGame(TEST_SAVE_FILE);
        controller.handleDrawCard();
        controller.handleDrawCard();
        assertNotEquals(intialHandSize, controller.getCurrentPlayer().getNumCards());
        controller.loadGame(TEST_SAVE_FILE);
        Player_Model loadedPlayer = controller.getCurrentPlayer();
        assertEquals(intialHandSize, loadedPlayer.getNumCards());
        assertEquals(firstCardInfo, loadedPlayer.getHand().get(0).toString());
    }

    /**
     * Tests the turn order is preserved
     * - verifies that the current player is in order as entered
     */
    @Test
    public void testTurnOrderPreservation(){
        System.out.println("Testing player turn order preservation");
        Player_Model initPlayer = controller.getCurrentPlayer();
        String initPlayerName = initPlayer.getName();
        controller.saveGame(TEST_SAVE_FILE);
        controller.handleNextPlayer();
        assertNotEquals(initPlayer, controller.getCurrentPlayer().getName());
        controller.loadGame(TEST_SAVE_FILE);
        assertEquals(initPlayerName, controller.getCurrentPlayer().getName());
    }

    /**
     * Tets the deck state preservation after load/save click
     * - verifies the deck size returns to what it was before
     */
    @Test
    public void testDeckStatePreservation(){
        System.out.println("Testing Deck state preservation");
        int initialDeckSize = controller.getRemainingDrawPileCards();
        controller.saveGame(TEST_SAVE_FILE);
        controller.handleDrawCard();
        controller.handleDrawCard();
        controller.loadGame(TEST_SAVE_FILE);
        assertEquals(initialDeckSize, controller.getRemainingDrawPileCards());
    }

    /**
     * tests that the state is preserved after load/save click
     * - verifies the intialSide is what it was set to be
     */
    @Test
    public void testSideStatePreservation(){
        System.out.println("Testing players side state preservation");
        boolean initialSide = controller.isDarkSide();
        controller.saveGame(TEST_SAVE_FILE);
        controller.loadGame(TEST_SAVE_FILE);
        assertEquals(initialSide, controller.isDarkSide());
    }

    /**
     * Test multiple load/save cycles
     * - verifies cards of current player each cycle is as expected
     */
    @Test
    public void testMultipleSaveLoadCycles(){
        for (int i = 0; i< 3; i++) {
            int cards = controller.getCurrentPlayer().getNumCards();
            controller.saveGame(TEST_SAVE_FILE);
            controller.handleDrawCard();
            controller.loadGame(TEST_SAVE_FILE);
            assertEquals(cards, controller.getCurrentPlayer().getNumCards());
        }
    }

    /**
     * Tesst taht the AI players are preserved
     */
    @Test
    public void testAIPlayerPreservation(){
        System.out.println("Testing AI player preservation");
        controller.resetGame();
        controller.createPlayers(1,1);
        controller.initializeGame();

        boolean hasAI = false;
        for(Player_Model p : controller.getParticipants()){
            if(p.isAI()){
                hasAI = true;
                break;
            }
        }
        assertTrue(hasAI);
        controller.saveGame(TEST_SAVE_FILE);
        controller.loadGame(TEST_SAVE_FILE);
        boolean hasAIAfterLoad = false;
        for(Player_Model p : controller.getParticipants()){
            if(p.isAI()){
                hasAIAfterLoad = true;
                break;
            }
        }
    }

    /**
     * Tests the Card_Model serializaiton
     * - verifies card value is as expected
     * - verifies card colour is as expected
     * -
     */
    @Test
    public void testCardSerialisation(){
        System.out.println("Testing Card_Model serialisation");
        Card_Model card = new Card_Model(Card_Model.CardValue.EIGHT, Card_Model.CardColour.GREEN);
        try{
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(stream);
            out.writeObject(card);
            out.close();

            ByteArrayInputStream stream1 = new ByteArrayInputStream(stream.toByteArray());
            ObjectInputStream in1 = new ObjectInputStream(stream1);
            Card_Model loadedCard = (Card_Model) in1.readObject();
            in1.close();

            assertEquals(card.getCardValue(), loadedCard.getCardValue());
            assertEquals(card.getColour(), loadedCard.getColour());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Test the Player_Model serialization
     * - verfies the name is as expected
     * - verifies the scpre is as expected
     * - verifies the number of cards is as expected
     */
    @Test
    public void testPlayerSerialisation(){
        System.out.println("Testing Player_Model serialisation");
        Player_Model player = new Player_Model("Lasya");
        player.addCard(new Card_Model(Card_Model.CardValue.EIGHT, Card_Model.CardColour.GREEN));
        player.setScore(150);

        try{
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(stream);
            out.writeObject(player);
            out.close();

            ByteArrayInputStream stream1 = new ByteArrayInputStream(stream.toByteArray());
            ObjectInputStream in = new ObjectInputStream(stream1);
            Player_Model loadedPlayer = (Player_Model) in.readObject();
            in.close();

            assertEquals(player.getName(), loadedPlayer.getName());
            assertEquals(player.getScore(), loadedPlayer.getScore());
            assertEquals(player.getNumCards(), loadedPlayer.getNumCards());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Tests the Deck_Model serialization
     * - verifies the deck size is as expected
     */
    @Test
    public void testDeckSerialisation(){
        System.out.println("Testing Deck_Model serialisation");
        Deck_Model deck = new Deck_Model();
        int initialDeckSize = deck.getNumDrawCards();

        try{
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(stream);
            out.writeObject(deck);
            out.close();

            ByteArrayInputStream stream1 = new ByteArrayInputStream(stream.toByteArray());
            ObjectInputStream in = new ObjectInputStream(stream1);
            Deck_Model loadedDeck = (Deck_Model) in.readObject();
            in.close();

            assertEquals(initialDeckSize, loadedDeck.getNumDrawCards());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test Uno_Model serizliation
     * - verifies the number participants is as expected
     */
    @Test
    public void testUnoModelSerialisation(){
        System.out.println("Testing uno model serialisation");
        try{
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(stream);
            out.writeObject(uno);
            out.close();

            ByteArrayInputStream stream1 = new ByteArrayInputStream(stream.toByteArray());
            ObjectInputStream in = new ObjectInputStream(stream1);
            Uno_Model loadedModel = (Uno_Model) in.readObject();
            in.close();

            assertNotNull(loadedModel);
            assertEquals(uno.getParticipants().size(), loadedModel.getParticipants().size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the undo redo history cleared after load
     * - verifies canUndo() functionality allowabolitiy
     */
    @Test
    public void testUndoRedoHistoryClearSerialisation(){
        System.out.println("Testing undo redo history clear serialisation");
        controller.handleDrawCard();
        assertTrue(controller.canUndo());
        controller.saveGame(TEST_SAVE_FILE);
        controller.loadGame(TEST_SAVE_FILE);
        assertFalse(controller.canUndo());
        assertFalse(controller.canRedo());
    }

}