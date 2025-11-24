import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a player in Uno Flip. Each player has a name, a hand of cards,
 * a score, and a count of how many cards they currently hold. The class provides methods to manage the player's hand,
 * such as adding, removing, drawing, and playing cards. It also allows for updating and resetting the player's score.
 *
 * Data Structure Design:
 * - String name: Holds the player's display name, used for identification in the game UI and logic
 * - ArrayList of Card_Model: A dynamic list that stores the cards currently in the player's hand
 *   ArrayList choosen for:
 *      * structure allows for efficient indexed access (for playing cards by index)
 *      * easy addition/removal as the hand changes during play
 * - int score: Tracks the player's total score in the game
 * - int numCards: Keeps track of the number of cards in player's hand
 * - boolean isAI: Keeps track of if it is the AI's turn to play
 * - AIStrategy aiStrategy: Stores which strategy the AI will use
 *
 * @author Lucas Baker
 * @version 3.0 - Milestone 3
 */
public class Player_Model {
    private String name;
    private ArrayList<Card_Model> hand;
    private int score;
    private int numCards;
    private boolean isAI;
    private AIStrategy aiStrategy;

    public enum AIStrategy {
        FIRST_VALID,      // Play first valid card found
        HIGHEST_SCORE,    // Play card worth most points
        STRATEGIC         // Consider game state for best move
    }

    /**
     * Constructs a new player with an empty card list and a score of 0.
     */
    public Player_Model() {
        hand = new ArrayList<>();
        numCards = 0;
        score = 0;
        isAI = false;
        aiStrategy = AIStrategy.FIRST_VALID;
    }

    /**
     * Constructs a new player that sets the name with an empty card list and a score of 0.
     */
    public Player_Model(String name) {
        this.name = name;
        hand = new ArrayList<>();
        numCards = 0;
        score = 0;
        isAI = false;
        aiStrategy = AIStrategy.FIRST_VALID;
    }

    /**
     * Constructs a new AI player that sets the name with an empty card list, a score of 0, and strategic strategy
     */
    public Player_Model(String name, boolean isAI) {
        this.name = name;
        hand = new ArrayList<>();
        numCards = 0;
        score = 0;
        this.isAI = isAI;
        aiStrategy = AIStrategy.STRATEGIC;
    }

    /**
     * Constructs a new AI player that sets the name with an empty card list, a score of 0, and given strategy
     */
    public Player_Model(String name, boolean isAI, AIStrategy strategy) {
        this.name = name;
        hand = new ArrayList<>();
        numCards = 0;
        score = 0;
        this.isAI = isAI;
        this.aiStrategy = strategy;
    }

    /**
     * Gets list of cards in the player's hand.
     * @return ArrayList containing the player's cards.
     */
    public ArrayList<Card_Model> getHand() { return hand; }

    /**
     * Gets number of cards in the player's hand.
     * @return number of cards in the player's hand.
     */
    public int getNumCards() { return numCards; }

    /**
     * Gets the player's score.
     * @return the player's score.
     */
    public int getScore() { return score; }

    /**
     * Gets name of the player.
     * @return name of the player.
     */
    public String getName() { return name; }

    /**
     * Gets if player is AI
     * @return if plauer is AI
     */
    public boolean isAI() { return isAI; }

    /**
     * Gets the AI player's strategy
     * @return the strategy
     */
    public AIStrategy getAIStrategy() { return aiStrategy; }

    /**
     * Sets the player's score.
     * @param score
     * The score to set for the player.
     */
    public void setScore(int score) { this.score = score; }

    /**
     * Reset the player's score to 0.
     */
    public void resetScore() { score = 0; }

    /**
     * Sets name of the player.
     * @param name
     * The name to set for the player.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets if player is AI.
     * @param isAI
     * Whether player is AI or not.
     */
    public void setAI(boolean isAI) { this.isAI = isAI; }

    /**
     * Sets the AI player's strategy.
     * @param strategy
     * The strategy of the AI player.
     */
    public void setAIStrategy(AIStrategy strategy) { this.aiStrategy = strategy; }

    /**
     * Adds a card to the player's hand, increases card count.
     * @param card
     * The card to add to the player's hand.
     */
    public void addCard(Card_Model card) {
        hand.add(card);
        numCards++;
    }

    /**
     * Draws card from the deck, adds it to the player's hand, increases card count.
     * @param deck
     * The Uno deck to draw a card.
     */
    public void drawCard(Deck_Model deck) {
        hand.add(deck.draw());
        numCards++;
    }

    /**
     * Removes card from the player's hand by its index, decreases card count.
     * @param index
     * The index of the card in the list to remove from the player's hand.
     */
    public void removeCard(int index) {
        hand.remove(index);
        numCards--;
    }

    /**
     * Plays card from the player's hand.
     * @param index
     * The index of the card to play in the player's hand.
     * @return the card played.
     */
    public Card_Model playCard(int index) {
        return hand.get(index);
    }

    /**
     * Flips all cards in the player's hand to their opposite side.
     */
    public void flipAllCards() {
        for (Card_Model card : hand) {
            card.flipCardSide();
        }
    }

    /**
     * Finds all cards in the player's hand that are valid.
     * @param activeCard
     * the currently active card on the pile
     * @param matchColour
     * the required matching colour
     * @param matchType
     * the required matching value
     * @return list of indices for the valid cards
     */
    public List<Integer> getValidCardIndices(Card_Model activeCard, Card_Model.CardColour matchColour, Card_Model.CardValue matchType) {
        List<Integer> valid = new ArrayList<>();
        for (int i = 0; i < hand.size(); i++) {
            Card_Model card = hand.get(i);
            if (isValidPlay(card, matchColour, matchType)) {
                valid.add(i);
            }
        }
        return valid;
    }

    /**
     * Determines whether a card is valid.
     * @param card
     * card being tested
     * @param matchColour
     * colour to match
     * @param matchType
     * value to match
     * @return true if the card is a valid play
     */
    private boolean isValidPlay(Card_Model card, Card_Model.CardColour matchColour, Card_Model.CardValue matchType) {
        if (card == null) return false;
        if (card.isWildCard()) return true;
        return card.getColour() == matchColour || card.getCardValue() == matchType;
    }

    /**
     * Selects the best card to play according to the AI's strategy.
     * @param activeCard
     * the active card
     * @param matchColour
     * the required colour
     * @param matchType
     * the required value
     * @param isDarkSide
     * true if game is on dark side
     * @return index of chosen card, or -1 if none
     */
    public int selectCardToPlay(Card_Model activeCard, Card_Model.CardColour matchColour,
            Card_Model.CardValue matchType, boolean isDarkSide) {
        if (!isAI) return -1;
        
        List<Integer> validIndices = getValidCardIndices(activeCard, matchColour, matchType);
        if (validIndices.isEmpty()) return -1;

        switch (aiStrategy) {
            case FIRST_VALID:
                return validIndices.get(0);
            case HIGHEST_SCORE:
                return selectHighestScoreCard(validIndices, isDarkSide);
            case STRATEGIC:
                return selectStrategicCard(validIndices, isDarkSide);
            default:
                return validIndices.get(0);
        }
    }

    /**
     * Selects the valid card with the highest point value.
     * @param validIndices
     * list of valid card indices
     * @param isDarkSide
     * whether dark side scoring applies
     * @return index of selected card
     */
    private int selectHighestScoreCard(List<Integer> validIndices, boolean isDarkSide) {
        int bestIdx = validIndices.get(0);
        int bestScore = 0;
        for (int idx : validIndices) {
            int cardScore = hand.get(idx).getCardScore(isDarkSide);
            if (cardScore > bestScore) {
                bestScore = cardScore;
                bestIdx = idx;
            }
        }
        return bestIdx;
    }

    /**
     * Selects the card with the highest calculated priority.
     * @param validIndices
     * valid card indices
     * @param isDarkSide
     * whether dark side rules apply
     * @return index of strategic choice
     */
    private int selectStrategicCard(List<Integer> validIndices, boolean isDarkSide) {
        int bestIdx = validIndices.get(0);
        int bestPriority = -1;
        
        for (int idx : validIndices) {
            Card_Model card = hand.get(idx);
            int priority = getCardPriority(card, isDarkSide);
            if (priority > bestPriority) {
                bestPriority = priority;
                bestIdx = idx;
            }
        }
        return bestIdx;
    }

    /**
     * Computes the priority of a card for AI strategy.
     * @param card
     * card to evaluate
     * @param isDarkSide
     * whether dark side scoring applies
     * @return calculated priority value
     */
    private int getCardPriority(Card_Model card, boolean isDarkSide) {
        int priority = card.getCardScore(isDarkSide);
        Card_Model.CardValue val = card.getCardValue();
        
        // Prioritize action cards when hand is small
        if (numCards <= 3) {
            if (val == Card_Model.CardValue.SKIP || 
                val == Card_Model.CardValue.SKIP_EVERYONE) priority += 25;
            if (val == Card_Model.CardValue.REVERSE) priority += 20;
            if (val == Card_Model.CardValue.DRAW_ONE || 
                val == Card_Model.CardValue.DRAW_FIVE) priority += 30;
        }
        
        // Save wild cards unless necessary
        if (card.isWildCard() && numCards > 1) priority -= 40;
        
        // Prefer flip cards in strategic situations
        if (val == Card_Model.CardValue.FLIP && numCards > 4) priority += 15;
        
        return priority;
    }

    /**
     * Selects the most advantageous colour when AI plays a wild card.
     * @return the chosen colour
     */
    public Card_Model.CardColour selectWildColour() {
        if (!isAI) return null;
        
        int[] colourCounts = new int[4];
        Card_Model.CardColour[] colours;
        Card_Model.CardSide side = hand.isEmpty() ? Card_Model.CardSide.LIGHT_SIDE
            : hand.get(0).getCurrentCardSide();
        
        if (side == Card_Model.CardSide.LIGHT_SIDE) {
            colours = new Card_Model.CardColour[]{
                Card_Model.CardColour.RED, Card_Model.CardColour.BLUE,
                Card_Model.CardColour.GREEN, Card_Model.CardColour.YELLOW
            };
        } else {
            colours = new Card_Model.CardColour[]{
                Card_Model.CardColour.TEAL, Card_Model.CardColour.PURPLE,
                Card_Model.CardColour.PINK, Card_Model.CardColour.ORANGE
            };
        }
        
        for (Card_Model card : hand) {
            Card_Model.CardColour c = card.getColour();
            for (int i = 0; i < colours.length; i++) {
                if (c == colours[i]) colourCounts[i]++;
            }
        }
        
        int maxIdx = 0;
        for (int i = 1; i < 4; i++) {
            if (colourCounts[i] > colourCounts[maxIdx]) maxIdx = i;
        }
        return colours[maxIdx];
    }

    /**
     * Displays the player's cards.
     */
    public void displayCards() {
        System.out.println("Your Cards:");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println((i + 1) + "." + hand.get(i).toString());
        }
    }
}
