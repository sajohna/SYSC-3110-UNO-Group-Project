import java.util.EventObject;
/**
 * This class represents the Event object for UNO game
 * This caputres key game events to be communicated throughout the model (cards played, player changes, game status updates)
 *
 * Data Structures:
 *     - Card_Model card: Represents the card invovled for card play events
 *         - This is used to capture card specific details and pass them to the view model
 *     - Player_model player: Rrepresents the player involved in the event
 *         - This is used to identify which player is involved in the event and pass player specific details to the view model
 *     - Uno_Model.gameStatus gameStatus: Enum representing current game status
 *         - This is used to communicate overall game state changes to the view model
 *
 * @author Lasya Erukulla
 * @version 2.0 Milestone 2
 */
public class Uno_Event extends EventObject {
    private Card_Model card;
    private Player_Model player;
    private Uno_Model.GameStatus gameStatus;

    /**
     * Constructor for the Uno_Event class
     * @param model: Uno_Model the instance of the UNO game model
     */
    public Uno_Event (Uno_Model model){
        super(model);
    }

    /**
     * Constructor for the Uno_Event class
     * @param model: Uno_Model the instance of the UNO game model
     * @param card: Card_Model the card involve in the event
     */
    public Uno_Event (Uno_Model model, Card_Model card){
        super(model);
        this.card = card;
    }

    /**
     * Constructor for the Uno_Event class
     * @param model: Uno_Model the instance of the UNO game model
     * @param gameStatus: Uno_Model.GameStatus the game status involve in the event
     */
    public Uno_Event (Uno_Model model, Uno_Model.GameStatus gameStatus){
        super(model);
        this.gameStatus = gameStatus;
    }

    /**
     * Get the card involve with this event
     * @return Card_Model the card object involved in the event
     */
    public Card_Model getCard(){
        return card;
    }

    /**
     * Get the player involve with this event
     * @return Player_Model the player object involved in the event
     */
    public Player_Model getPlayer() {
        return player;
    }

    /**
     * Get the game status involve with this event
     * @return Uno_Model.GameStatus the game status object involved in the event
     */
    public Uno_Model.GameStatus getGameStatus() {
        return gameStatus;
    }
}