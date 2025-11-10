import java.util.EventObject;

/**
 * Event object for UNO game state changes.
 * Encapsulates different types of game events (card plays, player changes, status updates).
 *
 * Data Structures:
 *   - Card_Model card: Single card reference for card-specific events.
 *       Used for:
 *         * Passing played card information to views
 *   - Player_Model player: Single player reference for player-specific events.
 *       Used for:
 *         * Identifying winner or current player in events
 *   - Uno_Model.GameStatus gameStatus: Enum representing current game state.
 *       Used for:
 *         * Communicating state transitions (NOT_STARTED, IN_PROGRESS, ROUND_ENDED, GAME_OVER)
 *
 * @author Lasya Erukulla
 * @version 2.0 Milestone 2
 */
public class Uno_Event extends EventObject {
    private Card_Model card;
    private Player_Model player;
    private Uno_Model.GameStatus gameStatus;

    public Uno_Event(Uno_Model source)
    {
        super(source);
    }

    public Uno_Event(Uno_Model source, Card_Model card){
        super(source);
        this.card=card;
    }

    public Uno_Event(Uno_Model source, Player_Model player){
        super(source);
        this.player=player;
    }

    public Uno_Event(Uno_Model source, Uno_Model.GameStatus gameStatus){
        super(source);
        this.gameStatus=gameStatus;
    }

    public Card_Model getCard()
    {
        return card;
    }

    public Player_Model getPlayer()
    {
        return player;
    }

    public Uno_Model.GameStatus getGameStatus()
    {
        return gameStatus;
    }
}