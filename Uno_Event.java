import java.util.EventObject;

/**
 * Add class comments later
 * 
 * @author Lasya Erukulla
 * @version 2.0 Milestone 2
 */
public class Uno_Event extends EventObject {
    private Card_Model card;
    private Player_Model player;
    private Uno_Model.GameStatus gameStatus;

    public Uno_Event(Uno_Model source)
    {
        super(source);
    }

    public Uno_Event(Uno_Model source, Card_Model card){
        super(source);
        this.card=card;
    }

    public Uno_Event(Uno_Model source, Player_Model player){
        super(source);
        this.player=player;
    }

    public Uno_Event(Uno_Model source, Uno_Model.GameStatus gameStatus){
        super(source);
        this.gameStatus=gameStatus;
    }

    public Card_Model getCard()
    {
        return card;
    }

    public Player_Model getPlayer()
    {
        return player;
    }

    public Uno_Model.GameStatus getGameStatus()
    {
        return gameStatus;
    }
}
