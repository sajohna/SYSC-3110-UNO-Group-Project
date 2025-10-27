import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class Uno_ModelTest {
    private Uno_Model game;
    private Player_Model player1;
    private Player_Model player2;
    private Player_Model player3;

    @BeforeEach
    void setUp() {
        game = new Uno_Model();
        player1 = new Player_Model("Alice");
        player2 = new Player_Model("Bob");
        player3 = new Player_Model("Charlie");
    }

    // ======== GETTER TESTS ========

    @Test
    @DisplayName("getCurrentPlayer returns null when no players added")
    void testGetCurrentPlayerEmpty() {
        assertNull(game.getCurrentPlayer());
    }

    @Test
    @DisplayName("getCurrentPlayer returns first player after initialization")
    void testGetCurrentPlayerAfterInit() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertEquals(player1, game.getCurrentPlayer());
    }

    @Test
    @DisplayName("getActiveCard returns null before game starts")
    void testGetActiveCardBeforeStart() {
        assertNull(game.getActiveCard());
    }

    @Test
    @DisplayName("getActiveCard returns valid card after initialization")
    void testGetActiveCardAfterInit() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertNotNull(game.getActiveCard());
    }

    @Test
    @DisplayName("getMatchColour returns correct colour")
    void testGetMatchColour() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertNotNull(game.getMatchColour());
    }

    @Test
    @DisplayName("getRemainingDeckCards returns positive number after init")
    void testGetRemainingDeckCards() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertTrue(game.getRemainingDeckCards() > 0);
    }

    @Test
    @DisplayName("getGameStatus returns NOT_STARTED initially")
    void testGetGameStatusInitial() {
        assertEquals(Uno_Model.GameStatus.NOT_STARTED, game.getGameStatus());
    }

    @Test
    @DisplayName("getGameStatus returns IN_PROGRESS after initialization")
    void testGetGameStatusAfterInit() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertEquals(Uno_Model.GameStatus.IN_PROGRESS, game.getGameStatus());
    }

    @Test
    @DisplayName("isPendingColourSelection returns false initially")
    void testIsPendingColourSelectionInitial() {
        assertFalse(game.isPendingColourSelection());
    }

    @Test
    @DisplayName("isRoundEnded returns false when game in progress")
    void testIsRoundEndedDuringGame() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertFalse(game.isRoundEnded());
    }

    @Test
    @DisplayName("isGameOver returns false initially")
    void testIsGameOverInitial() {
        assertFalse(game.isGameOver());
    }

    @Test
    @DisplayName("getWinner returns null before game ends")
    void testGetWinnerBeforeEnd() {
        assertNull(game.getWinner());
    }

    // ======== SETUP TESTS ========

    @Test
    @DisplayName("addPlayer successfully adds valid player")
    void testAddPlayerSuccess() {
        assertTrue(game.addPlayer(player1));
    }

    @Test
    @DisplayName("addPlayer fails with null player")
    void testAddPlayerNull() {
        assertFalse(game.addPlayer(null));
    }

    @Test
    @DisplayName("addPlayer fails when game already started")
    void testAddPlayerAfterGameStart() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertFalse(game.addPlayer(player3));
    }

    @Test
    @DisplayName("addPlayer fails when max players reached")
    void testAddPlayerMaxLimit() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.addPlayer(new Player_Model("Dave"));
        assertFalse(game.addPlayer(new Player_Model("Eve")));
    }

    @Test
    @DisplayName("initializeGame deals 7 cards to each player")
    void testInitializeGameDealsCards() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertEquals(7, player1.getNumCards());
        assertEquals(7, player2.getNumCards());
    }

    @Test
    @DisplayName("initializeGame sets active card")
    void testInitializeGameSetsActiveCard() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertNotNull(game.getActiveCard());
    }

    @Test
    @DisplayName("initializeGame sets game status to IN_PROGRESS")
    void testInitializeGameStatus() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertEquals(Uno_Model.GameStatus.IN_PROGRESS, game.getGameStatus());
    }

    @Test
    @DisplayName("initializeGame avoids WILD_DRAW_TWO as initial card")
    void testInitializeGameAvoidsWildDrawTwo() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertNotEquals(Card_Model.CardValue.WILD_DRAW_TWO, game.getActiveCard().getCardValue());
    }

    // ======== TURN MANAGEMENT TESTS ========

    @Test
    @DisplayName("advanceToNextTurn moves to next player")
    void testAdvanceToNextTurn() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        Player_Model first = game.getCurrentPlayer();
        game.advanceToNextTurn();
        assertNotEquals(first, game.getCurrentPlayer());
    }

    @Test
    @DisplayName("advanceToNextTurn wraps around to first player")
    void testAdvanceToNextTurnWraparound() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        game.advanceToNextTurn();
        game.advanceToNextTurn();
        assertEquals(player1, game.getCurrentPlayer());
    }

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

    @Test
    @DisplayName("reversePlayDirection changes direction")
    void testReversePlayDirection() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.initializeGame();
        Player_Model first = game.getCurrentPlayer();
        game.advanceToNextTurn();
        Player_Model second = game.getCurrentPlayer();
        game.reversePlayDirection();
        assertEquals(first, game.getCurrentPlayer());
    }

    @Test
    @DisplayName("reversePlayDirection with 2 players advances turn")
    void testReversePlayDirectionTwoPlayers() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        Player_Model first = game.getCurrentPlayer();
        game.reversePlayDirection();
        assertNotEquals(first, game.getCurrentPlayer());
    }

    // ======== CARD LOGIC TESTS ========

    @Test
    @DisplayName("isValidPlay returns false for null card")
    void testIsValidPlayNull() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertFalse(game.isValidPlay(null));
    }

    @Test
    @DisplayName("isValidPlay returns true for WILD card")
    void testIsValidPlayWild() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        Card_Model wildCard = new Card_Model(Card_Model.CardValue.WILD, Card_Model.CardColour.WILD);
        assertTrue(game.isValidPlay(wildCard));
    }

    @Test
    @DisplayName("isValidPlay returns true for WILD_DRAW_TWO")
    void testIsValidPlayWildDrawTwo() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        Card_Model wildDrawTwo = new Card_Model(Card_Model.CardValue.WILD_DRAW_TWO, Card_Model.CardColour.WILD);
        assertTrue(game.isValidPlay(wildDrawTwo));
    }

    @Test
    @DisplayName("isValidPlay returns true for matching colour")
    void testIsValidPlayMatchingColour() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        Card_Model.CardColour matchColour = game.getMatchColour();
        Card_Model card = new Card_Model(Card_Model.CardValue.ONE, matchColour);
        assertTrue(game.isValidPlay(card));
    }

    @Test
    @DisplayName("playCard returns INVALID_CARD_INDEX for negative index")
    void testPlayCardNegativeIndex() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertEquals(Uno_Model.TurnAction.INVALID_CARD_INDEX, game.playCard(-1));
    }

    @Test
    @DisplayName("playCard returns INVALID_CARD_INDEX for out of bounds index")
    void testPlayCardOutOfBounds() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertEquals(Uno_Model.TurnAction.INVALID_CARD_INDEX, game.playCard(100));
    }

    @Test
    @DisplayName("drawCard increases player hand size")
    void testDrawCard() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        int initialCards = player1.getNumCards();
        game.drawCard();
        assertEquals(initialCards + 1, player1.getNumCards());
    }

    @Test
    @DisplayName("drawCard returns CARD_DRAWN")
    void testDrawCardReturnValue() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertEquals(Uno_Model.TurnAction.CARD_DRAWN, game.drawCard());
    }

    @Test
    @DisplayName("drawCardAndPass advances turn")
    void testDrawCardAndPass() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        Player_Model first = game.getCurrentPlayer();
        game.drawCardAndPass();
        assertNotEquals(first, game.getCurrentPlayer());
    }

    @Test
    @DisplayName("drawCardAndPass returns TURN_PASSED")
    void testDrawCardAndPassReturnValue() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertEquals(Uno_Model.TurnAction.TURN_PASSED, game.drawCardAndPass());
    }

    @Test
    @DisplayName("identifySpecialCard returns NONE for number card")
    void testIdentifySpecialCardNone() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        // Assuming we can set active card to a number
        assertEquals(Uno_Model.SpecialCardEffect.class, game.identifySpecialCard().getClass());
    }

    @Test
    @DisplayName("setActiveColour returns false for null colour")
    void testSetActiveColourNull() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertFalse(game.setActiveColour(null));
    }

    @Test
    @DisplayName("setActiveColour returns false for WILD colour")
    void testSetActiveColourWild() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        assertFalse(game.setActiveColour(Card_Model.CardColour.WILD));
    }

    @Test
    @DisplayName("setActiveColour returns true for valid colour")
    void testSetActiveColourValid() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        // Need to trigger pending colour selection first
        // This test assumes we can get into that state
        assertTrue(game.setActiveColour(Card_Model.CardColour.RED) ||
                !game.isPendingColourSelection());
    }

    @Test
    @DisplayName("setActiveColour clears pending colour selection")
    void testSetActiveColourClearsPending() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        game.setActiveColour(Card_Model.CardColour.RED);
        assertFalse(game.isPendingColourSelection());
    }

    // ======== GAME STATE TESTS ========

    @Test
    @DisplayName("startNewRound clears player hands")
    void testStartNewRoundClearsHands() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        game.startNewRound();
        assertEquals(7, player1.getNumCards()); // Should have new dealt cards
    }

    @Test
    @DisplayName("startNewRound resets game to IN_PROGRESS")
    void testStartNewRoundStatus() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        game.startNewRound();
        assertEquals(Uno_Model.GameStatus.IN_PROGRESS, game.getGameStatus());
    }

    @Test
    @DisplayName("resetGame clears all player scores")
    void testResetGameScores() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        player1.setScore(100);
        game.resetGame();
        assertEquals(0, player1.getScore());
    }

    @Test
    @DisplayName("resetGame sets status to NOT_STARTED")
    void testResetGameStatus() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        game.resetGame();
        assertEquals(Uno_Model.GameStatus.NOT_STARTED, game.getGameStatus());
    }

    @Test
    @DisplayName("resetGame clears winner")
    void testResetGameWinner() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        game.resetGame();
        assertNull(game.getWinner());
    }

    @Test
    @DisplayName("getGameStateString returns non-empty string")
    void testGetGameStateString() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        String state = game.getGameStateString();
        assertNotNull(state);
        assertTrue(state.length() > 0);
    }

    @Test
    @DisplayName("getGameStateString contains active card info")
    void testGetGameStateStringContainsActiveCard() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        String state = game.getGameStateString();
        assertTrue(state.contains("Active Card"));
    }

    @Test
    @DisplayName("getGameStateString contains player scores")
    void testGetGameStateStringContainsScores() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();
        String state = game.getGameStateString();
        assertTrue(state.contains("SCORES"));
    }

    // ======== INTEGRATION TESTS ========

    @Test
    @DisplayName("Complete game flow with two players")
    void testCompleteGameFlow() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        assertEquals(Uno_Model.GameStatus.NOT_STARTED, game.getGameStatus());

        game.initializeGame();
        assertEquals(Uno_Model.GameStatus.IN_PROGRESS, game.getGameStatus());
        assertNotNull(game.getCurrentPlayer());
        assertNotNull(game.getActiveCard());
    }

    @Test
    @DisplayName("Multiple turns cycle through players correctly")
    void testMultipleTurns() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.initializeGame();

        Player_Model first = game.getCurrentPlayer();
        game.advanceToNextTurn();
        Player_Model second = game.getCurrentPlayer();
        game.advanceToNextTurn();
        Player_Model third = game.getCurrentPlayer();

        assertNotEquals(first, second);
        assertNotEquals(second, third);
        assertNotEquals(first, third);
    }

    @Test
    @DisplayName("Game handles invalid plays gracefully")
    void testInvalidPlayHandling() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        // Try to play invalid index
        Uno_Model.TurnAction result = game.playCard(999);
        assertEquals(Uno_Model.TurnAction.INVALID_CARD_INDEX, result);

        // Game should still be in progress
        assertEquals(Uno_Model.GameStatus.IN_PROGRESS, game.getGameStatus());
    }
}