import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * This class represents the Controller for UNO game
 * This hanles user inputs, updates the model and notifies view of the changes made
 *
 * Data Structures:
 *     - List<Uno_ViewHandler> handlers: ArrayList storing view observers
 *         * Dynamic number of views that can register recevie game updates
 *         * Sequential iteration when notifyin views of game events
 *         * efficeint additon of new view handlers
 *     - Stack<Uno_GameState> stackUNDO: Stack storing the game states of the uno game
 *        * Order objects as Last in first out which is needed for UNDOing the last game state
 *        * efficeint addition of new game states
 *        * easy access of to most recent game states
 *     - Stack<Uno_GameState> stackREDO: Stack storing the game states of the uno game
 *        * Order objects as Last in first out which is needed for UNDOing the last game state
 *        * efficeint addition of new game states
 *        * easy access of to most recent game states
 *
 * @author Lasya Erukulla
 * @version 4.0 - Milestone 4 + 5
 */
public class Uno_Controller implements ActionListener {
    private Uno_Model uno;
    private List<Uno_ViewHandler> handlers;
    private Stack<Uno_GameState> stackUNDO;
    private Stack<Uno_GameState> stackREDO;
    private static final int MAX_UNO_NUM = 50;

    /* Constructor */
    public Uno_Controller(Uno_Model uno) {
        this.uno = uno;
        handlers = new ArrayList<>();
        this.stackUNDO = new Stack<>();
        this.stackREDO = new Stack<>();
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
        clearUndoRedoHistory();
        notifyGameUpdate();
    }

    /**
     * Start a new round in the UNO game
     */
    public void startNewRound() {
        uno.startNewRound();
        clearUndoRedoHistory();
        notifyGameUpdate();
    }

    /**
     * Reset the UNO game to intial state
     */
    public void resetGame() {
        uno.resetGame();
        clearUndoRedoHistory();
        notifyGameUpdate();
    }

    /**
     * Save a game to a file
     * @param gameName the file to save the game to
     * @return true if save was successful
     */
    public boolean saveGame(String gameName){
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(gameName))){
            out.writeObject(uno);
            return true;
        }catch (IOException e){
            System.err.println("Error saving game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load a game from a file
     * 
     * @param gameName the game fiel to load from
     * @return true if load was successful
     */
    public boolean loadGame(String gameName){
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(gameName))){
            Uno_Model newModelLoaded = (Uno_Model) in.readObject();
            this.uno = newModelLoaded;
            clearUndoRedoHistory();
            notifyGameUpdate();
            return true;
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
     * Create players for the UNO game and add them to the UNO model
     *
     * @param numPlayers: int the number of players to be created
     * @param numAIPlayers: int the number of AI players to be created
     */
    public void createPlayers(int numPlayers, int numAIPlayers){
        for (int i = 0; i <numPlayers; i++) {
            Player_Model player = new Player_Model("Player" + i);
            uno.addPlayer(player);
        }
        for (int i = 0; i <numAIPlayers; i++) {
            Player_Model ai = new Player_Model("AI" + i, true, Player_Model.AIStrategy.STRATEGIC);
            uno.addPlayer(ai);
        }
    }

    /**
     * Create players for the UNO game and add them to the UNO model
     * 
     * @param isAIList: List<Boolean> lsit indicating if the player at the index is an AI or human
     * @param names: List<String> list of names for the player, if null or empty default names will be used
     */
    public void createPlayersWithConfig(List<Boolean> isAIList, List<String> names){
        for (int i = 0; i < isAIList.size(); i++){
            String name = (names != null && !names.isEmpty()) ? names.get(i) : "Player" + i;
            boolean isAI = isAIList.get(i);
            Player_Model player = new Player_Model(name, isAI, isAI ? Player_Model.AIStrategy.STRATEGIC : Player_Model.AIStrategy.FIRST_VALID);
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
        if (isPendingColourSelection() || isPendingDrawColourSelection()){
            return;
        }
        saveGameStateForUndo();
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
        if (isPendingColourSelection() || isPendingDrawColourSelection()){
            return;
        }
        saveGameStateForUndo();
        uno.advanceToNextTurn();
        notifyGameUpdate();
    }

    /**
     * Handle the turn timeout
     * @return true if time expired
     */
    public boolean handleTurnTimeout(){
        if (uno.getGameStatus() != Uno_Model.GameStatus.IN_PROGRESS) {
            return false;
        }
        Uno_Model.TurnAction res = uno.handleTurnTimeout();
        if(res == Uno_Model.TurnAction.TIME_EXPIRED){
            notifyGameUpdate();
            return true;
        }
        return false;
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
        if (isPendingColourSelection() || isPendingDrawColourSelection()){
            return false;
        }
        saveGameStateForUndo();
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
        if (!isPendingColourSelection() && !isPendingDrawColourSelection()){
            return false;
        }
        saveGameStateForUndo();
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
    /* Methods for AI */
    /**
     * Check if the current player is an AI
     */
    public boolean isPlayerAI(){
        return uno.isCurrentPlayerAI();
    }

    /**
     * Proces the AI player's turn
     * 
     * @return boolean indicating if the AI turn was processed
     */
    public boolean processAITurn(){
        if(!isPlayerAI()) return false;
        if (uno.getGameStatus() != Uno_Model.GameStatus.IN_PROGRESS) {
            return false;
        }

        if (isPendingColourSelection() || isPendingDrawColourSelection()){
            Card_Model.CardColour chosenColour = uno.getAIColourSelection();
            if (chosenColour != null)  return setWildCardColour(chosenColour);
            return false;
        }

        int cardIndex = uno.getAICardSelection();
        if (cardIndex >= 0) {
            boolean played = playCard(cardIndex);
            if (played && (isPendingColourSelection() || isPendingDrawColourSelection())) {
                Card_Model.CardColour colour = uno.getAIColourSelection();
                if (colour != null) {
                    setWildCardColour(colour);
                }
            }
            return played;
        } else {
            handleDrawCard();
            handleNextPlayer();
            return true;
        }

    }
    
    /**
     * Process AI turns until it is a human player's turn
     */
    public void processAITurnsUntilHuman() {
        while (isPlayerAI() && 
               uno.getGameStatus() == Uno_Model.GameStatus.IN_PROGRESS) {
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            processAITurn();
        }
    }

    /**
     * Save the current game state before UNDO
     */
    public void saveGameStateForUndo(){
        if (uno.getGameStatus() != Uno_Model.GameStatus.IN_PROGRESS) {
            return;
        }
        Uno_GameState currentState = new Uno_GameState(uno);
        stackUNDO.push(currentState);
        if(stackUNDO.size() > MAX_UNO_NUM){
            stackUNDO.remove(0);
        }
        stackREDO.clear();
    }
    
    /**
     * Undo the last action(state)
     * @return true if undo is successful
     */
    public boolean undoGameState() {
        if(canUndo()){
            Uno_GameState currentState = new Uno_GameState(uno);
            stackREDO.push(currentState);

            Uno_GameState prevState = stackUNDO.pop();
            prevState.restoreToModel(uno);

            notifyGameUpdate();
            return true;
        }
        return false;        
    }

    /**
     * Redo the last action(state)
     * @return true if redo is successful
     */
    public boolean redoGameState() {
        if (canRedo()){
            Uno_GameState currentState = new Uno_GameState(uno);
            stackUNDO.push(currentState);

            Uno_GameState nextState = stackREDO.pop();
            nextState.restoreToModel(uno);

            notifyGameUpdate();
            return true;
        }
        return false;
    }

    /**
     * Clears the undo and redo stack
     */
    public void clearUndoRedoHistory(){
        stackREDO.clear();
        stackUNDO.clear();
    }

    /* Getters for different game attributes */
    /**
     * Gets the uno model
     */
    public Uno_Model getUnoModel(){
        return uno;
    }
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
     * Check if the game is pending a colour selection after a wild card play
     * @return boolean indicating if the game is pending a colour selection
     */
    public boolean isPendingDrawColourSelection() {
        return uno.isPendingDrawColourSelection();
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
    /**
     * Check if the deck is on the dark side
     * @return boolean indicating if the deck is on the dark side
     */
    public boolean isDarkSide(){
        return uno.isDarkSide();
    }

    /**
     * Check if undo is allowed
     * @return true if undo is allowed
     * */
    public boolean canUndo(){
        return !stackUNDO.isEmpty() && 
            uno.getGameStatus() == Uno_Model.GameStatus.IN_PROGRESS;
    }

    /**
     * Check if redo is allowed
     * @return true if redo is allowed
     * */
    public boolean canRedo(){
        return !stackREDO.isEmpty() && 
            uno.getGameStatus() == Uno_Model.GameStatus.IN_PROGRESS;
    }

    /**Timed mode methods**/
    /**
     * Set time mode to enabled or disabled
     * 
     * @param enabled boolean true if enable timed mode
     * @return true if setting was changed successfully
     * */
    public boolean setTimeModeEnabled(boolean enabled) {
        return uno.setTimedModeEnabled(enabled);
    }

    /**
     * Check if the time mode is enabled
     * 
     * @return true is timed mode is enabled
     * */
    public boolean isTimeModeEnabled() {
        return uno.isTimedModeEnabled();
    }

    /**
     * Set the turn time limit in seconds
     * 
     * @param secs the time limit to set
     * @return true if setting was changed successfully
     */
    public boolean setTurnTimeLimit(int secs){
        return uno.setTurnTimeLimit(secs);
    }

    /**
    * Get the turn time limit in seconds
    * 
    * @return the turn time limit
    * */
    public int getTurnTimeLimit(){
        return uno.getTurnTimeLimit();
    }

    /**
    * Get remaining turn time for the current turn
    * 
    * @return remaining time in seconds or -1 if time mode is disabled
    * */
    public int getRemainingTurnTime(){
        return uno.getRemainingTurnTime();
    }

    /**
    * Check if the turn time is expired
    * 
    * @return true if the the turn time is expired
    * */
    public boolean isTurnTimeExpired(){
        return uno.isTurnTimeExpired();
    }

}
