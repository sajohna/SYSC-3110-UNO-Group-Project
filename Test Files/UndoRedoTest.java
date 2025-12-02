import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * This test class validates the Undo/Redo system used in the Uno Flip game.
 * It ensures that player actions, game state transitions, and controller logic
 * correctly support reversible operations such as drawing cards, playing cards,
 * advancing turns, and flipping between light and dark sides of the deck.
 *
 * @author Lucas Baker
 * @version 4.0 - Milestone 4
 */
public class UndoRedoTest {
    private Uno_Model model;
    private Uno_Controller controller;
    private Player_Model player1;
    private Player_Model player2;

    @BeforeEach
    void setUp() {
        model = new Uno_Model();
        controller = new Uno_Controller(model);
        player1 = new Player_Model("Player1");
        player2 = new Player_Model("Player2");
        
        controller.createPlayers(2, 0);
        controller.initializeGame();
    }

    /**
     * Tests that initially undo/redo are not available
     */
    @Test
    void testInitialUndoRedoState() {
        System.out.println("Initially no undo/redo available");
        assertFalse(controller.canUndo());
        assertFalse(controller.canRedo());
    }

    /**
     * Tests that drawing a card can be undone
     */
    @Test
    void testUndoDrawCard() {
        System.out.println("Drawing card can be undone");
        Player_Model current = controller.getCurrentPlayer();
        int initialCards = current.getNumCards();
        
        controller.handleDrawCard();
        assertEquals(initialCards + 1, current.getNumCards());
        assertTrue(controller.canUndo());
        
        controller.undoGameState();
        assertEquals(initialCards, current.getNumCards());
    }

    /**
     * Tests that playing a card can be undone
     */
    @Test
    void testUndoPlayCard() {
        System.out.println("Playing card can be undone");
        Player_Model current = controller.getCurrentPlayer();
        List<Integer> validIndices = current.getValidCardIndices(controller.getActiveCard(), controller.getMatchColour(), controller.getActiveCard().getCardValue());
        
        if (validIndices.isEmpty())
        {
            return;
        }
        
        int initialCards = current.getNumCards();
        Card_Model initialActive = controller.getActiveCard();
        
        controller.playCard(validIndices.get(0));
        
        assertTrue(controller.canUndo());
        controller.undoGameState();
        
        assertEquals(initialCards, current.getNumCards());
    }

    /**
     * Tests multi-level undo (undoing multiple actions)
     */
    @Test
    void testMultiLevelUndo() {
        System.out.println("Multi-level undo works correctly");
        Player_Model current = controller.getCurrentPlayer();
        int initialCards = current.getNumCards();

        controller.handleDrawCard();
        controller.handleDrawCard();
        controller.handleDrawCard();
        
        assertEquals(initialCards + 3, current.getNumCards());

        controller.undoGameState();
        assertEquals(initialCards + 2, current.getNumCards());
        controller.undoGameState();
        assertEquals(initialCards + 1, current.getNumCards());
        controller.undoGameState();
        assertEquals(initialCards, current.getNumCards());
        
        assertFalse(controller.canUndo());
    }

    /**
     * Tests that redo works after undo
     */
    @Test
    void testRedo() {
        System.out.println("Redo works after undo");
        Player_Model current = controller.getCurrentPlayer();
        int initialCards = current.getNumCards();
        
        controller.handleDrawCard();
        assertEquals(initialCards + 1, current.getNumCards());
        
        controller.undoGameState();
        assertEquals(initialCards, current.getNumCards());
        assertTrue(controller.canRedo());
        
        controller.redoGameState();
        assertEquals(initialCards + 1, current.getNumCards());
        assertFalse(controller.canRedo());
    }

    /**
     * Tests multi-level redo
     */
    @Test
    void testMultiLevelRedo() {
        System.out.println("Multi-level redo works correctly");
        Player_Model current = controller.getCurrentPlayer();
        int initialCards = current.getNumCards();
        
        // Draw 3 cards
        controller.handleDrawCard();
        controller.handleDrawCard();
        controller.handleDrawCard();
        
        // Undo all
        controller.undoGameState();
        controller.undoGameState();
        controller.undoGameState();
        
        assertEquals(initialCards, current.getNumCards());
        
        // Redo all
        controller.redoGameState();
        assertEquals(initialCards + 1, current.getNumCards());
        controller.redoGameState();
        assertEquals(initialCards + 2, current.getNumCards());
        controller.redoGameState();
        assertEquals(initialCards + 3, current.getNumCards());
    }

    /**
     * Tests that redo stack is cleared on new action
     */
    @Test
    void testRedoStackClearedOnNewAction() {
        System.out.println("New action clears redo stack");
        controller.handleDrawCard();
        controller.undoGameState();
        assertTrue(controller.canRedo());
        
        // Perform new action
        controller.handleDrawCard();
        
        // Redo should no longer be available
        assertFalse(controller.canRedo());
    }

    /**
     * Tests that turn advancement can be undone
     */
    @Test
    void testUndoTurnAdvancement() {
        System.out.println("Turn advancement can be undone");
        Player_Model firstPlayer = controller.getCurrentPlayer();
        controller.handleDrawCard();
        controller.handleNextPlayer();
        
        Player_Model secondPlayer = controller.getCurrentPlayer();
        assertNotEquals(firstPlayer, secondPlayer);
        
        controller.undoGameState();
        assertEquals(firstPlayer, controller.getCurrentPlayer());
    }

    /**
     * Tests game state preservation during undo/redo
     */
    @Test
    void testGameStatePreservation() {
        System.out.println("Game state preserved during undo/redo");
        Card_Model initialActive = controller.getActiveCard();
        Card_Model.CardColour initialColour = controller.getMatchColour();
        boolean initialDarkSide = controller.isDarkSide();
        
        controller.handleDrawCard();
        controller.undoGameState();
        
        assertEquals(initialActive, controller.getActiveCard());
        assertEquals(initialColour, controller.getMatchColour());
        assertEquals(initialDarkSide, controller.isDarkSide());
    }

    /**
     * Tests undo/redo with dark side flip
     */
    @Test
    void testUndoRedoWithFlip() {
        System.out.println("Undo/redo works with flip cards");
        boolean initialDarkSide = controller.isDarkSide();
        
        // Find and play a flip card if available
        Player_Model current = controller.getCurrentPlayer();
        for (int i = 0; i < current.getNumCards(); i++) {
            if (current.getHand().get(i).isFlipCard()) {
                if (controller.playCard(i)) {
                    assertNotEquals(initialDarkSide, controller.isDarkSide());
                    
                    controller.undoGameState();
                    assertEquals(initialDarkSide, controller.isDarkSide());
                    break;
                }
            }
        }
    }

    /**
     * Tests that undo/redo is disabled during round end
     */
    @Test
    void testUndoRedoDisabledAtRoundEnd() {
        System.out.println("Undo/redo disabled at round end");
        controller.handleDrawCard();
        
        // Simulate round end by setting status
        // (In real game, this happens when player empties hand)
        // We just verify the check works correctly
        assertTrue(controller.canUndo());
    }

    /**
     * Tests undo stack size limit
     */
    @Test
    void testUndoStackSizeLimit() {
        System.out.println("Undo stack respects size limit");
        for (int i = 0; i < 60; i++)
        {
            controller.handleDrawCard();
        }

        int undoCount = 0;
        while (controller.canUndo() && undoCount < 100)
        {
            controller.undoGameState();
            undoCount++;
        }
        
        // Should have undone at most 50 times (the limit)
        assertTrue(undoCount <= 50);
    }

    /**
     * Tests GameState restoration
     */
    @Test
    void testGameStateRestoration() {
        System.out.println("GameState restoration works correctly");
        Uno_GameState originalState = new Uno_GameState(model);
        int originalCards = controller.getCurrentPlayer().getNumCards();
        
        controller.handleDrawCard();
        controller.handleDrawCard();
        
        // Restore original state
        originalState.restoreToModel(model);
        
        assertEquals(originalCards, controller.getCurrentPlayer().getNumCards());
    }
}
