/**
 * Interface for handling UNO view.
 * Defines the view components that respond to game state changes.
 * 
 * @author Lucas Baker
 * @version 2.0 - Milestone 2
 */
public interface UnoViewHandler {

    /**
     * Handles game state update events.
     * Called when the model state changes and view needs to refresh.
     * 
     * @param event the UnoGameEvent containing update information
     */
    void handleGameUpdate(UnoGameEvent event);

    /**
     * Handles round end events.
     * Called when a round ends with a winner.
     * 
     * @param event the UnoGameEvent containing round end information
     */
    void handleRoundEnd(UnoGameEvent event);

    /**
     * Handles game over events.
     * Called when the game is completely over with a final winner.
     * 
     * @param event the UnoGameEvent containing game over information
     */
    void handleGameOver(UnoGameEvent event);
}