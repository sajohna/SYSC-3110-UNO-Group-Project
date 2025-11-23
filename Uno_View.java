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
 * @version 3.0 - Milestone 3
 */
public class Uno_View extends JFrame implements Uno_ViewHandler {
    private Uno_Controller controller;
    private JPanel mainPanel, topPanel, centerPanel, bottomPanel, rightPanel;
    private JLabel activeCardLabel, currentPlayerLabel, deckCountLabel, matchColorLabel, sideLabel;
    private JTextArea gameStateArea;
    private JButton topCardDisplay;
    private JPanel playerHandPanel;
    private List<JButton> cardButtons;
    private JButton drawCardButton, nextTurnButton, newRoundButton, newGameButton;
    private JPanel colorSelectionPanel;
    private JButton redButton, blueButton, greenButton, yellowButton;
    private JButton tealButton, purpleButton, pinkButton, orangeButton;
    private JPanel setupPanel;
    private JButton startGameButton;
    private JComboBox<String>[] playerTypeBoxes;
    private JTextField[] playerNameFields;
    private int selectedPlayerCount = 2;
    private Timer aiTimer;

    /**
     * Constructs the UNO view, initializes components, listeners, and displays the setup panel.
     * @param controller
     * the controller managing game logic and updates
     */
    public Uno_View(Uno_Controller controller) {
        this.controller = controller;
        controller.addViewHandler(this);
        setTitle("UNO Flip");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
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

        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.setBackground(new Color(60, 179, 113));

        sideLabel = new JLabel("Side: LIGHT");
        sideLabel.setForeground(Color.WHITE);
        sideLabel.setFont(new Font("Arial", Font.BOLD, 14));

        activeCardLabel = new JLabel("Active Card: None");
        activeCardLabel.setForeground(Color.WHITE);
        matchColorLabel = new JLabel("Match Color: None");
        matchColorLabel.setForeground(Color.WHITE);
        deckCountLabel = new JLabel("Deck: 0");
        deckCountLabel.setForeground(Color.WHITE);
        currentPlayerLabel = new JLabel("Current Player: None");
        currentPlayerLabel.setForeground(Color.YELLOW);

        topPanel.add(sideLabel);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(currentPlayerLabel);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(activeCardLabel);
        topPanel.add(matchColorLabel);
        topPanel.add(deckCountLabel);

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(34, 139, 34));

        JPanel topCardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        topCardPanel.setBackground(new Color(34, 139, 34));
        topCardPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), "Top Card", 0, 0, null, Color.WHITE));

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

        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(46, 125, 50));
        rightPanel.setPreferredSize(new Dimension(280, 0));

        gameStateArea = new JTextArea(15, 20);
        gameStateArea.setEditable(false);
        gameStateArea.setBackground(new Color(240, 248, 255));
        JScrollPane stateScroll = new JScrollPane(gameStateArea);
        stateScroll.setBorder(BorderFactory.createTitledBorder("Scores"));
        rightPanel.add(stateScroll);

        colorSelectionPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        colorSelectionPanel.setBorder(BorderFactory.createTitledBorder("Choose Color"));
        colorSelectionPanel.setBackground(new Color(46, 125, 50));
        colorSelectionPanel.setVisible(false);

        redButton = createColorButton("RED", new Color(220, 20, 60));
        blueButton = createColorButton("BLUE", new Color(30, 144, 255));
        greenButton = createColorButton("GREEN", new Color(34, 139, 34));
        yellowButton = createColorButton("YELLOW", new Color(255, 215, 0));
        tealButton = createColorButton("TEAL", new Color(0, 128, 128));
        purpleButton = createColorButton("PURPLE", new Color(128, 0, 128));
        pinkButton = createColorButton("PINK", new Color(255, 105, 180));
        orangeButton = createColorButton("ORANGE", new Color(255, 140, 0));

        colorSelectionPanel.add(redButton);
        colorSelectionPanel.add(blueButton);
        colorSelectionPanel.add(greenButton);
        colorSelectionPanel.add(yellowButton);
        colorSelectionPanel.add(tealButton);
        colorSelectionPanel.add(purpleButton);
        colorSelectionPanel.add(pinkButton);
        colorSelectionPanel.add(orangeButton);

        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(colorSelectionPanel);

        cardButtons = new ArrayList<>();

        // Setup panel with AI selection
        setupPanel = new JPanel();
        setupPanel.setLayout(new BoxLayout(setupPanel, BoxLayout.Y_AXIS));
        setupPanel.setBackground(new Color(34, 139, 34));

        JLabel titleLabel = new JLabel("UNO FLIP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel setupLabel = new JLabel("Configure Players (2-4):");
        setupLabel.setForeground(Color.WHITE);
        setupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel playerConfigPanel = new JPanel(new GridLayout(4, 3, 10, 10));
        playerConfigPanel.setBackground(new Color(34, 139, 34));
        playerConfigPanel.setMaximumSize(new Dimension(500, 200));

        playerTypeBoxes = new JComboBox[4];
        playerNameFields = new JTextField[4];
        String[] types = {"Human", "AI", "None"};

        for (int i = 0; i < 4; i++) {
            JLabel pLabel = new JLabel("Player " + (i + 1) + ":");
            pLabel.setForeground(Color.WHITE);
            playerNameFields[i] = new JTextField(i < 2 ? "Player" + (i + 1) : "");
            playerTypeBoxes[i] = new JComboBox<>(types);
            playerTypeBoxes[i].setSelectedIndex(i < 2 ? 0 : 2);
            playerConfigPanel.add(pLabel);
            playerConfigPanel.add(playerNameFields[i]);
            playerConfigPanel.add(playerTypeBoxes[i]);
        }

        startGameButton = new JButton("Start Game");
        startGameButton.setPreferredSize(new Dimension(200, 60));
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        setupPanel.add(Box.createVerticalGlue());
        setupPanel.add(titleLabel);
        setupPanel.add(Box.createVerticalStrut(30));
        setupPanel.add(setupLabel);
        setupPanel.add(Box.createVerticalStrut(20));
        setupPanel.add(playerConfigPanel);
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
        startGameButton.addActionListener(e -> handleStartGame());
        drawCardButton.addActionListener(e -> handleDrawCard());
        nextTurnButton.addActionListener(e -> handleNextTurn());
        newRoundButton.addActionListener(e -> handleNewRound());
        newGameButton.addActionListener(e -> handleNewGame());

        redButton.addActionListener(e -> handleColorSelection(Card_Model.CardColour.RED));
        blueButton.addActionListener(e -> handleColorSelection(Card_Model.CardColour.BLUE));
        greenButton.addActionListener(e -> handleColorSelection(Card_Model.CardColour.GREEN));
        yellowButton.addActionListener(e -> handleColorSelection(Card_Model.CardColour.YELLOW));
        tealButton.addActionListener(e -> handleColorSelection(Card_Model.CardColour.TEAL));
        purpleButton.addActionListener(e -> handleColorSelection(Card_Model.CardColour.PURPLE));
        pinkButton.addActionListener(e -> handleColorSelection(Card_Model.CardColour.PINK));
        orangeButton.addActionListener(e -> handleColorSelection(Card_Model.CardColour.ORANGE));
    }

    /**
     * Handles start game action.
     */
    private void handleStartGame() {
        List<Boolean> isAIList = new ArrayList<>();
        List<String> names = new ArrayList<>();
        
        for (int i = 0; i < 4; i++) {
            String type = (String) playerTypeBoxes[i].getSelectedItem();
            if (!type.equals("None")) {
                isAIList.add(type.equals("AI"));
                String name = playerNameFields[i].getText().trim();
                names.add(name.isEmpty() ? (type.equals("AI") ? "AI" + (i + 1) : "Player" + (i + 1)) : name);
            }
        }

        if (isAIList.size() < 2 || isAIList.size() > 4) {
            showError("Please configure 2-4 players");
            return;
        }

        controller.createPlayersWithConfig(isAIList, names);
        controller.initializeGame();
        showGamePanel();
        updateFullView();
        showMessage("Game started! " + controller.getCurrentPlayer().getName() + "'s turn.");
        checkAndProcessAI();
    }

    /**
     * Handles draw card action.
     */
    private void handleDrawCard() {
        if (controller.isPendingColourSelection() || controller.isPendingDrawColourSelection()) {
            showError("Please select a color first!");
            return;
        }
        String playerName = controller.getCurrentPlayer().getName();
        controller.handleDrawCard();
        showMessage(playerName + " drew a card.");
        nextTurnButton.setEnabled(true);
    }

    /**
     * Handles next turn action.
     */
    private void handleNextTurn() {
        if (controller.isPendingColourSelection() || controller.isPendingDrawColourSelection()) {
            showError("Please select a color first!");
            return;
        }
        controller.handleNextPlayer();
        nextTurnButton.setEnabled(false);
        showMessage("Now it's " + controller.getCurrentPlayer().getName() + "'s turn!");
        checkAndProcessAI();
    }

    /**
     * Handles new round action.
     */
    private void handleNewRound() {
        controller.startNewRound();
        newRoundButton.setVisible(false);
        newGameButton.setVisible(false);
        nextTurnButton.setEnabled(false);
        showMessage("New round started!");
        checkAndProcessAI();
    }

    /**
     * Handles new game action.
     */
    private void handleNewGame() {
        if (aiTimer != null) aiTimer.stop();
        controller.resetGame();
        showSetupPanel();
    }

    /**
     * Handles card play action.
     */
    private void handleCardPlay(int cardIndex) {
        if (controller.isPendingColourSelection() || controller.isPendingDrawColourSelection()) {
            showError("Please select a color first!");
            return;
        }
        String playerName = controller.getCurrentPlayer().getName();
        boolean success = controller.playCard(cardIndex);
        if (success) {
            if (controller.isPendingColourSelection() || controller.isPendingDrawColourSelection()) {
                showMessage(playerName + " played a WILD card! Choose a color.");
            } else {
                showMessage(playerName + " played a card!");
                nextTurnButton.setEnabled(true);
            }
        } else {
            showError("Invalid card play!");
        }
    }

    /**
     * Handles color selection for wild cards.
     */
    private void handleColorSelection(Card_Model.CardColour color) {
        boolean success = controller.setWildCardColour(color);
        if (success) {
            showMessage("Color set to " + color + ".");
            nextTurnButton.setEnabled(true);
            checkAndProcessAI();
        } else {
            showError("Invalid color for current side!");
        }
    }

    /**
     * Checks if the current player is an AI player and processes the AI turn
     * with appropriate delays.
     */
    private void checkAndProcessAI() {
        if (controller.getGameStatus() != Uno_Model.GameStatus.IN_PROGRESS) return;
        if (controller.isCurrentPlayerAI()) {
            setControlsEnabled(false);
            if (aiTimer != null) aiTimer.stop();
            aiTimer = new Timer(800, e -> {
                aiTimer.stop();
                String aiName = controller.getCurrentPlayer().getName();
                controller.executeAITurn();
                updateFullView();
                if (controller.getGameStatus() == Uno_Model.GameStatus.IN_PROGRESS) {
                    checkAndProcessAI();
                } else {
                    setControlsEnabled(true);
                }
            });
            aiTimer.setRepeats(false);
            aiTimer.start();
        } else {
            setControlsEnabled(true);
        }
    }

    /**
     * Enables or disables all interactive controls (used during AI turns).
     * @param enabled
     * true to enable controls; false to disable
     */
    private void setControlsEnabled(boolean enabled) {
        drawCardButton.setEnabled(enabled);
        for (JButton btn : cardButtons) btn.setEnabled(enabled);
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
        button.setFont(new Font("Arial", Font.BOLD, 10));
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
        boolean dark = controller.isDarkSide();
        sideLabel.setText("Side: " + (dark ? "DARK" : "LIGHT"));
        sideLabel.setForeground(dark ? Color.MAGENTA : Color.WHITE);
        mainPanel.setBackground(dark ? new Color(30, 30, 60) : new Color(34, 139, 34));
        
        if (controller.getActiveCard() != null) {
            activeCardLabel.setText("Active: " + controller.getActiveCard().toString());
            updateTopCardDisplay(controller.getActiveCard());
        }
        matchColorLabel.setText("Match: " + controller.getMatchColour());
        deckCountLabel.setText("Deck: " + controller.getRemainingDrawPileCards());

        Player_Model currentPlayer = controller.getCurrentPlayer();
        if (currentPlayer != null) {
            String aiTag = currentPlayer.isAI() ? " (AI)" : "";
            currentPlayerLabel.setText("Current: " + currentPlayer.getName() + aiTag);
            updatePlayerHand(currentPlayer.getHand());
        }

        updateScores();
        updateColorButtons(dark);
        colorSelectionPanel.setVisible(controller.isPendingColourSelection() || 
                                       controller.isPendingDrawColourSelection());
    }

    /**
     * Updates which color buttons are visible depending on whether the current
     * side is light or dark.
     * @param dark
     * true if on the dark side; false otherwise
     */
    private void updateColorButtons(boolean dark) {
        redButton.setVisible(!dark);
        blueButton.setVisible(!dark);
        greenButton.setVisible(!dark);
        yellowButton.setVisible(!dark);
        tealButton.setVisible(dark);
        purpleButton.setVisible(dark);
        pinkButton.setVisible(dark);
        orangeButton.setVisible(dark);
    }

    /**
     * Updates the top card display.
     */
    private void updateTopCardDisplay(Card_Model card) {
        if (card == null) {
            topCardDisplay.setText("<html><center>No Card</center></html>");
            topCardDisplay.setBackground(new Color(100, 100, 100));
            return;
        }
        topCardDisplay.setText("<html><center><b>" + card.getColour() + "</b><br><br>" + 
                               card.getCardValue() + "</center></html>");
        topCardDisplay.setBackground(getColorForCard(card.getColour()));
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
        JButton button = new JButton("<html><center>" + card.getColour() + "<br>" + 
                                     card.getCardValue() + "</center></html>");
        button.setPreferredSize(new Dimension(100, 140));
        button.setBackground(getColorForCard(card.getColour()));
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
            case TEAL -> new Color(0, 128, 128);
            case PURPLE -> new Color(128, 0, 128);
            case PINK -> new Color(255, 105, 180);
            case ORANGE -> new Color(255, 140, 0);
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
            sb.append(player.getName()).append(player.isAI() ? " (AI)" : "")
              .append(": ").append(player.getScore()).append(" pts\n");
        }
        sb.append("\n=== CARDS IN HAND ===\n\n");
        for (Player_Model player : controller.getParticipants()) {
            sb.append(player.getName()).append(": ").append(player.getNumCards()).append(" cards\n");
        }
        gameStateArea.setText(sb.toString());
    }

    /**
     * Displays a message dialog.
     */
    private void showMessage(String msg) { JOptionPane.showMessageDialog(this, msg); }
    /**
     * Displays an error message.
     */
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }

    /**
     * Handles game state update events from the controller.
     */
    @Override
    public void handleGameUpdate(Uno_Event event) { updateFullView(); }

    /**
     * Handles round end events.
     */
    @Override
    public void handleRoundEnd(Uno_Event event) {
        showMessage("Round ended! Check scores.");
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
            showMessage("GAME OVER! " + winner.getName() + " wins with " + winner.getScore() + " points!");
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
