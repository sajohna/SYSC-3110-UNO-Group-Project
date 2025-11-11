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
 *
 * @author Lucas Baker
 * @version 1.0 - Milestone 1
 */

import java.util.ArrayList;

public class Player_Model
{
    private String name;
    private ArrayList<Card_Model> hand;
    private int score;
    private int numCards;

    /**
     * Constructs a new player with an empty card list and a score of 0.
     */
    public Player_Model()
    {
        hand = new ArrayList<Card_Model>();
        numCards = 0;
        score = 0;
    }

    /**
     * Constructs a new player that sets the name with an empty card list and a score of 0.
     */
    public Player_Model(String name){
        this.name = name;
        hand = new ArrayList<Card_Model>();
        numCards = 0;
        score = 0;
    }
    /**
     * Gets list of cards in the player's hand.
     * @return ArrayList containing the player's cards.
     */
    public ArrayList<Card_Model> getHand()
    {
        return hand;
    }

    /**
     * Gets number of cards in the player's hand.
     * @return number of cards in the player's hand.
     */
    public int getNumCards()
    {
        return numCards;
    }

    /**
     * Gets the player's score.
     * @return the player's score.
     */
    public int getScore()
    {
        return score;
    }

    /**
     * Gets name of the player.
     * @return name of the player.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the player's score.
     * @param score
     * The score to set for the player.
     */
    public void setScore(int score)
    {
        this.score = score;
    }

    /**
     * Reset the player's score to 0.
     */
    public void resetScore()
    {
        score = 0;
    }

    /**
     * Sets name of the player.
     * @param name
     * The name to set for the player.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Adds a card to the player's hand, increases card count.
     * @param card
     * The card to add to the player's hand.
     */
    public void addCard(Card_Model card)
    {
        hand.add(card);
        numCards++;
    }

    /**
     * Draws card from the deck, adds it to the player's hand, increases card count.
     * @param deck
     * The Uno deck to draw a card.
     */
    public void drawCard(Deck_Model deck)
    {
        hand.add(deck.draw());
        numCards++;
    }

    /**
     * Removes card from the player's hand by its index, decreases card count.
     * @param index
     * The index of the card in the list to remove from the player's hand.
     */
    public void removeCard(int index)
    {
        hand.remove(index);
        numCards--;
    }

    /**
     * Removes a specific card object from the player's hand, decreases card count.
     * @param card
     * The card object to remove from the player's hand.
     */
    public void removeCard(Card_Model card)
    {
        hand.remove(card);
        numCards--;
    }

    /**
     * Plays card from the player's hand.
     * @param index
     * The index of the card to play in the player's hand.
     * @return the card played.
     */
    public Card_Model playCard(int index)
    {
        return hand.get(index);
    }

    /**
     * Displays the player's cards.
     */
    public void displayCards()
    {
        System.out.println("Your Cards:");
        for (int i = 0; i < hand.size(); i++)
        {
            System.out.println(i + 1 + "." + hand.get(i).toString());
        }
    }
}