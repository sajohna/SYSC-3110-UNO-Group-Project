import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Uno_Controller implements ActionListener {
    private Uno_Model uno;

    public Uno_Controller(Uno_Model uno) {
        this.uno = uno;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch(command){
            case "drawCard":
                uno.getCurrentPlayer().getName();
                uno.drawCard();
                break;
            case "nextPlayer":
                uno.advanceToNextTurn();
                break;
            default:
                break;
        }
    }

    public boolean playCard(Card_Model card){
        checkForWinner();
        keepPlaying();
        if(uno.selectCard(card)){  /*needs to be implemented in model*/
            //model.getCurrentPlayer().getMyCards().remove(card);
            uno.getCurrentPlayer().removeCard(card);
            uno.checkActionCard(); /*needs to be implemented in model*/
            return true;
        }
        return false;
    }
    public boolean checkForWinner() {
        System.out.println("Points = " + uno.getCurrentPlayer().getScore());
        if (checkWinner()) {
            System.out.println("Winner found");
            return true;
        }
        else
            System.out.println("no winner found");
        return false;
    }

    public boolean stopPlaying(){
        if (checkWinner() && uno.getCurrentPlayer().getScore() >= 500) {
            return true;
        }
        else return false;
    }
    public boolean keepPlaying(){
        if (checkWinner() && uno.getCurrentPlayer().getScore() < 500) {
            uno.notEnoughPoints(); /*needs to be implemented in model*/
            System.out.println("not enough points. points = " + uno.getCurrentPlayer().getScore());
            return true;
        }
        else return false;
    }

    public void playWild(Card_Model card, Card_Model.CardColour colour){
        wildCard(colour);
        uno.getCurrentPlayer().getMyCards().remove(card); /*needs to be implemented in model*/

        if (card.getCardValue() == Card_Model.CardValue.WILD_DRAW_TWO){
            uno.drawN(2,getIndex()); /*needs to be implemented in model*/
        }

    }

    public Player_Model getCurrentPlayer(){
        return uno.getCurrentPlayer();
    }

    /*needs to be implemented in model*/
    public boolean hasDrawn(){
        return uno.hasDrawn();
    }

    /*needs to be implemented in model*/
    public void setHasDrawn(){
        uno.getCurrentPlayer().setHasDrawn(true);
    }

    public void wildCard(Card_Model.CardColour colour){
        uno.wildCard(colour); /*needs to be implemented in model*/
    }

    public void checkActionCard(){
        Uno_Model.SpecialCardEffect effect = uno.identifySpecialCard();
    }

    /*needs to be implemented in model*/
    public int getIndex(){
        return uno.getCurrentPlayerIndex();
    }

    public boolean checkWinner(){
        if(uno.getCurrentPlayer().getNumCards() == 0){
            return true;
        }
        else return false;
    }
}
