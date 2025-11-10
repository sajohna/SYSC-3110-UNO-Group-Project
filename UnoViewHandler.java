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
     * @param event the Uno_Event containing update information
     */
    void handleGameUpdate(Uno_Event event);

    /**
     * Handles round end events.
     * Called when a round ends with a winner.
     *
     * @param event the Uno_Event containing round end information
     */
    void handleRoundEnd(Uno_Event event);

    /**
     * Handles game over events.
     * Called when the game is completely over with a final winner.
     *
     * @param event the Uno_Event containing game over information
     */
    void handleGameOver(Uno_Event event);
}