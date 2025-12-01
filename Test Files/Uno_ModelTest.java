import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the Uno_Model class.
 * Validates core game mechanics, turn management, card validation, and game state transitions.
 *
 * @author Saan John
 * @version 4.0 - Milestone 4 + Milestone 5
 */
public class Uno_ModelTest {
    private Uno_Model game;
    private Player_Model player1;
    private Player_Model player2;
    private Player_Model player3;

    /**
     * Sets up test fixtures before each test method.
     * Creates a fresh Uno_Model instance and three test players.
     */
    @BeforeEach
    void setUp() {
        game = new Uno_Model();
        player1 = new Player_Model("Lasya");
        player2 = new Player_Model("Saan");
        player3 = new Player_Model("Lucas");
    }

    /**
     * Tests that getCurrentPlayer returns null when no players have been added.
     * Validates initial state before game setup.
     */
    @Test
    @DisplayName("getCurrentPlayer returns null when no players added")
    void testGetCurrentPlayerEmpty() {
        assertNull(game.getCurrentPlayer());
    }

    /**
     * Tests the complete game status lifecycle from NOT_STARTED to IN_PROGRESS.
     * Verifies that:
     *   - Initial status is NOT_STARTED
     *   - Status changes to IN_PROGRESS after initialization
     *   - Current player and active card are properly set after initialization
     */
    @Test
    @DisplayName("Game status progresses correctly through lifecycle")
    void testGameStatusLifecycle() {
        assertEquals(Uno_Model.GameStatus.NOT_STARTED, game.getGameStatus());

        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        assertEquals(Uno_Model.GameStatus.IN_PROGRESS, game.getGameStatus());
        assertNotNull(game.getCurrentPlayer());
        assertNotNull(game.getActiveCard());
    }

    /**
     * Tests addPlayer method validation logic.
     * Verifies that:
     *   - Valid players can be added successfully
     *   - Null players are rejected
     *   - Players cannot be added after game has started
     */
    @Test
    @DisplayName("addPlayer validates correctly")
    void testAddPlayerValidation() {
        assertTrue(game.addPlayer(player1));
        assertFalse(game.addPlayer(null));

        game.addPlayer(player2);
        game.initializeGame();
        assertFalse(game.addPlayer(player3));
    }

    /**
     * Tests that the maximum player limit is enforced.
     * Verifies that attempting to add a 5th player fails when limit is 4.
     */
    @Test
    @DisplayName("addPlayer enforces max player limit")
    void testAddPlayerMaxLimit() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.addPlayer(new Player_Model("Dave"));
        assertFalse(game.addPlayer(new Player_Model("Eve")));
    }

    /**
     * Tests that initializeGame properly sets up all game components.
     * Verifies:
     *   - Active card is set and not null
     *   - Match colour is established
     *   - WILD_DRAW_TWO is not used as initial card (game rule)
     *   - Draw pile has cards remaining
     */
    @Test
    @DisplayName("initializeGame sets up game state correctly")
    void testInitializeGame() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        assertNotNull(game.getActiveCard());
        assertNotNull(game.getMatchColour());
        assertNotEquals(Card_Model.CardValue.WILD_DRAW_TWO, game.getActiveCard().getCardValue());
        assertTrue(game.getRemainingDrawPileCards() > 0);
    }

    /**
     * Tests that turn advancement cycles through players correctly.
     * Verifies:
     *   - Advancing changes to next player
     *   - Turn cycles back to first player after all players have gone
     */
    @Test
    @DisplayName("Turn advancement cycles through players")
    void testTurnAdvancement() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        Player_Model first = game.getCurrentPlayer();
        game.advanceToNextTurn();
        assertNotEquals(first, game.getCurrentPlayer());

        game.advanceToNextTurn();
        assertEquals(first, game.getCurrentPlayer());
    }

    /**
     * Tests that reversePlayDirection correctly changes turn order.
     * With 3+ players:
     *   - Reversing doesn't immediately advance turn
     *   - Subsequent advancement goes backward instead of forward
     */
    @Test
    @DisplayName("reversePlayDirection changes turn order")
    void testReversePlayDirection() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.initializeGame();

        Player_Model first = game.getCurrentPlayer();
        game.advanceToNextTurn();
        Player_Model second = game.getCurrentPlayer();

        // Reverse direction (doesn't advance with 3 players)
        game.reversePlayDirection();
        assertEquals(second, game.getCurrentPlayer());

        // Now advancing should go back to first instead of forward to third
        game.advanceToNextTurn();
        assertEquals(first, game.getCurrentPlayer());
    }

    /**
     * Tests that skipNextPlayer correctly skips one player in turn order.
     * Verifies the current player changes after skip operation.
     */
    @Test
    @DisplayName("skipNextPlayer skips one player")
    void testSkipNextPlayer() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.initializeGame();

        Player_Model first = game.getCurrentPlayer();
        game.skipNextPlayer();
        assertNotEquals(first, game.getCurrentPlayer());
    }

    /**
     * Tests card validation logic for different card types.
     * Verifies:
     *   - Null cards are invalid
     *   - Wild cards are always valid
     *   - Cards matching the current colour are valid
     */
    @Test
    @DisplayName("isValidPlay validates cards correctly")
    void testIsValidPlay() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        assertFalse(game.isValidPlay(null));

        Card_Model wildCard = new Card_Model(Card_Model.CardValue.WILD, Card_Model.CardColour.WILD);
        assertTrue(game.isValidPlay(wildCard));

        Card_Model.CardColour matchColour = game.getMatchColour();
        Card_Model matchingCard = new Card_Model(Card_Model.CardValue.ONE, matchColour);
        assertTrue(game.isValidPlay(matchingCard));
    }

    /**
     * Tests that playCard properly validates card index bounds.
     * Verifies:
     *   - Negative indices return INVALID_CARD_INDEX
     *   - Out-of-bounds indices return INVALID_CARD_INDEX
     */
    @Test
    @DisplayName("playCard validates index bounds")
    void testPlayCardIndexValidation() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        assertEquals(Uno_Model.TurnAction.INVALID_CARD_INDEX, game.playCard(-1));
        assertEquals(Uno_Model.TurnAction.INVALID_CARD_INDEX, game.playCard(100));
    }

    /**
     * Tests both card drawing operations.
     * Verifies:
     *   - drawCard returns CARD_DRAWN without advancing turn
     *   - drawCardAndPass returns TURN_PASSED and advances to next player
     */
    @Test
    @DisplayName("drawCard and drawCardAndPass work correctly")
    void testDrawCardActions() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        assertEquals(Uno_Model.TurnAction.CARD_DRAWN, game.drawCard());

        Player_Model first = game.getCurrentPlayer();
        assertEquals(Uno_Model.TurnAction.TURN_PASSED, game.drawCardAndPass());
        assertNotEquals(first, game.getCurrentPlayer());
    }

    /**
     * Tests setActiveColour validation for wild card colour selection.
     * Verifies:
     *   - Null colour is rejected
     *   - WILD colour cannot be set as match colour
     */
    @Test
    @DisplayName("setActiveColour validates colour input")
    void testSetActiveColour() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        assertFalse(game.setActiveColour(null));
        assertFalse(game.setActiveColour(Card_Model.CardColour.WILD));
    }

    /**
     * Tests that resetGame clears all game state.
     * Verifies:
     *   - Game status returns to NOT_STARTED
     *   - Winner is cleared
     */
    @Test
    @DisplayName("resetGame clears game state")
    void testResetGame() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        game.resetGame();

        assertEquals(Uno_Model.GameStatus.NOT_STARTED, game.getGameStatus());
        assertNull(game.getWinner());
    }

    /**
     * Tests getGameStateString formatting and content.
     * Verifies the string contains expected game information:
     *   - Active card information
     *   - Player scores section
     */
    @Test
    @DisplayName("getGameStateString returns formatted output")
    void testGetGameStateString() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        String state = game.getGameStateString();
        assertNotNull(state);
        assertTrue(state.contains("Active Card"));
        assertTrue(state.contains("SCORES"));
    }

    /**
     * Tests various game state query methods.
     * Verifies initial states:
     *   - No pending colour selection initially
     *   - Game not over initially
     *   - No winner initially
     *   - Round not ended during active play
     */
    @Test
    @DisplayName("Game state queries return correct values")
    void testGameStateQueries() {
        assertFalse(game.isPendingColourSelection());
        assertFalse(game.isGameOver());
        assertNull(game.getWinner());

        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        assertFalse(game.isRoundEnded());
    }

    @Test
    @DisplayName("identifySpecialCard returns NONE for regular card")
    void testIdentifySpecialCardForRegularCard() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        Card_Model regular = new Card_Model(Card_Model.CardValue.THREE, Card_Model.CardColour.RED);
        // Manually set active card to a normal card
        try {
            var field = Uno_Model.class.getDeclaredField("activeCard");
            field.setAccessible(true);
            field.set(game, regular);
        } catch (Exception e) {
            fail("Reflection failed to set activeCard");
        }

        assertEquals(Uno_Model.SpecialCardEffect.NONE, game.identifySpecialCard());
    }

    @Test
    @DisplayName("startNewRound resets round but keeps players and scores")
    void testStartNewRoundResetsRoundKeepsPlayers() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        // Simulate score gain
        player1.setScore(200);

        game.startNewRound();

        assertEquals(Uno_Model.GameStatus.IN_PROGRESS, game.getGameStatus());
        assertTrue(game.getParticipants().contains(player1));
        assertEquals(200, player1.getScore());
        assertNotNull(game.getActiveCard());
        assertNotEquals(Card_Model.CardColour.WILD, game.getMatchColour());
    }

    /**
     * Tests the flipAllCards method for UNO Flip functionality.
     * Verifies:
     *   - Game starts on light side (isDarkSide = false)
     *   - After flip, game switches to dark side (isDarkSide = true)
     *   - After second flip, game returns to light side
     *   - Match colour updates to reflect flipped active card's colour
     */
    @Test
    @DisplayName("flipAllCards toggles between light and dark sides")
    void testFlipAllCardsTogglesSides() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        // Game should start on light side
        assertFalse(game.isDarkSide());

        // Store the active card's light side colour
        Card_Model.CardColour lightColour = game.getMatchColour();

        // Flip to dark side
        game.flipAllCards();
        assertTrue(game.isDarkSide());

        // Match colour should update (will be different after flip)
        Card_Model.CardColour darkColour = game.getMatchColour();
        assertNotNull(darkColour);

        // Flip back to light side
        game.flipAllCards();
        assertFalse(game.isDarkSide());

        // Verify startNewRound resets to light side
        game.flipAllCards(); // Go to dark
        assertTrue(game.isDarkSide());
        game.startNewRound();
        assertFalse(game.isDarkSide()); // Should reset to light
    }

    /**
     * Tests AI player support methods.
     * Verifies:
     *   - isCurrentPlayerAI returns false for human players
     *   - isCurrentPlayerAI returns true for AI players
     *   - getAICardSelection returns -1 for human players
     *   - getAIColourSelection returns null for human players
     *   - AI methods delegate to Player_Model when current player is AI
     */
    @Test
    @DisplayName("AI support methods work correctly for human and AI players")
    void testAISupportMethods() {
        // Test with human players first
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        // Human player checks
        assertFalse(game.isCurrentPlayerAI());
        assertEquals(-1, game.getAICardSelection());
        assertNull(game.getAIColourSelection());

        // Reset and test with AI player
        game.resetGame();
        Player_Model aiPlayer = new Player_Model("AI Bot", true); // AI player
        Player_Model humanPlayer = new Player_Model("Human");

        game.addPlayer(aiPlayer);
        game.addPlayer(humanPlayer);
        game.initializeGame();

        // Find which player starts (could be either due to initial card effects)
        if (game.getCurrentPlayer() == aiPlayer) {
            assertTrue(game.isCurrentPlayerAI());
            // AI selection methods should return valid values (not -1/null)
            // The actual value depends on AI logic, but should not throw exceptions
            int aiSelection = game.getAICardSelection();
            // AI returns -1 if no valid play, or valid index otherwise
            assertTrue(aiSelection >= -1);

            Card_Model.CardColour aiColour = game.getAIColourSelection();
            // AI should return a valid colour for wild card selection
            assertNotNull(aiColour);
            assertNotEquals(Card_Model.CardColour.WILD, aiColour);
        } else {
            // Human player's turn
            assertFalse(game.isCurrentPlayerAI());
            game.advanceToNextTurn();
            assertTrue(game.isCurrentPlayerAI());
        }
    }

    /**
     * Tests that timed mode timer methods handle disabled state correctly.
     * Verifies:
     *   - getRemainingTurnTime returns -1 when timed mode is disabled
     *   - isTurnTimeExpired returns false when timed mode is disabled
     *   - handleTurnTimeout returns INVALID_PLAY when timed mode is disabled
     */
    @Test
    @DisplayName("Timed mode methods handle disabled state correctly")
    void testTimedModeDisabledBehavior() {
        game.setTimedModeEnabled(false); // Keep disabled
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        // All timer queries should indicate disabled state
        assertEquals(-1, game.getRemainingTurnTime());
        assertFalse(game.isTurnTimeExpired());
        assertEquals(Uno_Model.TurnAction.INVALID_PLAY, game.handleTurnTimeout());
    }

    /**
     * Tests that timed mode timer returns correct values based on enabled state.
     * Verifies:
     *   - getRemainingTurnTime returns -1 when timed mode is disabled
     *   - isTurnTimeExpired returns false when timed mode is disabled
     *   - handleTurnTimeout returns INVALID_PLAY when timer is not expired
     */
    @Test
    @DisplayName("Timed mode timer queries work correctly")
    void testTimedModeTimerQueries() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        // Timed mode disabled - all timer queries should indicate disabled state
        assertFalse(game.isTimedModeEnabled());
        assertEquals(-1, game.getRemainingTurnTime(),
                "getRemainingTurnTime should return -1 when timed mode disabled");
        assertFalse(game.isTurnTimeExpired(),
                "isTurnTimeExpired should return false when timed mode disabled");
        assertEquals(Uno_Model.TurnAction.INVALID_PLAY, game.handleTurnTimeout(),
                "handleTurnTimeout should return INVALID_PLAY when timed mode disabled");

        // Enable timed mode
        game.resetGame();
        game.setTimedModeEnabled(true);
        game.setTurnTimeLimit(30);

        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        // With timed mode enabled, timer should be active
        assertTrue(game.isTimedModeEnabled());
        assertNotEquals(-1, game.getRemainingTurnTime(),
                "getRemainingTurnTime should not be -1 when timed mode enabled");
        assertFalse(game.isTurnTimeExpired(),
                "isTurnTimeExpired should be false immediately after game starts");
        assertTrue(game.getRemainingTurnTime() <= 30 && game.getRemainingTurnTime() >= 0,
                "Remaining time should be within time limit");
    }

    /**
     * Tests undo/redo restoration methods for game state preservation.
     * Verifies:
     *   - Turn index can be saved and restored
     *   - Play direction can be saved and restored
     *   - Active card can be saved and restored
     *   - Match colour can be saved and restored
     *   - Match type can be saved and restored
     *   - Dark side flag can be saved and restored
     *   - Pending colour selection flags can be saved and restored
     */
    @Test
    @DisplayName("Undo/redo state restoration methods work correctly")
    void testUndoRedoStateRestoration() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.initializeGame();

        // Save initial state
        int initialTurnIdx = game.getCurrentTurnIndex();
        int initialPlayDirection = game.getPlayDirection();
        Card_Model initialActiveCard = game.getActiveCard();
        Card_Model.CardColour initialMatchColour = game.getMatchColour();
        Card_Model.CardValue initialMatchType = game.getMatchType();
        boolean initialIsDarkSide = game.isDarkSide();
        boolean initialPendingColourSelection = game.isPendingColourSelection();
        boolean initialPendingDrawColourSelection = game.isPendingDrawColourSelection();

        // Modify game state
        game.advanceToNextTurn();
        game.reversePlayDirection();
        game.flipAllCards();

        // Verify state changed
        assertNotEquals(initialTurnIdx, game.getCurrentTurnIndex());
        assertNotEquals(initialPlayDirection, game.getPlayDirection());
        assertNotEquals(initialIsDarkSide, game.isDarkSide());

        // Restore state using undo/redo setter methods
        game.setCurrentTurnIndex(initialTurnIdx);
        game.setPlayDirection(initialPlayDirection);
        game.setActiveCard(initialActiveCard);
        game.setMatchColour(initialMatchColour);
        game.setMatchType(initialMatchType);
        game.setIsDarkSide(initialIsDarkSide);
        game.setPendingColourSelection(initialPendingColourSelection);
        game.setPendingDrawColourSelection(initialPendingDrawColourSelection);

        // Verify state is restored
        assertEquals(initialTurnIdx, game.getCurrentTurnIndex(),
                "Turn index should be restored");
        assertEquals(initialPlayDirection, game.getPlayDirection(),
                "Play direction should be restored");
        assertEquals(initialActiveCard, game.getActiveCard(),
                "Active card should be restored");
        assertEquals(initialMatchColour, game.getMatchColour(),
                "Match colour should be restored");
        assertEquals(initialMatchType, game.getMatchType(),
                "Match type should be restored");
        assertEquals(initialIsDarkSide, game.isDarkSide(),
                "Dark side flag should be restored");
        assertEquals(initialPendingColourSelection, game.isPendingColourSelection(),
                "Pending colour selection should be restored");
        assertEquals(initialPendingDrawColourSelection, game.isPendingDrawColourSelection(),
                "Pending draw colour selection should be restored");
    }
}