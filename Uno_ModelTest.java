import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class Uno_ModelTest {
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

    @Test
    @DisplayName("getCurrentPlayer returns null when no players added")
    void testGetCurrentPlayerEmpty() {
        assertNull(game.getCurrentPlayer());
    }

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

    @Test
    @DisplayName("addPlayer validates correctly")
    void testAddPlayerValidation() {
        assertTrue(game.addPlayer(player1));
        assertFalse(game.addPlayer(null));

        game.addPlayer(player2);
        game.initializeGame();
        assertFalse(game.addPlayer(player3));
    }

    @Test
    @DisplayName("addPlayer enforces max player limit")
    void testAddPlayerMaxLimit() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.addPlayer(new Player_Model("Dave"));
        assertFalse(game.addPlayer(new Player_Model("Eve")));
    }

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

    @Test
    @DisplayName("playCard validates index bounds")
    void testPlayCardIndexValidation() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        assertEquals(Uno_Model.TurnAction.INVALID_CARD_INDEX, game.playCard(-1));
        assertEquals(Uno_Model.TurnAction.INVALID_CARD_INDEX, game.playCard(100));
    }

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

    @Test
    @DisplayName("setActiveColour validates colour input")
    void testSetActiveColour() {
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeGame();

        assertFalse(game.setActiveColour(null));
        assertFalse(game.setActiveColour(Card_Model.CardColour.WILD));
    }

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
}