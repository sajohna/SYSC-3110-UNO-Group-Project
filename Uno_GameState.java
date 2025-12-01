import java.io.Serializable;
import java.util.*;

/**
 * This class represents the game state of the uno game
 * This handles saving the game state and restoring the game state
 * 
 * The data types used are the exact same as used in Uno_Model, Player_Model, Deck_Model
 *   - long serialVersionUID: Version identifier for serialization compatibility.
 *         * Ensures deserialized objects are compatible with current class definition
 *   - List of PlayerStateShots (participants): Stores players in turn order.
 *       ArrayList chosen for:
 *         * Efficient sequential iteration during initialization and scoring
 *         * Fixed size after game starts (max 4 players), so no dynamic resizing concerns
 *         * Natural ordering supports circular turn advancement with modulo arithmetic
 *         * Supports both human and AI players transparently
 *
 *   - int currentTurnIndex: Tracks current player position. Combined with ArrayList enables:
 *         * Simple modulo arithmetic for circular turn advancement
 *         * Bidirectional traversal via playDirection multiplier
 *
 *   - int playDirection: Multiplier (1 or -1) for turn advancement direction.
 *       Enables:
 *         * Elegant bidirectional turn traversal with single formula
 *
 *   - boolean isDarkSide: Tracks which side of cards is currently active.
 *       * Affects card colors (light: R/B/G/Y, dark: Teal/Purple/Pink/Orange)
 *       * Affects card scoring values
 *       * Toggled by FLIP card effect
 *
 *   - boolean pendingColourSelection: Tracks if waiting for wild card colour choice.
 *         * Set true when WILD or WILD_DRAW_TWO played
 *         * Blocks turn advancement until colour selected
 *   - boolean pendingDrawColourSelection: Tracks Wild Draw Colour state.
 *         * Dark side exclusive card effect
 *         * Next player draws until specified colour appears
 *   - ArrayList of Card_Model: Stores the cards in the draw pile
 *     ArrayList choosen for:
 *         * Dynamic sizing: can grow/shrink as cards are drawn from the draw pile
 *         * Easy access: allows efficient access to cards by index
 *         * Built-in methods: provides useful methods like shuffle to shuffle cards in the draw pile
 *   - ArrayList of Card_Model: Stores the cards in the discard pile
 *     ArrayList choosen for:
 *         * Dynamic sizing: can grow/shrink as cards are drawn from the discard pile
 *         * Easy access: allows efficient access to cards by index
 *         * Built-in methods: provides useful methods like shuffle to shuffle cards in the discard pile
 * @author Lasya Erukulla
 * @version 4.0 - Milestone 4
 */
public class Uno_GameState implements Serializable{
    private static final long serialVersionUID = 1L;

    //player state
    private final List<PlayerStateShots> playerStateShots;
    private final int currentTurnIndex;

    //game state
    private final Card_Model activeCard;
    private final Card_Model.CardColour matchColour;
    private final Card_Model.CardValue matchType;
    private final int playDirection;
    private final boolean isDarkSide;
    private final boolean isPendingColourSelection;
    private final boolean isPendingDrawColourSelection;

    //deck state
    private final List<Card_Model> drawPileCards;
    private final List<Card_Model> discardPileCards;



    /**
     * Insider class to capture player state (Multiple players' state needed to be saved)
     */
    public static class PlayerStateShots implements Serializable{
        private static final long serialVersionUID = 1L;
        public String name;
        public ArrayList<Card_Model> hand;
        public int score;
        public int numCards;
        public boolean isAI;
        public Player_Model.AIStrategy aiStrategy;
        public PlayerStateShots(Player_Model player){
            this.name = player.getName();
            this.hand = player.getHand();
            this.score = player.getScore();
            this.numCards = player.getNumCards();
            this.isAI =player.isAI();
            this.aiStrategy = player.getAIStrategy();
        }
    }

    /**
     * Constructore to save current game state
     * 
     * @param uno the uno model that needs to be saved
     */
    public Uno_GameState(Uno_Model uno){
        //capture player states
        this.playerStateShots = new ArrayList<>();
        for(Player_Model player : uno.getParticipants()){
            playerStateShots.add(new PlayerStateShots(player));
        }

        //capture turn state
        this.currentTurnIndex = uno.getCurrentTurnIndex();

        //capture game state
        this.activeCard = uno.getActiveCard();
        this.matchColour = uno.getMatchColour();
        this.matchType = uno.getMatchType();
        this.playDirection = uno.getPlayDirection();
        this.isDarkSide = uno.isDarkSide();
        this.isPendingColourSelection = uno.isPendingColourSelection();
        this.isPendingDrawColourSelection = uno.isPendingDrawColourSelection();

        //capture deck state
        this.drawPileCards = new ArrayList<>(uno.getDeck().getCards());
        this.discardPileCards =new ArrayList<>(uno.getDeck().getDiscardPile());
    }

    /**
     * Restore the saved model into a new empty one
     * 
     * @param uno a new empty uno model that needs to be filled
     */
    public void restoreToModel(Uno_Model uno){
        List<Player_Model> participants = uno.getParticipants();
        for(int i = 0; i < playerStateShots.size(); i++){
            PlayerStateShots playerState = playerStateShots.get(i);
            Player_Model participant = participants.get(i);
            participant.getHand().clear();
            for(Card_Model card: playerState.hand){
                participant.addCard(card);
            }
            participant.setScore(playerState.score);
        }

        //restore game state
        uno.setCurrentTurnIndex(currentTurnIndex);
        uno.setActiveCard(activeCard);
        uno.setMatchColour(matchColour);
        uno.setMatchType(matchType);
        uno.setPlayDirection(playDirection);
        uno.setIsDarkSide(isDarkSide);
        uno.setPendingColourSelection(isPendingColourSelection);
        uno.setPendingDrawColourSelection(isPendingDrawColourSelection);

        //restore deck state
        uno.getDeck().getCards().clear();
        uno.getDeck().getCards().addAll(drawPileCards);
        uno.getDeck().getDiscardPile().clear();
        uno.getDeck().getDiscardPile().addAll(discardPileCards);
    }
}