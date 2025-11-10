import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Uno_View extends JFrame implements UnoViewHandler {
    private Uno_Model model;
    private JPanel playerHandPane;

    private JScrollPane scrollPane;
    private JButton topCard;
    private JButton nextButton;
    private JButton drawButton;

    private JPanel statusPane;
    private JLabel currentPlayerLabel;

    private JLabel playStatus;
    private JLabel colourStatus;

    private Uno_Controller controller;

    private Card_Model startingCard;

    private boolean firstRound;

    private int numPlayers;

    public Uno_View() {
        this.model = new Uno_Model(askNumberOfPlayers());
        this.controller = new Uno_Controller(model);
        setTitle("Uno Flip");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);

        // Create and configure components (buttons, labels, etc.)

        playerHandPane = new JPanel();
        scrollPane = new JScrollPane(playerHandPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(25);
        startingCard = model.getInitialCard();
        firstRound = true;
        String startCard = model.getInitialCard().toString();
        String imagePath = "images/" + startCard +".jpg";
        ImageIcon icon = new ImageIcon(imagePath);
        topCard = new JButton(icon);
        topCard.putClientProperty("card", model.getInitialCard());
        nextButton = new JButton("Next Player");
        drawButton = new JButton("Draw Card");
        nextButton.setActionCommand("Next Player");
        drawButton.setActionCommand("Draw");
        currentPlayerLabel = new JLabel((model.getCurrentPlayer().getName()) + "'s turn");

        playStatus = new JLabel("Select a card");
        colourStatus = new JLabel("Colour:" + model.getInitialCard().getColour().name());

        // Add components to the frame and layout configuration
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.SOUTH);
        this.add(topCard, BorderLayout.CENTER);


        // Status Pane Components and Creation
        statusPane = new JPanel();
        statusPane.setLayout(new BoxLayout(statusPane,BoxLayout.Y_AXIS));
        currentPlayerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        colourStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPane.add(currentPlayerLabel);
        statusPane.add(playStatus);
        statusPane.add(colourStatus);
        this.add(statusPane,BorderLayout.WEST);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(nextButton);
        buttonPanel.add(drawButton);
        this.add(buttonPanel, BorderLayout.NORTH);

        //next and draw buttons
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Uno_Event unoEvent = new Uno_Event(model);
                handleNextTurn(e);
                updateView();
            }
        });

        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDrawCard(e);
            }
        });
        model.checkActionCard();
        updateView();
        setVisible(true);
    }

    private int askNumberOfPlayers() {
        Integer[] playerOptions = { 2, 3, 4 };
        JComboBox<Integer> playerDropdown = new JComboBox<>(playerOptions);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Select number of players:"));
        panel.add(playerDropdown);

        int result = JOptionPane.showConfirmDialog(null, panel, "Number of Players", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            return (int) playerDropdown.getSelectedItem();
        }
        System.exit(0);
        return 0;
    }

    private void askWildCard(Card_Model card) {
        String[] playerOptions = { "BLUE", "YELLOW","RED", "GREEN"};
        JComboBox<String> playerDropdown = new JComboBox<>(playerOptions);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Select desired colour:"));
        panel.add(playerDropdown);

        int result = JOptionPane.showConfirmDialog(null, panel, "Number of Players", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            topCard.setIcon(new ImageIcon(card.getPathToImageFile()));
            controller.playWild(card, Card_Model.CardColour.valueOf((String) playerDropdown.getSelectedItem()));
            updateView();
            if (card.getCardValue() == Card_Model.CardValue.WILD) {
                updatePlayStatus("Colour has been changed!");
            } else{
                updatePlayStatus("Colour changed and next player draws 2/skips!");
            }

        }
    }

    private void showWinnerPopup() {
        String message = model.getCurrentPlayer().getName() + "! Wins!";
        JOptionPane.showMessageDialog(null, message, "Winner!", JOptionPane.INFORMATION_MESSAGE);
    }

    public void updatePlayerTurnLabel(){
        currentPlayerLabel.setText((model.getCurrentPlayer().getName())  + "'s turn");

    }

    public void updatePlayStatus(String status){
        playStatus.setText(status);
    }

    public void updateColourStatus(){
        colourStatus.setText("Colour: " + model.getActiveCard().getColour().name());
    }

    public void updateView() {
        // change player's hand, top card, and other components
        playerHandPane.removeAll();

        ArrayList<Card_Model> playerHand = controller.getCurrentPlayer().getHand();
        updatePlayerTurnLabel();
        updatePlayStatus("Please select a card");
        updateColourStatus();

        if (!controller.hasDrawn()){
            drawButton.setEnabled(true);
        }
        else{
            drawButton.setEnabled(false);
        }

        if (controller.getCurrentPlayer().canPlay() && !controller.getCurrentPlayer().getHasDrawn()){
            nextButton.setEnabled(false);
        }
        else{
            nextButton.setEnabled(true);
            drawButton.setEnabled(false);
        }

        for (Card_Model card: playerHand) {

            ImageIcon icon = new ImageIcon(card.getPathToImageFile());
            JButton cardButton = new JButton(icon);
            cardButton.putClientProperty("card", card);
            cardButton.setActionCommand("play");
            cardButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handlePlay(e);
                }
            });
            if (!controller.getCurrentPlayer().canPlay()){
                cardButton.setEnabled(false);
            }
            cardButton.setVisible(true);
            playerHandPane.add(cardButton);
        }
        // Repaint the player hand panel
        playerHandPane.revalidate();
        playerHandPane.repaint();
    }

    @Override
    public void handleDrawCard(ActionEvent e) {
        controller.actionPerformed(e);
        updateView();
        updatePlayStatus("Drew One Card");
    }


    @Override
    public void handlePlay(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        Card_Model card = (Card_Model) button.getClientProperty("card");
        if (card.getColour() == Card_Model.CardColour.WILD){
            askWildCard(card);
        } else if (controller.playCard(card)) {
            ImageIcon icon = new ImageIcon(card.getPathToImageFile());
            topCard.setIcon(icon);
            updateView();
            if (card.getCardValue() == Card_Model.CardValue.SKIP) {
                updatePlayStatus("Skipping Next Player's Turn!");
            } else if (card.getCardValue() == Card_Model.CardValue.DRAW_ONE){
                updatePlayStatus("Next player draws and skips turn!");
            } else if (card.getCardValue() == Card_Model.CardValue.REVERSE){
                updatePlayStatus("Order of players reversed!");
            } else{
                updatePlayStatus("Good move");
            }
        } else {
            updatePlayStatus("Invalid Move");
        }
        if (controller.checkForWinner()) {
            showWinnerPopup();
            nextButton.setEnabled(false);
            drawButton.setEnabled(false);
        }
    }

    @Override
    public void handleNextTurn(ActionEvent e) {
        controller.actionPerformed(e);
        updateView();
        firstRound = false;
    }

    public static void main(String[] args){
        Uno_View view = new Uno_View();
    }
}