import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Add class comments later
 * 
 * @author Lasya Erukulla
 * @version 2.0 Milestone 2
 */
public class Uno_Controller implements ActionListener {
    private Uno_Model uno;
    private List<UnoViewHandler> handlers;

    public Uno_Controller(Uno_Model uno) {
        this.uno = uno;
        handlers = new ArrayList<>();
    }

    /** 
     * Notify view handlers about updates in the game
     */
    public void addViewHandler(UnoViewHandler handler) {
        handlers.add(handler);
    }

    /** Game controls */
    public void initializeGame(){
        uno.initializeGame();
    }

    public void startNewRound(){
        uno.startNewRound();
        notifyGameUpdate();
    }

    public void resetGame(){
        uno.resetGame();
        notifyGameUpdate();
    }

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

    public void createPlayers(int numPlayers){
        for (int i = 0; i < numPlayers; i++) {
            Player_Model player = new Player_Model("Player " + i));
            uno.addPlayer(player);
        }
    }

    public boolean playCard(int cardIndex) {
        if (uno.getGameStatus() != Uno_Model.GameStatus.IN_PROGRESS) {
            return false;
        }
        
        if (uno.isPendingColourSelection()) {
            return false;
        }

        Uno_Model.TurnAction result = uno.playCard(cardIndex);
        if (result == Uno_Model.TurnAction.CARD_PLAYED) {
            notifyGameUpdate();
            if (uno.isGameOver()) {
                notifyGameOver();
            } else if (uno.isRoundEnded()) {
                notifyRoundEnd();
            }
            return true;
        }
        return false;
    }

    public void handleDrawCard() {
        if (uno.getGameStatus() != Uno_Model.GameStatus.IN_PROGRESS){
            return;
        }

        if (uno.isPendingColourSelection()) {
            return;
        }

        uno.drawCard();
        notifyGameUpdate();
    }

    public void handleNextPlayer() {
        if (uno.getGameStatus() != Uno_Model.GameStatus.IN_PROGRESS){
            return;
        }

        if (uno.isPendingColourSelection()) {
            return;
        }

        uno.advanceToNextTurn();
        notifyGameUpdate();
    }


    public boolean playWildCard(Card_Model.CardColour colour){
        if (!uno.isPendingColourSelection()) {
            return false;
        }

        boolean success = uno.setWildCardColour(colour);
        if (success){
            uno.setActiveColour(colour);
            notifyGameUpdate();
            if (uno.isGameOver()) {
                notifyGameOver();
            } else if (uno.isRoundEnded()) {
                notifyRoundEnd();
            }
            return true;
        }
        return false;

    }

    /** Notify Updates */
    public void notifyGameUpdate() {
        Uno_Event event = new Uno_Event(uno, uno.getGameStatus());
        for (UnoViewHandler handler : handlers) {
            handler.onGameUpdate(event);
        }
    }

    public void notifyGameOver() {
        Uno_Event event = new Uno_Event(uno, uno.getGameStatus());
        for (UnoViewHandler handler : handlers) {
            handler.onGameOver(event);
        }
    }

    public void notifyRoundOver() {
        Uno_Event event = new Uno_Event(uno, uno.getGameStatus());
        for (UnoViewHandler handler : handlers) {
            handler.onRoundOver(event);
        }
    }

    /** Getters and Setters */
    public Player_Model getCurrentPlayer(){
        return uno.getCurrentPlayer();
    }

    public Card_Model getActiveCard(){
        return uno.getActiveCard();
    }

    public Card_Model.CardColour getMatchColour(){
        return uno.getMatchColour();
    }

    public int getRemainingDeckCards(){
        return uno.getRemainingDeckCards();
    }

    public Uno_Model.GameStatus getGameStatus(){
        return uno.getGameStatus();
    }

    public Player_Model getWinner(){
        return uno.getWinner();
    }

    public boolean isPendingColourSelection() {
        return model.isPendingColourSelection();
    }
    
    public boolean isRoundEnded() {
        return model.isRoundEnded();
    }
    
    public boolean isGameOver() {
        return model.isGameOver();
    }

    public List<Player_Model> getPlayers() {
        return model.getParticipants();
    }

    public Uno_Model.SpecialCardEffect checkActionCard(){
        Uno_Model.SpecialCardEffect effect = uno.identifySpecialCard();
        return effect;
    }
}
