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

    /*
     * Draws card from the deck, adds it to the player's hand, increases card count.
     * @param deck
     * The Uno deck to draw a card.
     */
    public void drawCard(Deck_Model deck)
    {
        hand.add(deck.draw());
        numCards++;
    }

    /*
     * Removes card from the player's hand by its index, decreases card count.
     * @param index
     * The index of the card in the list to remove from the player's hand.
     */
    public void removeCard(int index)
    {
        hand.remove(index);
        numCards--;
    }

    /*
     * Plays card from the player's hand.
     * @param index
     * The index of the card to play in the player's hand.
     * @return the card played.
     */
    public Card_Model playCard(int index)
    {
        return hand.get(index);
    }

    /*
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