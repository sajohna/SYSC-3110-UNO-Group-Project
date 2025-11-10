import java.awt.event.ActionEvent;

public interface UnoViewHandler {

    void handleNextTurn(ActionEvent e);
    void handleDrawCard(ActionEvent e);
    void handlePlay(ActionEvent e);

}
