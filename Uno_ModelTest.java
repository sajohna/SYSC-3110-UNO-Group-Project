import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test suite for Uno_Model class.
 * Tests core game state management and business logic for Milestone 1.
 *
 * @version 1.0 - Milestone 1
 */
public class Uno_ModelTest {
    private Uno_Model game;
    private Player_Model player1;
    private Player_Model player2;
    private Player_Model player3;

    @BeforeEach
    public void setUp() {
        game = new Uno_Model();
        player1 = new Player_Model("Alice");
        player2 = new Player_Model("Bob");
        player3 = new Player_Model("Charlie");
    }

    /**
     * Tests player management (2-4 players) and game initialization.
     * Verifies: Adding players, player count validation, initialization state,
     * initial card dealt, and preventing players being added after game starts.
     */
    @Test
    @DisplayName("Test player management and game initialization")
    public void testPlayerManagementAndInitialization() {
        // Test constants
        assertEquals(2, game.getMinParticipants(), "Min players should be 2");
        assertEquals(4, game.getMaxParticipants(), "Max players should be 4");
        assertEquals(500, game.getTargetScore(), "Target score should be 500");

        // Test player validation (2-4 players)
        assertFalse(game.isValidPlayerCount(), "0 players invalid");
        assertTrue(game.addPlayer(player1), "Should add player 1");
        assertFalse(game.isValidPlayerCount(), "1 player invalid");
        assertTrue(game.addPlayer(player2), "Should add player 2");
        assertTrue(game.isValidPlayerCount(), "2 players valid");
        assertTrue(game.addPlayer(player3), "Should add player 3");
        assertEquals(3, game.getPlayerCount(), "Should have 3 players");

        // Test initialization with invalid player count
        setUp();
        game.addPlayer(player1);
        assertThrows(IllegalStateException.class, () -> game.initializeGame());

        // Test proper initialization
        game.addPlayer(player2);
        game.initializeGame();
        assertNotNull(game.getActiveCard(), "Should have active card");
        assertEquals(Uno_Model.GameStatus.IN_PROGRESS, game.getGameStatus());
        assertNotEquals(Card_Model.CardValue.WILD_DRAW_TWO, game.getInitialCard().getCardType());
        assertEquals(93, game.getRemainingDeckCards(), "Should have dealt 15 cards");

        // Cannot add players after start
        assertFalse(game.addPlayer(player3), "Cannot add after start");
    }

    /**
     * Tests turn management, advancement, and skip functionality.
     * Verifies: Current player tracking, turn index advancement,
     * wrap-around behavior, and skip next player functionality.
     */
    @Test
    @DisplayName("Test turn management, advancement, and skip")
    public void testTurnManagement() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.initializeGame();

        // Test current player and advancement
        assertEquals(0, game.getCurrentTurnIndex(), "Should start at 0");
        assertEquals(player1, game.getCurrentPlayer(), "Player 1 goes first");

        game.advanceToNextTurn();
        assertEquals(1, game.getCurrentTurnIndex(), "Advance to 1");
        assertEquals(player2, game.getCurrentPlayer(), "Player 2's turn");

        game.advanceToNextTurn();
        game.advanceToNextTurn();
        assertEquals(0, game.getCurrentTurnIndex(), "Wrap to 0");

        // Test skip functionality
        game.skipNextPlayer();
        assertEquals(2, game.getCurrentTurnIndex(), "Skip to player 2");
    }

    /**
     * Tests reverse direction for both 3+ player and 2-player games.
     * Verifies: Play direction tracking, direction reversal in 3+ player games,
     * and special case where reverse acts as skip in 2-player games.
     */
    @Test
    @DisplayName("Test reverse direction for 3+ and 2-player games")
    public void testReverseDirection() {
        // Test 3-player reverse
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.initializeGame();

        assertEquals(1, game.getPlayDirection(), "Start forward");
        game.advanceToNextTurn();
        assertEquals(1, game.getCurrentTurnIndex(), "Advance forward");

        game.reversePlayDirection();
        assertEquals(-1, game.getPlayDirection(), "Reversed");
        game.advanceToNextTurn();
        assertEquals(0, game.getCurrentTurnIndex(), "Advance backward");

        // Test 2-player reverse acts as skip
        setUp();
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        int startIdx = game.getCurrentTurnIndex();
        game.reversePlayDirection();
        assertNotEquals(startIdx, game.getCurrentTurnIndex(), "2-player reverse skips");
    }

    /**
     * Tests draw card functionality and pass turn feature.
     * Verifies: Drawing cards from deck, deck size tracking,
     * drawing without passing turn, and drawing with passing turn.
     */
    @Test
    @DisplayName("Test draw card and pass turn functionality")
    public void testDrawCardAndPass() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        int deckBefore = game.getRemainingDeckCards();
        int turnBefore = game.getCurrentTurnIndex();

        // Test draw without pass
        Uno_Model.TurnAction action = game.drawCard();
        assertEquals(Uno_Model.TurnAction.CARD_DRAWN, action);
        assertEquals(deckBefore - 1, game.getRemainingDeckCards(), "Deck decreases");
        assertEquals(turnBefore, game.getCurrentTurnIndex(), "Turn doesn't advance");

        // Test draw and pass turn
        deckBefore = game.getRemainingDeckCards();
        action = game.drawCardAndPass();
        assertEquals(Uno_Model.TurnAction.TURN_PASSED, action);
        assertEquals(deckBefore - 1, game.getRemainingDeckCards(), "Deck decreases");
        assertNotEquals(turnBefore, game.getCurrentTurnIndex(), "Turn advances");
    }

    /**
     * Tests card placement validation according to game rules.
     * Verifies: Wild cards always valid, matching color/type validation,
     * null card rejection, and invalid card index handling.
     */
    @Test
    @DisplayName("Test card placement validation")
    public void testCardValidation() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        // Wild cards always valid
        assertTrue(game.isValidPlay(new Card_Model(Card_Model.CardColour.WILD, Card_Model.CardValue.WILD)));
        assertTrue(game.isValidPlay(new Card_Model(Card_Model.CardColour.WILD, Card_Model.CardValue.WILD_DRAW_TWO)));

        // Null card invalid
        assertFalse(game.isValidPlay(null), "Null card invalid");

        // Invalid card indices
        assertEquals(Uno_Model.TurnAction.INVALID_CARD_INDEX, game.playCard(-1));
        assertEquals(Uno_Model.TurnAction.INVALID_CARD_INDEX, game.playCard(100));
    }

    /**
     * Tests identification of all special action cards.
     * Verifies: Correct identification of Draw One, Reverse, Skip, Wild, and Wild Draw Two cards,
     * including regular cards, and whether each special card requires color selection.
     */
    @Test
    @DisplayName("Test all special card identification (Skip, Reverse, Draw One, Wild, Wild Draw Two)")
    public void testSpecialCardIdentification() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.distributeInitialCards();

        // Test null active card
        assertEquals(Uno_Model.SpecialCardEffect.NONE, game.identifySpecialCard());

        try {
            java.lang.reflect.Field activeCardField = Uno_Model.class.getDeclaredField("activeCard");
            activeCardField.setAccessible(true);

            // Regular number card
            activeCardField.set(game, new Card_Model(Card_Model.CardColour.RED, Card_Model.CardValue.FIVE));
            assertEquals(Uno_Model.SpecialCardEffect.NONE, game.identifySpecialCard());

            // SKIP card
            activeCardField.set(game, new Card_Model(Card_Model.CardColour.RED, Card_Model.CardValue.SKIP));
            assertEquals(Uno_Model.SpecialCardEffect.SKIP, game.identifySpecialCard());
            assertFalse(game.requiresColourSelection(), "Skip doesn't require colour");

            // REVERSE card
            activeCardField.set(game, new Card_Model(Card_Model.CardColour.BLUE, Card_Model.CardValue.REVERSE));
            assertEquals(Uno_Model.SpecialCardEffect.REVERSE, game.identifySpecialCard());
            assertFalse(game.requiresColourSelection(), "Reverse doesn't require colour");

            // DRAW_ONE card
            activeCardField.set(game, new Card_Model(Card_Model.CardColour.GREEN, Card_Model.CardValue.DRAW_ONE));
            assertEquals(Uno_Model.SpecialCardEffect.DRAW_ONE, game.identifySpecialCard());
            assertFalse(game.requiresColourSelection(), "Draw One doesn't require colour");

            // WILD card
            activeCardField.set(game, new Card_Model(Card_Model.CardColour.WILD, Card_Model.CardValue.WILD));
            assertEquals(Uno_Model.SpecialCardEffect.WILD, game.identifySpecialCard());
            assertTrue(game.requiresColourSelection(), "Wild requires colour");

            // WILD_DRAW_TWO card
            activeCardField.set(game, new Card_Model(Card_Model.CardColour.WILD, Card_Model.CardValue.WILD_DRAW_TWO));
            assertEquals(Uno_Model.SpecialCardEffect.WILD_DRAW_TWO, game.identifySpecialCard());
            assertTrue(game.requiresColourSelection(), "Wild Draw Two requires colour");

        } catch (Exception e) {
            fail("Could not test special cards: " + e.getMessage());
        }
    }

    /**
     * Tests scoring system and game status tracking.
     * Verifies: Hand value calculation, round score tracking,
     * game status transitions (NOT_STARTED, IN_PROGRESS),
     * and winner/round end checking.
     */
    @Test
    @DisplayName("Test scoring and game status")
    public void testScoringAndStatus() {
        // Test initial status
        assertEquals(Uno_Model.GameStatus.NOT_STARTED, game.getGameStatus());
        assertNull(game.getWinner());
        assertFalse(game.isRoundEnded());
        assertFalse(game.isGameOver());

        // Test after initialization
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertEquals(Uno_Model.GameStatus.IN_PROGRESS, game.getGameStatus());

        // Test scoring
        int handValue = game.calculateHandValue(player1);
        assertTrue(handValue >= 0, "Hand value non-negative");
        assertEquals(0, game.getLastRoundScore(player1), "Initial round score 0");
    }

    /**
     * Tests game state display and observation methods.
     * Verifies: All getter methods return valid data, game state string generation,
     * scores string generation, and visibility of game information.
     */
    @Test
    @DisplayName("Test game state display and observation")
    public void testStateDisplay() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        // Test all getters return valid data
        assertNotNull(game.getParticipants());
        assertNotNull(game.getDeck());
        assertNotNull(game.getActiveCard());
        assertNotNull(game.getInitialCard());
        assertNotNull(game.getMatchColour());
        assertNotNull(game.getMatchType());
        assertNotNull(game.getCurrentPlayer());

        // Test state strings
        String stateString = game.getGameStateString();
        assertNotNull(stateString);
        assertTrue(stateString.contains("UNO GAME STATE"));
        assertTrue(stateString.contains("Active Card"));

        String scoresString = game.getScoresString();
        assertNotNull(scoresString);
        assertTrue(scoresString.contains("SCORES"));
        assertTrue(scoresString.contains(player1.getName()));
    }

    /**
     * Integration test covering complete game flow with all features.
     * Verifies: Player management, initialization, drawing/passing turns,
     * direction changes, skip functionality, round management, and game reset.
     * This test ensures all components work correctly in combination.
     */
    @Test
    @DisplayName("Integration: Complete game flow with all features")
    public void testCompleteGameFlow() {
        // Player management (2-4 players)
        assertTrue(game.addPlayer(player1), "Add player 1");
        assertTrue(game.addPlayer(player2), "Add player 2");
        assertTrue(game.isValidPlayerCount(), "Valid player count");

        // Initialize game
        game.initializeGame();
        assertEquals(Uno_Model.GameStatus.IN_PROGRESS, game.getGameStatus());
        assertEquals(player1, game.getCurrentPlayer());

        // Draw card and pass turn
        int deckBefore = game.getRemainingDeckCards();
        game.drawCardAndPass();
        assertEquals(deckBefore - 1, game.getRemainingDeckCards());
        assertEquals(player2, game.getCurrentPlayer());

        // Test direction change
        game.reversePlayDirection();
        assertEquals(-1, game.getPlayDirection());
        game.reversePlayDirection();
        assertEquals(1, game.getPlayDirection());

        // Test skip
        int turnBefore = game.getCurrentTurnIndex();
        game.skipNextPlayer();
        assertNotEquals(turnBefore, game.getCurrentTurnIndex());

        // Test new round
        game.startNewRound();
        assertEquals(Uno_Model.GameStatus.IN_PROGRESS, game.getGameStatus());
        assertEquals(player1, game.getCurrentPlayer());
        assertEquals(1, game.getPlayDirection(), "Direction reset");

        // Test full reset
        game.resetGame();
        assertEquals(Uno_Model.GameStatus.NOT_STARTED, game.getGameStatus());
        assertEquals(2, game.getParticipants().size(), "Players remain");

    }
}
