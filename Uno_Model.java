import java.util.*;

/**
 * Model class representing the core game logic for UNO.
 * Manages game state, turn order, card plays, and scoring across multiple rounds.
 *
 * This class handles:
 *   - Player management (2-4 players)
 *   - Game initialization and round management
 *   - Turn advancement and direction control
 *   - Card validation and special card effects (SKIP, REVERSE, DRAW, WILD)
 *   - Scoring and win condition checking (first to 500 points)
 *
 * Game flow:
 *   1. Add 2-4 players using addPlayer(Player_Model)
 *   2. Initialize game with initializeGame()
 *   3. Players take turns playing cards or drawing
 *   4. Round ends when a player empties their hand
 *   5. Game continues until a player reaches 500 points
 *
 * Data Structure Design:
 *
 *   - List of Player_Model (participants): Stores players in turn order.
 *       ArrayList chosen for:
 *         * Efficient sequential iteration during initialization and scoring
 *         * Fixed size after game starts (max 4 players), so no dynamic resizing concerns
 *         * Natural ordering supports circular turn advancement with modulo arithmetic
 *
 *   - Deck_Model stack: Encapsulates the draw pile using internal stack/queue operations.
 *       Provides:
 *         * Automatic reshuffling when deck depletes
 *         * Abstraction of card management away from game logic
 *
 *   - Map of Player_Model to Integer (roundScores): Tracks points earned per player each round.
 *       HashMap chosen for:
 *         * Direct association between player objects and their round points
 *         * Easy clearing between rounds while maintaining player references
 *
 *   - int turnIdx: Tracks current player position. Combined with ArrayList enables:
 *         * Simple modulo arithmetic for circular turn advancement
 *         * Bidirectional traversal via playDirection multiplier
 *
 *   - int playDirection: Multiplier (1 or -1) for turn advancement direction.
 *       Enables:
 *         * Elegant bidirectional turn traversal with single formula
 *
 *   - boolean hasDrawnThisTurn: Tracks if current player has drawn during their turn.
 *         * Prevents multiple draws per turn
 *         * Simple boolean flag reset on turn change
 *
 * @author Saan John
 * @version 2.0 - Milestone 2
 */
public class Uno_Model {
    private static final int CARDS_PER_PLAYER = 7;
    private static final int MIN_PARTICIPANTS = 2;
    private static final int MAX_PARTICIPANTS = 4;
    private static final int TARGET_SCORE = 500;

    public enum GameStatus {
        NOT_STARTED,
        IN_PROGRESS,
        ROUND_ENDED,
        GAME_OVER
    }
    public enum TurnAction {
        CARD_PLAYED,
        CARD_DRAWN,
        TURN_PASSED,
        INVALID_PLAY,
        INVALID_CARD_INDEX
    }
    public enum SpecialCardEffect {
        NONE,
        REVERSE,
        SKIP,
        DRAW_ONE,
        WILD,
        WILD_DRAW_TWO
    }

    private List<Player_Model> participants;
    private Deck_Model stack;
    private Card_Model activeCard;
    private Card_Model initialCard;
    private Card_Model.CardValue matchType;
    private Card_Model.CardColour matchColour;
    private Player_Model victor;
    private int turnIdx;
    private GameStatus status;
    private int playDirection;
    private boolean pendingColourSelection;
    private Map<Player_Model, Integer> roundScores;

    /**
     * Constructs a new Uno_Model and initializes game state.
     * Sets up empty player list, new deck, and default game parameters.
     */
    public Uno_Model() {
        participants = new ArrayList<>();
        stack = new Deck_Model();
        turnIdx = 0;
        status = GameStatus.NOT_STARTED;
        playDirection = 1;
        pendingColourSelection = false;
        roundScores = new HashMap<>();
    }

    // ======== GETTERS ========

    /**
     * Returns the player whose turn it currently is.
     * @return the current Player_Model, or null if no players have been added
     */
    public Player_Model getCurrentPlayer() {
        return participants.isEmpty() ? null : participants.get(turnIdx);
    }

    /**
     * Returns the card currently at the top of the discard pile.
     * @return the active Card_Model, or null if game hasn't started
     */
    public Card_Model getActiveCard() {
        return activeCard;
    }

    /**
     * Returns the colour that must be matched for valid plays.
     * @return the current match colour
     */
    public Card_Model.CardColour getMatchColour() {
        return matchColour;
    }

    /**
     * Returns the number of cards remaining in the draw pile.
     * @return the count of drawable cards
     */
    public int getRemainingDrawPileCards() {
        return stack.getNumDrawCards();
    }

    /**
     * Returns the current status of the game.
     * @return the current GameStatus enum value
     */
    public GameStatus getGameStatus() {
        return status;
    }

    /**
     * Checks if a wild card colour selection is pending.
     * @return true if waiting for colour selection, false otherwise
     */
    public boolean isPendingColourSelection() {
        return pendingColourSelection;
    }

    /**
     * Checks if the current round has ended.
     * @return true if round is over or game is over, false otherwise
     */
    public boolean isRoundEnded() {
        return status == GameStatus.ROUND_ENDED || status == GameStatus.GAME_OVER;
    }

    /**
     * Checks if the entire game has concluded.
     * @return true if a player has reached the target score, false otherwise
     */
    public boolean isGameOver() {
        return status == GameStatus.GAME_OVER;
    }

    /**
     * Returns the winner of the game.
     * @return the winning Player_Model, or null if game is not over
     */
    public Player_Model getWinner() {
        return victor;
    }

    /**
     * Returns the list of participants.
     * ADDED FOR MILESTONE 2 - Needed for controller/view access
     * @return List of Player_Model participants
     */
    public List<Player_Model> getParticipants() {
        return participants;
    }

    // ======== SETUP ========

    /**
     * Adds a player to the game.
     * Can only be called before the game starts and when under max player limit.
     * @param player the Player_Model to add
     * @return true if player was successfully added, false otherwise
     */
    public boolean addPlayer(Player_Model player) {
        if (status != GameStatus.NOT_STARTED || player == null || participants.size() >= MAX_PARTICIPANTS)
            return false;
        participants.add(player);
        roundScores.put(player, 0);
        return true;
    }

    /**
     * Initializes the game by dealing cards, setting the initial active card,
     * and processing any special effects from the starting card.
     * Sets game status to IN_PROGRESS.
     */
    public void initializeGame() {
        distributeInitialCards();
        do { initialCard = stack.draw(); }
        while (initialCard.getCardValue() == Card_Model.CardValue.WILD_DRAW_TWO);

        activeCard = initialCard;
        matchColour = initialCard.getColour();
        matchType = initialCard.getCardValue();
        status = GameStatus.IN_PROGRESS;
        processInitialCardEffect();
    }

    /**
     * Deals the starting hand to each player.
     * Each player receives CARDS_PER_PLAYER cards from the deck.
     */
    private void distributeInitialCards() {
        for (int i = 0; i < CARDS_PER_PLAYER; i++)
            for (Player_Model p : participants)
                p.drawCard(stack);
    }

    /**
     * Processes any special effect of the initial card drawn at game start.
     * Handles REVERSE, SKIP, DRAW_ONE, and WILD card effects.
     */
    private void processInitialCardEffect() {
        SpecialCardEffect effect = identifySpecialCard();
        switch (effect) {
            case REVERSE -> { if (participants.size()==2) advanceToNextTurn(); else playDirection*=-1; }
            case SKIP -> advanceToNextTurn();
            case DRAW_ONE -> { forceCurrentPlayerDraw(1); advanceToNextTurn(); }
            case WILD -> matchColour = Card_Model.CardColour.values()[new Random().nextInt(4)];
            default -> {}
        }
    }

    // ======== TURN MANAGEMENT ========

    /**
     * Advances to the next player's turn based on current play direction.
     * Wraps around to the beginning of the player list when necessary.
     */
    public void advanceToNextTurn() {
        turnIdx = (turnIdx + playDirection + participants.size()) % participants.size();
    }

    /**
     * Skips the next player in turn order.
     * Effectively advances the turn twice.
     */
    public void skipNextPlayer() {
        advanceToNextTurn();
        advanceToNextTurn();
    }

    /**
     * Reverses the play direction (clockwise to counter-clockwise or vice versa).
     * In a 2-player game, this acts like a skip and advances to the next turn.
     */
    public void reversePlayDirection() {
        playDirection *= -1;
        if (participants.size()==2) advanceToNextTurn();
    }

    // ======== CARD LOGIC ========

    /**
     * Checks if a card can be legally played on the current active card.
     * Wild cards are always valid. Other cards must match colour or value.
     * @param card the Card_Model to validate
     * @return true if the card can be played, false otherwise
     */
    public boolean isValidPlay(Card_Model card) {
        if (card == null) return false;
        if (card.getCardValue() == Card_Model.CardValue.WILD || card.getCardValue() == Card_Model.CardValue.WILD_DRAW_TWO) return true;
        return card.getColour() == matchColour || card.getCardValue() == matchType;
    }

    /**
     * Attempts to play a card from the current player's hand.
     * Validates the card index and legality of the play.
     * Processes special card effects and checks for round end.
     * @param index the index of the card in the player's hand
     * @return TurnAction indicating the result of the play attempt
     */
    public TurnAction playCard(int index) {
        Player_Model player = getCurrentPlayer();
        if (index<0 || index>=player.getNumCards()) return TurnAction.INVALID_CARD_INDEX;
        Card_Model card = player.playCard(index);
        if (!isValidPlay(card)) return TurnAction.INVALID_PLAY;

        activeCard = card;
        matchType = card.getCardValue();
        if (card.getColour()!=Card_Model.CardColour.WILD) matchColour = card.getColour();
        player.removeCard(index);

        if (player.getNumCards()==0) { endRound(player); return TurnAction.CARD_PLAYED; }

        SpecialCardEffect effect = identifySpecialCard();
        if (effect==SpecialCardEffect.WILD || effect==SpecialCardEffect.WILD_DRAW_TWO) pendingColourSelection=true;
        else processSpecialCardEffect(effect);
        return TurnAction.CARD_PLAYED;
    }

    /**
     * Draws a card for the current player and passes their turn.
     * @return TurnAction.TURN_PASSED
     */
    public TurnAction drawCardAndPass() {
        getCurrentPlayer().drawCard(stack);
        advanceToNextTurn();
        return TurnAction.TURN_PASSED;
    }

    /**
     * Draws a single card for the current player without ending their turn.
     * @return TurnAction.CARD_DRAWN
     */
    public TurnAction drawCard() {
        getCurrentPlayer().drawCard(stack);
        return TurnAction.CARD_DRAWN;
    }

    /**
     * Processes the effect of a special card after it has been played.
     * Handles REVERSE, SKIP, and DRAW_ONE effects.
     * @param effect the SpecialCardEffect to process
     */
    private void processSpecialCardEffect(SpecialCardEffect effect) {
        switch(effect){
            case REVERSE -> reversePlayDirection();
            case SKIP -> skipNextPlayer();
            case DRAW_ONE -> { forceNextPlayerDraw(1); skipNextPlayer(); }
            default -> advanceToNextTurn();
        }
    }

    /**
     * Forces the current player to draw a specified number of cards.
     * @param n the number of cards to draw
     */
    private void forceCurrentPlayerDraw(int n) {
        for(int i=0;i<n;i++) getCurrentPlayer().drawCard(stack);
    }

    /**
     * Forces the next player in turn order to draw a specified number of cards.
     * @param n the number of cards to draw
     */
    private void forceNextPlayerDraw(int n){
        int nextIdx = (turnIdx+playDirection+participants.size())%participants.size();
        Player_Model next = participants.get(nextIdx);
        for(int i=0;i<n;i++) next.drawCard(stack);
    }

    /**
     * Identifies the special effect of the current active card.
     * @return the SpecialCardEffect enum corresponding to the active card
     */
    public SpecialCardEffect identifySpecialCard() {
        if(activeCard==null) return SpecialCardEffect.NONE;
        return switch(activeCard.getCardValue()){
            case REVERSE -> SpecialCardEffect.REVERSE;
            case SKIP -> SpecialCardEffect.SKIP;
            case DRAW_ONE -> SpecialCardEffect.DRAW_ONE;
            case WILD -> SpecialCardEffect.WILD;
            case WILD_DRAW_TWO -> SpecialCardEffect.WILD_DRAW_TWO;
            default -> SpecialCardEffect.NONE;
        };
    }

    /**
     * Sets the active colour after a wild card has been played.
     * Also processes WILD_DRAW_TWO penalty if applicable and advances the turn.
     * @param colour the CardColour to set as the new match colour
     * @return true if colour was successfully set, false if invalid colour provided
     */
    public boolean setActiveColour(Card_Model.CardColour colour){
        if(colour==null || colour==Card_Model.CardColour.WILD) return false;
        matchColour=colour; pendingColourSelection=false;
        SpecialCardEffect effect=identifySpecialCard();
        if(effect==SpecialCardEffect.WILD_DRAW_TWO){ forceNextPlayerDraw(2); skipNextPlayer(); }
        else advanceToNextTurn();
        return true;
    }

    /**
     * Ends the current round when a player empties their hand.
     * Calculates points, updates scores, and checks if game is over.
     * @param winner the Player_Model who emptied their hand
     */
    private void endRound(Player_Model winner){
        int points = participants.stream().filter(p->p!=winner).flatMap(p->p.getHand().stream()).mapToInt(c->c.getCardValue().cardScore).sum();
        winner.setScore(winner.getScore() + points);
        roundScores.put(winner, points);
        if(winner.getScore()>=TARGET_SCORE){ victor=winner; status=GameStatus.GAME_OVER; }
        else status=GameStatus.ROUND_ENDED;
    }

    /**
     * Starts a new round by clearing hands, resetting the deck, and re-initializing.
     * Preserves player scores from previous rounds.
     */
    public void startNewRound(){
        participants.forEach(p->p.getHand().clear());
        stack=new Deck_Model();
        turnIdx=0;
        playDirection=1;
        pendingColourSelection=false;
        activeCard=null;
        initialCard=null;
        roundScores.clear();
        status = GameStatus.IN_PROGRESS;
        initializeGame();
    }

    /**
     * Resets the entire game to initial state.
     * Clears all player hands and scores, resets deck, and sets status to NOT_STARTED.
     */
    public void resetGame(){
        participants.forEach(p->{p.resetScore(); p.getHand().clear();});
        participants.clear();
        stack=new Deck_Model();
        turnIdx=0;
        playDirection=1;
        pendingColourSelection=false;
        activeCard=null;
        initialCard=null;
        victor=null;
        status=GameStatus.NOT_STARTED;
        roundScores.clear();
    }

    /**
     * Generates a formatted string representation of the current game state.
     * Includes active card, match colour, deck size, direction, status, current player's hand, and all scores.
     * @return a formatted string containing comprehensive game state information
     */
    public String getGameStateString(){
        StringBuilder sb=new StringBuilder("\n=== UNO GAME STATE ===\n");
        sb.append("Active Card: ").append(activeCard).append("\n");
        sb.append("Match Colour: ").append(matchColour).append("\n");
        sb.append("Deck Cards: ").append(getRemainingDrawPileCards()).append("\n");
        sb.append("Direction: ").append(playDirection==1?"Forward":"Reverse").append("\n");
        sb.append("Status: ").append(status).append("\n");
        Player_Model current=getCurrentPlayer();
        if(current!=null){ sb.append("\n--- CURRENT PLAYER ---\n"); sb.append(current.getName()).append("'s Hand: ").append(current.getHand()).append("\n"); }
        sb.append("\n=== SCORES ===\n"); participants.forEach(p->sb.append(p.getName()).append(": ").append(p.getScore()).append("\n"));
        return sb.toString();
    }
}