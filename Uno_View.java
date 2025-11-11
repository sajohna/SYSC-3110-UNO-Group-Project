import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * View component for UNO Flip.
 * Handles GUI elements and implements UnoViewHandler for event-driven updates.
 *
 * Data Structures:
 *   - List<JButton> cardButtons: ArrayList storing player hand card buttons.
 *       Chosen for:
 *         * Dynamic resizing as hand size changes throughout the game
 *         * Sequential access when updating/clearing hand display
 *         * Direct index correspondence to player's card list
 *   - JButton[] playerCountButtons: Array of exactly 3 buttons (2-4 players).
 *       Chosen for:
 *         * Fixed size known at compile time
 *         * Simple indexed access for setup listeners
 *   - Multiple JPanel components: Swing containers organizing GUI layout.
 *       Used for:
 *         * Hierarchical organization of UI components
 *         * Different layout managers (BorderLayout, FlowLayout, BoxLayout, GridLayout)
 *
 * @author Lucas Baker
 * @version 2.0 - Milestone 2
 */
public class Uno_View extends JFrame implements Uno_ViewHandler {
    private Uno_Controller controller;

    // Main panels
    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private JPanel rightPanel;

    // Game state display components
    private JLabel activeCardLabel;
    private JLabel currentPlayerLabel;
    private JLabel deckCountLabel;
    private JLabel matchColorLabel;
    private JTextArea gameStateArea;
    private JButton topCardDisplay;

    // Player hand display
    private JPanel playerHandPanel;
    private List<JButton> cardButtons;

    // Action buttons
    private JButton drawCardButton;
    private JButton nextTurnButton;
    private JButton newRoundButton;
    private JButton newGameButton;

    // Wild card color selection buttons
    private JPanel colorSelectionPanel;
    private JButton redButton;
    private JButton blueButton;
    private JButton greenButton;
    private JButton yellowButton;

    // Player setup
    private JPanel setupPanel;
    private JButton[] playerCountButtons;
    private JButton startGameButton;

    private int selectedPlayerCount = 0;

    /**
     * Constructs the UnoGameView with controller.
     */
    public Uno_View(Uno_Controller controller) {
        this.controller = controller;
        controller.addViewHandler(this);

        setTitle("UNO Flip");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        initializeComponents();
        setupLayout();
        attachListeners();
        showSetupPanel();

        setVisible(true);
    }

    /**
     * Initializes all UI components.
     */
    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(34, 139, 34));

        // Top panel - game state info
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.setBackground(new Color(60, 179, 113));

        activeCardLabel = new JLabel("Active Card: None");
        activeCardLabel.setForeground(Color.WHITE);

        matchColorLabel = new JLabel("Match Color: None");
        matchColorLabel.setForeground(Color.WHITE);

        deckCountLabel = new JLabel("Deck: 0");
        deckCountLabel.setForeground(Color.WHITE);

        currentPlayerLabel = new JLabel("Current Player: None");
        currentPlayerLabel.setForeground(Color.YELLOW);

        topPanel.add(currentPlayerLabel);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(activeCardLabel);
        topPanel.add(matchColorLabel);
        topPanel.add(deckCountLabel);

        // Center panel - player hand
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(34, 139, 34));

        // Top card display panel
        JPanel topCardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        topCardPanel.setBackground(new Color(34, 139, 34));
        topCardPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "Top Card",
                0,
                0,
                null,
                Color.WHITE));

        topCardDisplay = new JButton("<html><center>No Card</center></html>");
        topCardDisplay.setPreferredSize(new Dimension(120, 160));
        topCardDisplay.setBackground(new Color(100, 100, 100));
        topCardDisplay.setForeground(Color.WHITE);
        topCardDisplay.setOpaque(true);
        topCardDisplay.setBorderPainted(true);
        topCardDisplay.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        topCardDisplay.setEnabled(false);

        topCardPanel.add(topCardDisplay);

        JLabel handLabel = new JLabel("Your Hand:");
        handLabel.setForeground(Color.WHITE);
        handLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        playerHandPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        playerHandPanel.setBackground(new Color(34, 139, 34));

        JScrollPane scrollPane = new JScrollPane(playerHandPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(new Color(34, 139, 34));

        JPanel handSection = new JPanel(new BorderLayout());
        handSection.setBackground(new Color(34, 139, 34));
        handSection.add(handLabel, BorderLayout.NORTH);
        handSection.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(topCardPanel, BorderLayout.NORTH);
        centerPanel.add(handSection, BorderLayout.CENTER);

        // Bottom panel - action buttons
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(60, 179, 113));

        drawCardButton = new JButton("Draw Card");
        drawCardButton.setPreferredSize(new Dimension(150, 40));

        nextTurnButton = new JButton("Next Turn");
        nextTurnButton.setPreferredSize(new Dimension(150, 40));
        nextTurnButton.setEnabled(false);

        newRoundButton = new JButton("New Round");
        newRoundButton.setPreferredSize(new Dimension(150, 40));
        newRoundButton.setVisible(false);

        newGameButton = new JButton("New Game");
        newGameButton.setPreferredSize(new Dimension(150, 40));
        newGameButton.setVisible(false);

        bottomPanel.add(drawCardButton);
        bottomPanel.add(nextTurnButton);
        bottomPanel.add(newRoundButton);
        bottomPanel.add(newGameButton);

        // Right panel - game state and color selection
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(46, 125, 50));
        rightPanel.setPreferredSize(new Dimension(250, 0));

        gameStateArea = new JTextArea(20, 20);
        gameStateArea.setEditable(false);
        gameStateArea.setBackground(new Color(240, 248, 255));
        JScrollPane stateScroll = new JScrollPane(gameStateArea);
        stateScroll.setBorder(BorderFactory.createTitledBorder("Scores"));

        rightPanel.add(stateScroll);

        // Color selection panel
        colorSelectionPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        colorSelectionPanel.setBorder(BorderFactory.createTitledBorder("Choose Wild Color"));
        colorSelectionPanel.setBackground(new Color(46, 125, 50));
        colorSelectionPanel.setVisible(false);

        redButton = createColorButton("RED", Color.RED);
        blueButton = createColorButton("BLUE", Color.BLUE);
        greenButton = createColorButton("GREEN", Color.GREEN);
        yellowButton = createColorButton("YELLOW", Color.YELLOW);

        colorSelectionPanel.add(redButton);
        colorSelectionPanel.add(blueButton);
        colorSelectionPanel.add(greenButton);
        colorSelectionPanel.add(yellowButton);

        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(colorSelectionPanel);

        cardButtons = new ArrayList<>();

        // Setup panel
        setupPanel = new JPanel();
        setupPanel.setLayout(new BoxLayout(setupPanel, BoxLayout.Y_AXIS));
        setupPanel.setBackground(new Color(34, 139, 34));

        JLabel setupLabel = new JLabel("Select Number of Players:");
        setupLabel.setForeground(Color.WHITE);
        setupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(34, 139, 34));

        playerCountButtons = new JButton[3];
        for (int i = 0; i < 3; i++) {
            int players = i + 2;
            playerCountButtons[i] = new JButton(players + " Players");
            playerCountButtons[i].setPreferredSize(new Dimension(150, 60));
            buttonPanel.add(playerCountButtons[i]);
        }

        startGameButton = new JButton("Start Game");
        startGameButton.setPreferredSize(new Dimension(200, 60));
        startGameButton.setEnabled(false);
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        setupPanel.add(Box.createVerticalGlue());
        setupPanel.add(setupLabel);
        setupPanel.add(Box.createVerticalStrut(30));
        setupPanel.add(buttonPanel);
        setupPanel.add(Box.createVerticalStrut(30));
        setupPanel.add(startGameButton);
        setupPanel.add(Box.createVerticalGlue());
    }

    /**
     * Sets up the layout of the main panel.
     */
    private void setupLayout() {
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    /**
     * Attaches all event listeners to UI components.
     */
    private void attachListeners() {
        // Player count selection
        for (int i = 0; i < playerCountButtons.length; i++) {
            final int playerCount = i + 2;
            playerCountButtons[i].addActionListener(e -> {
                selectedPlayerCount = playerCount;
                startGameButton.setEnabled(true);
            });
        }

        // Start game
        startGameButton.addActionListener(e -> handleStartGame());

        // Draw card
        drawCardButton.addActionListener(e -> handleDrawCard());

        // Next turn
        nextTurnButton.addActionListener(e -> handleNextTurn());

        // New round
        newRoundButton.addActionListener(e -> handleNewRound());

        // New game
        newGameButton.addActionListener(e -> handleNewGame());

        // Color selection
        redButton.addActionListener(e -> handleColorSelection(Card_Model.CardColour.RED));
        blueButton.addActionListener(e -> handleColorSelection(Card_Model.CardColour.BLUE));
        greenButton.addActionListener(e -> handleColorSelection(Card_Model.CardColour.GREEN));
        yellowButton.addActionListener(e -> handleColorSelection(Card_Model.CardColour.YELLOW));
    }

    /**
     * Handles start game action.
     */
    private void handleStartGame() {
        if (selectedPlayerCount < 2 || selectedPlayerCount > 4) {
            showError("Please select 2-4 players");
            return;
        }

        controller.createPlayers(selectedPlayerCount);
        controller.initializeGame();

        showGamePanel();
        updateFullView();
        showMessage("Game started! " + controller.getCurrentPlayer().getName() + "'s turn.");
    }

    /**
     * Handles draw card action.
     */
    private void handleDrawCard() {
        if (controller.isPendingColourSelection()) {
            showError("Please select a color for the wild card first!");
            return;
        }

        String playerName = controller.getCurrentPlayer().getName();
        controller.handleDrawCard();
        showMessage(playerName + " drew a card. Click Next Turn when ready.");
        nextTurnButton.setEnabled(true);
    }

    /**
     * Handles next turn action.
     */
    private void handleNextTurn() {
        if (controller.isPendingColourSelection()) {
            showError("Please select a color for the wild card first!");
            return;
        }

        controller.handleNextPlayer();
        nextTurnButton.setEnabled(false);
        showMessage("Now it's " + controller.getCurrentPlayer().getName() + "'s turn!");
    }

    /**
     * Handles new round action.
     */
    private void handleNewRound() {
        controller.startNewRound();
        newRoundButton.setVisible(false);
        newGameButton.setVisible(false);
        nextTurnButton.setEnabled(false);
        showMessage("New round started! " + controller.getCurrentPlayer().getName() + "'s turn.");
    }

    /**
     * Handles new game action.
     */
    private void handleNewGame() {
        controller.resetGame();
        selectedPlayerCount = 0;
        startGameButton.setEnabled(false);
        newRoundButton.setVisible(false);
        newGameButton.setVisible(false);
        showSetupPanel();
    }

    /**
     * Handles card play action.
     */
    private void handleCardPlay(int cardIndex) {
        if (controller.isPendingColourSelection()) {
            showError("Please select a color for the wild card first!");
            return;
        }

        String playerName = controller.getCurrentPlayer().getName();

        boolean success = controller.playCard(cardIndex);

        if (success) {
            if (controller.isPendingColourSelection()) {
                showMessage(playerName + " played a WILD card! Choose a color.");
            } else {
                showMessage(playerName + " played a card!");
                nextTurnButton.setEnabled(true);
            }
        } else {
            showError("Invalid card play! Card doesn't match color or value.");
        }
    }

    /**
     * Handles color selection for wild cards.
     */
    private void handleColorSelection(Card_Model.CardColour color) {
        boolean success = controller.setWildCardColour(color);

        if (success) {
            showMessage("Color set to " + color + ". Click Next Turn when ready.");
            nextTurnButton.setEnabled(true);
        } else {
            showError("Failed to set color!");
        }
    }

    /**
     * Creates a colored button for wild card color selection.
     */
    private JButton createColorButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }

    /**
     * Shows the setup panel.
     */
    private void showSetupPanel() {
        getContentPane().removeAll();
        add(setupPanel);
        revalidate();
        repaint();
    }

    /**
     * Shows the main game panel.
     */
    private void showGamePanel() {
        getContentPane().removeAll();
        add(mainPanel);
        revalidate();
        repaint();
    }

    /**
     * Updates the full view with current game state.
     */
    private void updateFullView() {
        // Update labels
        if (controller.getActiveCard() != null) {
            activeCardLabel.setText("Active Card: " + controller.getActiveCard().toString());
            updateTopCardDisplay(controller.getActiveCard());
        }
        matchColorLabel.setText("Match Color: " + controller.getMatchColour());
        deckCountLabel.setText("Deck: " + controller.getRemainingDrawPileCards());

        Player_Model currentPlayer = controller.getCurrentPlayer();
        if (currentPlayer != null) {
            currentPlayerLabel.setText("Current Player: " + currentPlayer.getName());
            updatePlayerHand(currentPlayer.getHand());
        }

        // Update scores
        updateScores();

        // Update color selection visibility
        colorSelectionPanel.setVisible(controller.isPendingColourSelection());
    }

    /**
     * Updates the top card display.
     */
    private void updateTopCardDisplay(Card_Model card) {
        if (card == null) {
            topCardDisplay.setText("<html><center>No Card</center></html>");
            topCardDisplay.setBackground(new Color(100, 100, 100));
            topCardDisplay.setForeground(Color.WHITE);
            return;
        }

        topCardDisplay.setText("<html><center><b>" +
                card.getColour() + "</b><br><br>" +
                card.getCardValue() + "</center></html>");

        Color bgColor = getColorForCard(card.getColour());
        topCardDisplay.setBackground(bgColor);
        topCardDisplay.setForeground(Color.WHITE);
    }

    /**
     * Updates the player's hand display.
     */
    private void updatePlayerHand(List<Card_Model> hand) {
        playerHandPanel.removeAll();
        cardButtons.clear();

        for (int i = 0; i < hand.size(); i++) {
            Card_Model card = hand.get(i);
            JButton cardButton = createCardButton(card, i);

            final int index = i;
            cardButton.addActionListener(e -> handleCardPlay(index));

            cardButtons.add(cardButton);
            playerHandPanel.add(cardButton);
        }

        playerHandPanel.revalidate();
        playerHandPanel.repaint();
    }

    /**
     * Creates a button representing a card.
     */
    private JButton createCardButton(Card_Model card, int index) {
        JButton button = new JButton("<html><center>" +
                card.getColour() + "<br>" +
                card.getCardValue() + "</center></html>");
        button.setPreferredSize(new Dimension(100, 140));

        Color bgColor = getColorForCard(card.getColour());
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        return button;
    }

    /**
     * Gets the display color for a card color.
     */
    private Color getColorForCard(Card_Model.CardColour colour) {
        return switch (colour) {
            case RED -> new Color(220, 20, 60);
            case BLUE -> new Color(30, 144, 255);
            case GREEN -> new Color(34, 139, 34);
            case YELLOW -> new Color(255, 215, 0);
            case WILD -> new Color(50, 50, 50);
        };
    }

    /**
     * Updates the scores display.
     */
    private void updateScores() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SCORES ===\n\n");

        for (Player_Model player : controller.getParticipants()) {
            sb.append(player.getName()).append(": ")
                    .append(player.getScore()).append(" pts\n");
        }

        sb.append("\n=== CARDS IN HAND ===\n\n");
        for (Player_Model player : controller.getParticipants()) {
            sb.append(player.getName()).append(": ")
                    .append(player.getNumCards()).append(" cards\n");
        }

        gameStateArea.setText(sb.toString());
    }

    /**
     * Displays a message dialog.
     */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * Displays an error message.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ========== UnoViewHandler Implementation ==========

    /**
     * Handles game state update events from the controller.
     */
    @Override
    public void handleGameUpdate(Uno_Event event) {
        updateFullView();
    }

    /**
     * Handles round end events.
     */
    @Override
    public void handleRoundEnd(Uno_Event event) {
        showMessage("Round ended! Check the scores.");
        newRoundButton.setVisible(true);
        newGameButton.setVisible(true);
    }

    /**
     * Handles game over events.
     */
    @Override
    public void handleGameOver(Uno_Event event) {
        Player_Model winner = event.getPlayer();
        if (winner != null) {
            showMessage("🎉 GAME OVER! " + winner.getName() + " wins with " +
                    winner.getScore() + " points!");
        }
        newRoundButton.setVisible(false);
        newGameButton.setVisible(true);
    }

    /**
     * Main method to launch the UNO game GUI.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Uno_Model model = new Uno_Model();
            Uno_Controller controller = new Uno_Controller(model);
            new Uno_View(controller);
        });
    }
}