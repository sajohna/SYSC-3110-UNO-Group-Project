import java.util.*;

/**
 * Model class representing the core game logic for UNO Flip.
 * Manages game state, turn order, card plays, and scoring across multiple rounds.
 *
 * This class handles:
 *   - Player management (2-4 players, both human and AI)
 *   - Game initialization and round management
 *   - Turn advancement and direction control
 *   - Card validation and special card effects (SKIP, REVERSE, DRAW, WILD, FLIP)
 *   - Light/Dark side card flipping (UNO Flip mechanic)
 *   - Scoring and win condition checking (first to 500 points)
 *
 * Game flow:
 *   1. Add 2-4 players using addPlayer(Player_Model)
 *   2. Initialize game with initializeGame()
 *   3. Players take turns playing cards or drawing
 *   4. FLIP cards switch all cards between light and dark sides
 *   5. Round ends when a player empties their hand
 *   6. Game continues until a player reaches 500 points
 *
 * Data Structure Design:
 *
 *   - List of Player_Model (participants): Stores players in turn order.
 *       ArrayList chosen for:
 *         * Efficient sequential iteration during initialization and scoring
 *         * Fixed size after game starts (max 4 players), so no dynamic resizing concerns
 *         * Natural ordering supports circular turn advancement with modulo arithmetic
 *         * Supports both human and AI players transparently
 *
 *   - Deck_Model stack: Encapsulates the draw pile using internal stack/queue operations.
 *       Provides:
 *         * Automatic reshuffling when deck depletes
 *         * Abstraction of card management away from game logic
 *         * Support for flipping entire deck between light/dark sides
 *         * drawCardsUntilColour() for Wild Draw Colour effect
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
 *   - boolean isDarkSide: Tracks which side of cards is currently active.
 *         * Affects card colors (light: R/B/G/Y, dark: Teal/Purple/Pink/Orange)
 *         * Affects card scoring values
 *         * Toggled by FLIP card effect
 *
 *   - boolean pendingColourSelection: Tracks if waiting for wild card colour choice.
 *         * Set true when WILD or WILD_DRAW_TWO played
 *         * Blocks turn advancement until colour selected
 *
 *   - boolean pendingDrawColourSelection: Tracks Wild Draw Colour state.
 *         * Dark side exclusive card effect
 *         * Next player draws until specified colour appears
 *
 * @author Saan John
 * @version 3.0 - Milestone 3
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
        WILD_DRAW_TWO,
        FLIP,
        DRAW_FIVE,
        SKIP_EVERYONE,
        WILD_DRAW_COLOUR
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
    private boolean pendingDrawColourSelection;
    private boolean isDarkSide;

    /**
     * Constructs a new Uno_Model and initializes game state.
     * Sets up empty player list, new deck, and default game parameters.
     * Game starts on light side with forward play direction.
     */
    public Uno_Model() {
        participants = new ArrayList<>();
        stack = new Deck_Model();
        turnIdx = 0;
        status = GameStatus.NOT_STARTED;
        playDirection = 1;
        pendingColourSelection = false;
        roundScores = new HashMap<>();
        pendingDrawColourSelection = false;
        isDarkSide = false;
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
     * Returns the card match type that must be matched for valid plays.
     * @return the current match type
     */
    public Card_Model.CardValue getMatchType() {
        return matchType;
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
     * Checks if a Wild Draw Colour card colour selection is pending.
     * This is separate from regular wild selection as it triggers draw-until-colour effect.
     * @return true if waiting for Wild Draw Colour selection, false otherwise
     */
    public boolean isPendingDrawColourSelection() {
        return pendingDrawColourSelection;
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
     * Returns the list of participants. Needed for controller/view access.
     * @return List of Player_Model participants
     */
    public List<Player_Model> getParticipants() {
        return participants;
    }

    /**
     * Checks which side of cards is currently being played.
     * Light side uses Red/Blue/Green/Yellow colors.
     * Dark side uses Teal/Purple/Pink/Orange colors.
     * @return true if currently playing on dark side of cards, false for light side
     */
    public boolean isDarkSide() {
        return isDarkSide;
    }

    /**
     * Returns the deck model for the current game.
     * @return the Deck_Model containing draw and discard piles
     */
    public Deck_Model getDeck() {
        return stack;
    }


    // ======== SETUP ========

    /**
     * Adds a player to the game.
     * Can only be called before the game starts and when under max player limit.
     * Supports both human and AI players.
     * @param player the Player_Model to add (can be human or AI)
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
     * Wild Draw Two and Wild Draw Colour cannot be starting cards.
     * Sets game status to IN_PROGRESS.
     */
    public void initializeGame() {
        distributeInitialCards();
        do { initialCard = stack.draw(); }
        while (initialCard.getCardValue() == Card_Model.CardValue.WILD_DRAW_TWO ||
                initialCard.getCardValue() == Card_Model.CardValue.WILD_DRAW_COLOUR);

        activeCard = initialCard;
        matchColour = initialCard.getColour();
        matchType = initialCard.getCardValue();
        status = GameStatus.IN_PROGRESS;
        processInitialCardEffect();
    }

    /**
     * Deals the starting hand to each player.
     * Each player receives CARDS_PER_PLAYER cards from the deck.
     * Cards are dealt one at a time in rotation (simulating real dealing).
     */
    private void distributeInitialCards() {
        for (int i = 0; i < CARDS_PER_PLAYER; i++)
            for (Player_Model p : participants)
                p.drawCard(stack);
    }

    /**
     * Generates a random colour appropriate for the current side.
     * Used when AI plays wild or when wild is the starting card.
     * Light side: Red, Blue, Green, Yellow
     * Dark side: Teal, Purple, Pink, Orange
     * @return a random CardColour from the current side's colour set
     */
    private Card_Model.CardColour getRandomColour() {
        if (isDarkSide) {
            Card_Model.CardColour[] dark = {Card_Model.CardColour.TEAL,
                    Card_Model.CardColour.PURPLE,
                    Card_Model.CardColour.PINK,
                    Card_Model.CardColour.ORANGE};
            return dark[new Random().nextInt(4)];
        }

        Card_Model.CardColour[] light = {Card_Model.CardColour.RED,
                Card_Model.CardColour.BLUE,
                Card_Model.CardColour.GREEN,
                Card_Model.CardColour.YELLOW};
        return light[new Random().nextInt(4)];
    }

    /**
     * Processes any special effect of the initial card drawn at game start.
     * Handles REVERSE, SKIP, DRAW_ONE, WILD, FLIP, SKIP_EVERYONE, and DRAW_FIVE effects.
     * Different from mid-game effects: Wild requires random colour, no player played it.
     */
    private void processInitialCardEffect() {
        SpecialCardEffect effect = identifySpecialCard();
        switch (effect) {
            case REVERSE -> { if (participants.size() == 2) advanceToNextTurn(); else playDirection *= -1; }
            case SKIP -> advanceToNextTurn();
            case DRAW_ONE -> { forceCurrentPlayerDraw(1); advanceToNextTurn(); }
            case WILD -> matchColour = getRandomColour();
            case FLIP -> flipAllCards();
            case SKIP_EVERYONE -> {} // Current player plays again
            case DRAW_FIVE -> { forceCurrentPlayerDraw(5); advanceToNextTurn(); }
            default -> {}
        }
    }

    // ======== TURN MANAGEMENT ========

    /**
     * Advances to the next player's turn based on current play direction.
     * Handles wrapping around the player list in both directions using modulo arithmetic.
     */
    public void advanceToNextTurn() {
        turnIdx = (turnIdx + playDirection + participants.size()) % participants.size();
    }

    /**
     * Skips the next player in turn order.
     * Effectively advances the turn twice (used by Skip card).
     */
    public void skipNextPlayer() {
        advanceToNextTurn();
        advanceToNextTurn();
    }

    /**
     * Handles Skip Everyone card effect (dark side exclusive).
     * Current player plays again - no turn advancement needed.
     */
    public void skipAllOtherPlayers() {
        // Current player plays again - no turn advancement
    }

    /**
     * Reverses the play direction (clockwise to counter-clockwise or vice versa).
     * In a 2-player game, this acts like a skip and advances to the next turn.
     */
    public void reversePlayDirection() {
        playDirection *= -1;
        if (participants.size() == 2) advanceToNextTurn();
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
        if (card.isWildCard()) return true;
        return card.getColour() == matchColour || card.getCardValue() == matchType;
    }

    /**
     * Attempts to play a card from the current player's hand.
     * Validates the card index and legality of the play.
     * Processes special card effects and checks for round end.
     * Handles both light side (WILD, WILD_DRAW_TWO) and dark side (WILD_DRAW_COLOUR) wilds.
     * @param index the index of the card in the player's hand
     * @return TurnAction indicating the result of the play attempt
     */
    public TurnAction playCard(int index) {
        Player_Model player = getCurrentPlayer();
        if (index < 0 || index >= player.getNumCards()) return TurnAction.INVALID_CARD_INDEX;
        Card_Model card = player.playCard(index);
        if (!isValidPlay(card)) return TurnAction.INVALID_PLAY;

        activeCard = card;
        matchType = card.getCardValue();
        if (card.getColour() != Card_Model.CardColour.WILD) matchColour = card.getColour();
        player.removeCard(index);
        stack.addToDiscardPile(card);

        if (player.getNumCards() == 0) { endRound(player); return TurnAction.CARD_PLAYED; }

        SpecialCardEffect effect = identifySpecialCard();
        if (effect == SpecialCardEffect.WILD || effect == SpecialCardEffect.WILD_DRAW_TWO) {
            pendingColourSelection = true;
        } else if (effect == SpecialCardEffect.WILD_DRAW_COLOUR) {
            pendingDrawColourSelection = true;
        } else {
            processSpecialCardEffect(effect);
        }
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
     * Used when player might want to play the drawn card.
     * @return TurnAction.CARD_DRAWN
     */
    public TurnAction drawCard() {
        getCurrentPlayer().drawCard(stack);
        return TurnAction.CARD_DRAWN;
    }

    /**
     * Processes the effect of a special card after it has been played.
     * Handles both light side effects (REVERSE, SKIP, DRAW_ONE) and
     * dark side effects (FLIP, SKIP_EVERYONE, DRAW_FIVE).
     * @param effect the SpecialCardEffect to process
     */
    private void processSpecialCardEffect(SpecialCardEffect effect) {
        switch (effect) {
            case REVERSE -> reversePlayDirection();
            case SKIP -> skipNextPlayer();
            case DRAW_ONE -> { forceNextPlayerDraw(1); skipNextPlayer(); }
            case FLIP -> { flipAllCards(); advanceToNextTurn(); }
            case SKIP_EVERYONE -> skipAllOtherPlayers();
            case DRAW_FIVE -> { forceNextPlayerDraw(5); skipNextPlayer(); }
            default -> advanceToNextTurn();
        }
    }

    /**
     * Flips all cards in the game to the opposite side (light ↔ dark).
     * This includes the deck, all player hands, and the active card.
     * Updates match colour/type to reflect the new side of the active card.
     * Triggered by the FLIP card effect.
     */
    public void flipAllCards() {
        isDarkSide = !isDarkSide;
        stack.flipDeck();
        for (Player_Model p : participants) {
            p.flipAllCards();
        }
        if (activeCard != null) {
            activeCard.flipCardSide();
            matchColour = activeCard.getColour();
            matchType = activeCard.getCardValue();
        }
    }

    /**
     * Forces the current player to draw a specified number of cards.
     * Used for starting card effects that affect first player.
     * @param n the number of cards to draw
     */
    private void forceCurrentPlayerDraw(int n) {
        for (int i = 0; i < n; i++) getCurrentPlayer().drawCard(stack);
    }

    /**
     * Forces the next player in turn order to draw a specified number of cards.
     * Used by Draw One, Draw Five, and Wild Draw Two effects.
     * @param n the number of cards to draw
     */
    private void forceNextPlayerDraw(int n) {
        int nextIdx = (turnIdx + playDirection + participants.size()) % participants.size();
        Player_Model next = participants.get(nextIdx);
        for (int i = 0; i < n; i++) next.drawCard(stack);
    }

    /**
     * Forces the next player to draw cards until a card of the specified colour appears.
     * Used by the Wild Draw Colour card (dark side exclusive).
     * The drawn cards are added to the next player's hand.
     * @param colour the CardColour that stops the drawing
     */
    public void forceNextPlayerDrawUntilColour(Card_Model.CardColour colour) {
        int nextIdx = (turnIdx + playDirection + participants.size()) % participants.size();
        Player_Model next = participants.get(nextIdx);
        ArrayList<Card_Model> drawn = stack.drawCardsUntilColour(colour);
        for (Card_Model c : drawn) {
            next.addCard(c);
        }
    }

    /**
     * Identifies the special effect of the current active card.
     * Maps card values to their corresponding special effects.
     * Supports both light side cards (DRAW_ONE, WILD_DRAW_TWO) and
     * dark side cards (DRAW_FIVE, SKIP_EVERYONE, WILD_DRAW_COLOUR).
     * @return the SpecialCardEffect enum corresponding to the active card, or NONE for number cards
     */
    public SpecialCardEffect identifySpecialCard() {
        if (activeCard == null) return SpecialCardEffect.NONE;
        return switch (activeCard.getCardValue()) {
            case REVERSE -> SpecialCardEffect.REVERSE;
            case SKIP -> SpecialCardEffect.SKIP;
            case DRAW_ONE -> SpecialCardEffect.DRAW_ONE;
            case WILD -> SpecialCardEffect.WILD;
            case WILD_DRAW_TWO -> SpecialCardEffect.WILD_DRAW_TWO;
            case FLIP -> SpecialCardEffect.FLIP;
            case DRAW_FIVE -> SpecialCardEffect.DRAW_FIVE;
            case SKIP_EVERYONE -> SpecialCardEffect.SKIP_EVERYONE;
            case WILD_DRAW_COLOUR -> SpecialCardEffect.WILD_DRAW_COLOUR;
            default -> SpecialCardEffect.NONE;
        };
    }

    /**
     * Sets the active colour after a wild card has been played.
     * Validates that the colour is appropriate for the current side.
     * Also processes pending wild card effects:
     *   - WILD_DRAW_COLOUR: draws until colour, then skips
     *   - WILD_DRAW_TWO: draws 2, then skips
     *   - Regular WILD: just advances turn
     * @param colour the CardColour to set as the new match colour
     * @return true if colour was successfully set, false if invalid colour provided
     */
    public boolean setActiveColour(Card_Model.CardColour colour) {
        if (colour == null || colour == Card_Model.CardColour.WILD) return false;
        if (!isValidColourForCurrentSide(colour)) return false;

        matchColour = colour;

        if (pendingDrawColourSelection) {
            pendingDrawColourSelection = false;
            forceNextPlayerDrawUntilColour(colour);
            skipNextPlayer();
        } else if (pendingColourSelection) {
            pendingColourSelection = false;
            SpecialCardEffect effect = identifySpecialCard();
            if (effect == SpecialCardEffect.WILD_DRAW_TWO) {
                forceNextPlayerDraw(2);
                skipNextPlayer();
            } else {
                advanceToNextTurn();
            }
        }
        return true;
    }

    /**
     * Validates that a colour is appropriate for the current side.
     * Light side uses: Red, Blue, Green, Yellow
     * Dark side uses: Teal, Purple, Pink, Orange
     * @param colour the CardColour to validate
     * @return true if the colour matches the current side, false otherwise
     */
    private boolean isValidColourForCurrentSide(Card_Model.CardColour colour) {
        if (isDarkSide) {
            return Card_Model.isDarkSideColour(colour);
        }
        return Card_Model.isLightSideColour(colour);
    }

    /**
     * Ends the current round when a player empties their hand.
     * Calculates points from remaining cards in other players' hands.
     * Point values depend on current side (light vs dark scoring).
     * Checks if winner has reached TARGET_SCORE to end the game.
     * @param winner the Player_Model who emptied their hand
     */
    private void endRound(Player_Model winner) {
        int points = 0;
        for (Player_Model p : participants) {
            if (p != winner) {
                for (Card_Model c : p.getHand()) {
                    points += c.getCardScore(isDarkSide);
                }
            }
        }
        winner.setScore(winner.getScore() + points);
        roundScores.put(winner, points);
        if (winner.getScore() >= TARGET_SCORE) {
            victor = winner;
            status = GameStatus.GAME_OVER;
        } else {
            status = GameStatus.ROUND_ENDED;
        }
    }

    /**
     * Starts a new round by clearing hands, resetting the deck, and re-initializing.
     * Preserves player scores from previous rounds.
     * Resets to light side and forward play direction.
     */
    public void startNewRound() {
        participants.forEach(p -> p.getHand().clear());
        stack = new Deck_Model();
        turnIdx = 0;
        playDirection = 1;
        pendingColourSelection = false;
        pendingDrawColourSelection = false;
        activeCard = null;
        initialCard = null;
        isDarkSide = false;
        roundScores.clear();
        status = GameStatus.IN_PROGRESS;
        initializeGame();
    }

    /**
     * Resets the entire game to initial state.
     * Clears all player hands and scores, removes all players, resets deck,
     * and sets status to NOT_STARTED. Used to start a completely new game.
     */
    public void resetGame() {
        participants.forEach(p -> { p.resetScore(); p.getHand().clear(); });
        participants.clear();
        stack = new Deck_Model();
        turnIdx = 0;
        playDirection = 1;
        pendingColourSelection = false;
        pendingDrawColourSelection = false;
        activeCard = null;
        initialCard = null;
        victor = null;
        isDarkSide = false;
        status = GameStatus.NOT_STARTED;
        roundScores.clear();
    }

    // ======== AI SUPPORT ========

    /**
     * Gets the AI's card selection for its turn.
     * Delegates to the Player_Model's AI logic to determine which card to play.
     * @return index of the card to play, or -1 if AI should draw (no valid plays)
     */
    public int getAICardSelection() {
        Player_Model current = getCurrentPlayer();
        if (current == null || !current.isAI()) return -1;
        return current.selectCardToPlay(activeCard, matchColour, matchType, isDarkSide);
    }

    /**
     * Gets the AI's colour selection after playing a wild card.
     * Delegates to the Player_Model's AI logic to determine optimal colour choice.
     * @return the CardColour the AI chooses, or null if current player is not AI
     */
    public Card_Model.CardColour getAIColourSelection() {
        Player_Model current = getCurrentPlayer();
        if (current == null || !current.isAI()) return null;
        return current.selectWildColour();
    }

    /**
     * Checks if the current player is an AI.
     * Used by controller to determine whether to wait for user input or auto-play.
     * @return true if current player is AI-controlled, false otherwise
     */
    public boolean isCurrentPlayerAI() {
        Player_Model current = getCurrentPlayer();
        return current != null && current.isAI();
    }

    /**
     * Generates a formatted string representation of the current game state.
     * Includes side (light/dark), active card, match colour, deck size, direction,
     * status, current player's hand (with AI indicator), and all scores.
     * Useful for debugging and console-based play.
     * @return a formatted string containing comprehensive game state information
     */
    public String getGameStateString() {
        StringBuilder sb = new StringBuilder("\n=== UNO FLIP GAME STATE ===\n");
        sb.append("Side: ").append(isDarkSide ? "DARK" : "LIGHT").append("\n");
        sb.append("Active Card: ").append(activeCard).append("\n");
        sb.append("Match Colour: ").append(matchColour).append("\n");
        sb.append("Deck Cards: ").append(getRemainingDrawPileCards()).append("\n");
        sb.append("Direction: ").append(playDirection == 1 ? "Forward" : "Reverse").append("\n");
        sb.append("Status: ").append(status).append("\n");
        Player_Model current = getCurrentPlayer();
        if (current != null) {
            sb.append("\n--- CURRENT PLAYER ---\n");
            sb.append(current.getName()).append(current.isAI() ? " (AI)" : " (Human)");
            sb.append("'s Hand: ").append(current.getHand()).append("\n");
        }
        sb.append("\n=== SCORES ===\n");
        participants.forEach(p -> sb.append(p.getName())
                .append(p.isAI() ? " (AI)" : "")
                .append(": ").append(p.getScore()).append("\n"));
        return sb.toString();
    }
}