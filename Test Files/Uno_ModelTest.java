import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the Uno_Model class.
 * Validates core game mechanics, turn management, card validation, and game state transitions.
 *
 * @author Saan John
 * @version 2.0 - Milestone 2
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
        assertTrue(game.getRemainingDeckCards() > 0);
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



}
