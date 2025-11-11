import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the Controller for UNO game
 * This hanles user inputs, updates the model and notifies view of the changes made
 *
 * Data Structures:
 *     - List<Uno_ViewHandler> handlers: ArrayList storing view observers
 *         * Dynamic number of views that can register recevie game updates
 *         * Sequential iteration when notifyin views of game events
 *         * efficeint additon of new view handlers
 *
 * @author Lasya Erukulla
 * @version 2.0 Milestone 2
 */
public class Uno_Controller implements ActionListener {
    private Uno_Model uno;
    private List<Uno_ViewHandler> handlers;

    /* Constructor */
    public Uno_Controller(Uno_Model uno) {
        this.uno = uno;
        handlers = new ArrayList<>();
    }

    /* Add View handlers to the handlers list
     *
     * @param handler: Uno_ViewHandler to be added to the handler list
     */
    public void addViewHandler(Uno_ViewHandler handler) {
        handlers.add(handler);
    }

    /**
     * Notify all view handlers of an update in the game
     */
    public void notifyGameUpdate() {
        Uno_Event event = new Uno_Event(uno, uno.getGameStatus() );
        for (Uno_ViewHandler handler: handlers) {
            handler.handleGameUpdate(event);
        }
    }

    /**
     * Notify all view handlers of when a round is over
     */
    public void notifyRoundOver () {
        Uno_Event event = new Uno_Event(uno, uno.getGameStatus() );
        for (Uno_ViewHandler handler: handlers) {
            handler.handleRoundEnd(event);
        }
    }

    /**
     * Notify all view handlers of when the game is over
     */
    public void notifyGameOver () {
        Uno_Event event = new Uno_Event(uno, uno.getGameStatus() );
        for (Uno_ViewHandler handler: handlers) {
            handler.handleGameOver(event);
        }
    }

    /**
     * Intialize the UNO game by setting up the model
     */
    public void initializeGame(){
        uno.initializeGame();
        notifyGameUpdate();
    }

    /**
     * Start a new round in the UNO game
     */
    public void startNewRound() {
        uno.startNewRound();
        notifyGameUpdate();
    }

    /**
     * Reset the UNO game to intial state
     */
    public void resetGame() {
        uno.resetGame();
        notifyGameUpdate();
    }

    /**
     * Handle user actions and invoke the corresponding handler methods
     *
     * @ param e: ActionEvent user action event
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch(command){
            case "drawCard":
                handleDrawCard();
                break;
            case "nextPlayer":
                handleNextPlayer();
                break;
            default:
                break;
        }
    }

    /**
     * Create players for the UNO game and add them to the UNO model
     *
     * @param numPlayers: int the number of players to be created
     */
    public void createPlayers(int numPlayers) {
        for (int i = 0; i <numPlayers; i++) {
            Player_Model player = new Player_Model("Player" + i);
            uno.addPlayer(player);
        }
    }

    /**
     * Handle the draw a card action by the current player
     */

    public void handleDrawCard() {
        if (uno.getGameStatus() != Uno_Model.GameStatus.IN_PROGRESS) {
            return;
        }
        if (isPendingColourSelection()){
            return;
        }
        uno.drawCard();
        notifyGameUpdate();
    }

    /**
     * Handle advancing to the next player's turn
     */

    public void handleNextPlayer() {
        if (uno.getGameStatus() != Uno_Model.GameStatus.IN_PROGRESS) {
            return;
        }
        if (isPendingColourSelection()){
            return;
        }
        uno.advanceToNextTurn();
        notifyGameUpdate();
    }

    /**
     * Handle the play a action by the current player
     *
     * @param cardIndex: int index of the card to be played from the player's hand
     * @return boolean indicating if the card play was successful
     */
    public boolean playCard(int cardIndex){
        if (uno.getGameStatus() != Uno_Model.GameStatus.IN_PROGRESS) {
            return false;
        }
        if (isPendingColourSelection()){
            return false;
        }

        Uno_Model.TurnAction result = uno.playCard(cardIndex);
        if(result == Uno_Model.TurnAction.CARD_PLAYED){
            notifyGameUpdate();
            if(isGameOver()){
                notifyGameOver();
            } else if (isRoundOver()){
                notifyRoundOver();
            }
            return true;
        }
        return false;
    }

    /**
     * Handle setting the wild card colour after a wild card is played
     *
     * @param colour: Card_Model.CardColour teh colour to be set for the wild card
     * @return boolean indicating if the colour selection was successful
     */
    public boolean setWildCardColour(Card_Model.CardColour colour){
        if (!isPendingColourSelection()){
            return false;
        }
        boolean result = uno.setActiveColour(colour);
        if(result){
            notifyGameUpdate();
            if(isGameOver()){
                notifyGameOver();
            } else if (isRoundOver()){
                notifyRoundOver();
            }
            return true;
        }
        return false;
    }

    /* Getters for different game attributes */
    /**
     * Get the current player in the UNO game
     * @return Player_Model the current player object
     */
    public Player_Model getCurrentPlayer() {
        return uno.getCurrentPlayer();
    }

    /**
     * Get the winner in the UNO game
     * @return Player_Model the winner player object
     */
    public Player_Model getWinner() {
        return uno.getWinner();
    }

    /**
     * Get the current participants in the UNO game
     * @return List<Player_Model> the current participants in the game
     */
    public List<Player_Model> getParticipants() {
        return uno.getParticipants();
    }

    /**
     * Get the active card in the UNO game
     * @return Card_Model the active card object
     */
    public Card_Model getActiveCard() {
        return uno.getActiveCard();
    }

    /**
     * Get the match colour in the UNO game
     * @return Card_Model.CardColour the current match colour
     */
    public Card_Model.CardColour getMatchColour() {
        return uno.getMatchColour();
    }

    /**
     * Get the number of remaining cards in the draw pile
     * @return int the number of remaining cards in the draw pile
     */

    public int getRemainingDrawPileCards() {
        return uno.getRemainingDrawPileCards();
    }

    /**
     * Check if the active card is an action card and return its effect
     * @return  Uno_Model.SpecialCardEffect the effect of the action card if applicable
     */
    public Uno_Model.SpecialCardEffect checkActionCard() {
        Uno_Model.SpecialCardEffect effect = uno.identifySpecialCard();
        return effect;
    }

    /**
     * Get the current game status
     * @return  Uno_Model.SpecialCardEffect the current game status
     */

    public  Uno_Model.GameStatus getGameStatus() {
        return uno.getGameStatus();
    }

    /**
     * Check if the game is pending a colour selection after a wild card play
     * @return boolean indicating if the game is pending a colour selection
     */
    public boolean isPendingColourSelection() {
        return uno.isPendingColourSelection();
    }

    /**
     * Check if the current round is over
     * @return boolean indicating if the round is over
     */

    public boolean isRoundOver(){
        return uno.isRoundEnded();
    }

    /**
     * Check if the game is over
     * @return boolean indicating if the game is over
     */
    public boolean isGameOver(){
        return uno.isGameOver();
    }

}
