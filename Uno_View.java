import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
 * Timer functionality: Note we are aware the timer says "tim e" instead of time. Not sure how to fix this issue - but its not a code problem.
 * @author Lucas Baker
 * @version 4.0 - Milestone 4
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
    private Timer aiTimer;
    private JTextArea actionLogArea;
    private JButton undoButton;
    private JButton redoButton;
    private JButton saveButton;
    private JButton loadButton;
    private JLabel undoRedoStatusLabel;
    private JLabel timerLabel;
    private Timer uiUpdateTimer;
    private JCheckBox timedModeCheckBox;
    private JSpinner timeLimitSpinner;

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
        uiUpdateTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimerDisplay();
            }
        });
        uiUpdateTimer.start();
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

        timerLabel = new JLabel("Time: --s");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setVisible(false);

        topPanel.add(sideLabel);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(currentPlayerLabel);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(activeCardLabel);
        topPanel.add(matchColorLabel);
        topPanel.add(deckCountLabel);

        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(timerLabel);

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

        // Create Undo/Redo panel
        JPanel undoRedoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        undoRedoPanel.setBackground(new Color(60, 179, 113));

        undoButton = new JButton("Undo");
        undoButton.setPreferredSize(new Dimension(100, 35));
        undoButton.setEnabled(false);

        redoButton = new JButton("Redo");
        redoButton.setPreferredSize(new Dimension(100, 35));
        redoButton.setEnabled(false);

        undoRedoStatusLabel = new JLabel("");
        undoRedoStatusLabel.setForeground(Color.WHITE);
        undoRedoStatusLabel.setFont(new Font("Arial", Font.ITALIC, 10));

        undoRedoPanel.add(undoButton);
        undoRedoPanel.add(redoButton);
        undoRedoPanel.add(undoRedoStatusLabel);

        JPanel saveLoadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        saveLoadPanel.setBackground(new Color(60, 179, 113));

        saveButton = new JButton("Save Game");
        saveButton.setPreferredSize(new Dimension(120, 35));

        loadButton = new JButton("Load Game");
        loadButton.setPreferredSize(new Dimension(120, 35));

        saveLoadPanel.add(saveButton);
        saveLoadPanel.add(loadButton);

        bottomPanel.removeAll();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.add(drawCardButton);
        bottomPanel.add(nextTurnButton);
        bottomPanel.add(newRoundButton);
        bottomPanel.add(newGameButton);
        bottomPanel.add(undoButton);
        bottomPanel.add(redoButton);
        bottomPanel.add(saveButton);
        bottomPanel.add(loadButton);

        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(46, 125, 50));
        rightPanel.setPreferredSize(new Dimension(280, 0));

        gameStateArea = new JTextArea(10, 20);
        gameStateArea.setEditable(false);
        gameStateArea.setBackground(new Color(240, 248, 255));
        JScrollPane stateScroll = new JScrollPane(gameStateArea);
        stateScroll.setBorder(BorderFactory.createTitledBorder("Scores"));
        rightPanel.add(stateScroll);

        // Add action log area
        actionLogArea = new JTextArea(8, 20);
        actionLogArea.setEditable(false);
        actionLogArea.setBackground(new Color(255, 255, 240));
        actionLogArea.setLineWrap(true);
        actionLogArea.setWrapStyleWord(true);
        JScrollPane logScroll = new JScrollPane(actionLogArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Action Log"));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(logScroll);

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


        // Initialize Checkbox
        timedModeCheckBox = new JCheckBox("Enable Turn Timer");
        timedModeCheckBox.setBackground(new Color(34, 139, 34));
        timedModeCheckBox.setForeground(Color.WHITE);
        timedModeCheckBox.setFont(new Font("Arial", Font.PLAIN, 16));
        timedModeCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Initialize Time Limit Spinner (Default 30s, min 10s, max 60s, step 5s)
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(30, 10, 60, 5);
        timeLimitSpinner = new JSpinner(spinnerModel);
        ((JSpinner.DefaultEditor) timeLimitSpinner.getEditor()).getTextField().setEditable(false);
        timeLimitSpinner.setPreferredSize(new Dimension(80, 30));
        timeLimitSpinner.setMaximumSize(new Dimension(80, 30));

        JLabel limitLabel = new JLabel("Time Limit (s):");
        limitLabel.setForeground(Color.WHITE);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        timePanel.setBackground(new Color(34, 139, 34));
        timePanel.add(limitLabel);
        timePanel.add(timeLimitSpinner);
        timePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Initial state: hide time limit spinner
        timePanel.setVisible(false);

        // Add listener to show/hide spinner when checkbox is toggled
        timedModeCheckBox.addActionListener(e -> timePanel.setVisible(timedModeCheckBox.isSelected()));

        // Add to setupPanel
        setupPanel.add(Box.createVerticalStrut(20)); // Strut to separate from player config
        setupPanel.add(timedModeCheckBox);
        setupPanel.add(timePanel);


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

        undoButton.addActionListener(e -> handleUndo());
        redoButton.addActionListener(e -> handleRedo());
        saveButton.addActionListener(e -> handleSaveGame());
        loadButton.addActionListener(e -> handleLoadGame());

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
     * Logs an action to the action log area.
     */
    private void logAction(String action) {
        actionLogArea.append(action + "\n");
        actionLogArea.setCaretPosition(actionLogArea.getDocument().getLength());
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


        boolean timedMode = timedModeCheckBox.isSelected();
        int timeLimit = (int) timeLimitSpinner.getValue();

        controller.setTimeModeEnabled(timedMode);
        if (timedMode) {
            controller.setTurnTimeLimit(timeLimit);
        }


        controller.initializeGame();
        showGamePanel();
        updateFullView();

        Player_Model currentPlayer = controller.getCurrentPlayer();
        if (currentPlayer != null) {
            logAction("Game started! " + currentPlayer.getName() + "'s turn.");
        }

        // AI turn is now initiated by user clicking Next Turn if it's the AI's first turn
    }

    /**
     * Handles draw card action.
     */
    private void handleDrawCard() {
        if (controller.isPlayerAI()) {
            showError("AI is playing! Please wait.");
            return;
        }
        if (controller.isPendingColourSelection() || controller.isPendingDrawColourSelection()) {
            showError("Please select a color first!");
            return;
        }
        String playerName = controller.getCurrentPlayer().getName();
        controller.handleDrawCard();
        logAction(playerName + " drew a card.");
        nextTurnButton.setEnabled(true);
    }

    /**
     * Handles next turn action.
     */
    private void handleNextTurn() {

        if (controller.getGameStatus() == Uno_Model.GameStatus.IN_PROGRESS && controller.isPlayerAI()) {
            nextTurnButton.setEnabled(false); // Disable while AI plays
            logAction(controller.getCurrentPlayer().getName() + " (AI) turn started by user click.");
            checkAndProcessAI();
            return;
        }


        if (controller.isPendingColourSelection() || controller.isPendingDrawColourSelection()) {
            showError("Please select a color first!");
            return;
        }

        controller.handleNextPlayer();
        nextTurnButton.setEnabled(false);
        logAction("Now it's " + controller.getCurrentPlayer().getName() + "'s turn!");
    }

    /**
     * Handles new round action.
     */
    private void handleNewRound() {
        controller.startNewRound();
        newRoundButton.setVisible(false);
        newGameButton.setVisible(false);
        nextTurnButton.setEnabled(false);
        actionLogArea.setText("");
        logAction("New round started!");

        // AI turn is now initiated by user clicking Next Turn.
    }

    /**
     * Handles new game action.
     */
    private void handleNewGame() {
        if (aiTimer != null) aiTimer.stop();
        controller.resetGame();
        actionLogArea.setText("");
        showSetupPanel();
    }

    /**
     * Handles card play action.
     */
    private void handleCardPlay(int cardIndex) {
        if (controller.isPlayerAI()) {
            showError("AI is playing! Please wait.");
            return;
        }
        if (controller.isPendingColourSelection() || controller.isPendingDrawColourSelection()) {
            showError("Please select a color first!");
            return;
        }
        String playerName = controller.getCurrentPlayer().getName();
        Card_Model cardPlayed = controller.getCurrentPlayer().getHand().get(cardIndex);
        boolean success = controller.playCard(cardIndex);
        if (success) {
            if (controller.isPendingColourSelection() || controller.isPendingDrawColourSelection()) {
                logAction(playerName + " played " + cardPlayed + ". Choose a color!");
            } else {
                logAction(playerName + " played " + cardPlayed + ".");
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
        if (controller.isPlayerAI()) {
            showError("AI is playing! Please wait.");
            return;
        }
        boolean success = controller.setWildCardColour(color);
        if (success) {
            logAction(controller.getCurrentPlayer().getName() + " chose color: " + color + ".");
            nextTurnButton.setEnabled(true);

        } else {
            showError("Invalid color for current side!");
        }
    }

    /**
     * Handles undo button click.
     */
    private void handleUndo() {
        if (controller.undoGameState()) {
            logAction("Action undone");
            undoRedoStatusLabel.setText("Undone");
            Timer timer = new Timer(2000, e -> undoRedoStatusLabel.setText(""));
            timer.setRepeats(false);
            timer.start();
        } else {
            showError("Nothing to undo!");
        }
    }

    /**
     * Handles redo button click.
     */
    private void handleRedo() {
        if (controller.redoGameState()) {
            logAction("Action redone");
            undoRedoStatusLabel.setText("Redone");
            Timer timer = new Timer(2000, e -> undoRedoStatusLabel.setText(""));
            timer.setRepeats(false);
            timer.start();
        } else {
            showError("Nothing to redo!");
        }
    }

    /**
     * Handles save game button click.
     */
    private void handleSaveGame() {
        if (controller.getGameStatus() != Uno_Model.GameStatus.IN_PROGRESS) {
            showError("Can only save during active game!");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");
        fileChooser.setSelectedFile(new File("uno_save.dat"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (controller.saveGame(file.getAbsolutePath())) {
                logAction("Game saved to " + file.getName());
                JOptionPane.showMessageDialog(this,
                        "Game saved successfully!",
                        "Save Complete",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                showError("Failed to save game!");
            }
        }
    }

    /**
     * Handles load game button click.
     */
    private void handleLoadGame() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Loading will discard current game. Continue?",
                "Confirm Load",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (controller.loadGame(file.getAbsolutePath())) {
                actionLogArea.setText("");
                logAction("Game loaded from " + file.getName());
                showGamePanel();
                updateFullView();
                JOptionPane.showMessageDialog(this,
                        "Game loaded successfully!",
                        "Load Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                // AI turn is now initiated by user clicking Next Turn.
            } else {
                showError("Failed to load game!");
            }
        }
    }

    /**
     * Updates undo/redo button states.
     */
    private void updateUndoRedoButtons() {
        undoButton.setEnabled(controller.canUndo());
        redoButton.setEnabled(controller.canRedo());
    }


    /**
     * Checks if the current player is an AI player and processes the AI turn
     * with appropriate delays and logging.
     */
    private void checkAndProcessAI() {
        if (controller.getGameStatus() != Uno_Model.GameStatus.IN_PROGRESS) {
            setControlsEnabled(true);
            return;
        }

        if (controller.isPlayerAI()) {
            setControlsEnabled(false);
            if (aiTimer != null) aiTimer.stop();

            aiTimer = new Timer(1000, e -> {
                try {
                    Player_Model currentPlayer = controller.getCurrentPlayer();
                    if (currentPlayer == null || !currentPlayer.isAI()) {
                        setControlsEnabled(true);
                        return;
                    }

                    String aiName = currentPlayer.getName();

                    // Log what the AI is about to do
                    if (controller.isPendingColourSelection() || controller.isPendingDrawColourSelection()) {
                        logAction(aiName + " (AI) is choosing a color...");
                        boolean processed = controller.processAITurn();
                        if (processed) {
                            logAction(aiName + " (AI) chose color: " + controller.getMatchColour() + ".");
                        }
                    } else {
                        int numCardsBefore = currentPlayer.getNumCards();
                        Card_Model activeCardBefore = controller.getActiveCard();

                        logAction(aiName + " (AI) is taking their turn...");
                        boolean processed = controller.processAITurn();

                        if (processed) {
                            // Check what happened after the turn
                            Player_Model afterPlayer = controller.getCurrentPlayer();
                            int numCardsAfter = currentPlayer.getNumCards();
                            Card_Model activeCardAfter = controller.getActiveCard();

                            // Determine what action was taken
                            if (numCardsAfter > numCardsBefore) {
                                logAction(aiName + " (AI) drew a card and passed.");
                            } else if (activeCardAfter != activeCardBefore && numCardsAfter < numCardsBefore) {
                                logAction(aiName + " (AI) played " + activeCardAfter + ".");
                            }
                        }
                    }

                    updateFullView();

                    // Continue processing if still AI's turn
                    // This internal recursive call is needed because AI might need multiple steps (e.g., color selection)
                    if (controller.getGameStatus() == Uno_Model.GameStatus.IN_PROGRESS &&
                            controller.isPlayerAI()) {
                        checkAndProcessAI();
                    } else {
                        // AI turn is completely over, and it's either a Human's turn or the game ended.
                        setControlsEnabled(true);
                        if (controller.getGameStatus() == Uno_Model.GameStatus.IN_PROGRESS) {
                            Player_Model nextPlayer = controller.getCurrentPlayer();
                            if (nextPlayer != null && !nextPlayer.isAI()) {
                                logAction("It's now " + nextPlayer.getName() + "'s turn!");
                                nextTurnButton.setEnabled(false); // Start of human turn, pass button is disabled.
                            } else if (nextPlayer != null && nextPlayer.isAI()) {
                                // If the chain of AI moves unexpectedly stops on an AI, enable button for user to resume.
                                nextTurnButton.setEnabled(true);
                                logAction("It's now " + nextPlayer.getName() + " (AI)'s turn. Press 'Next Turn' to continue.");
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    setControlsEnabled(true);
                    showError("Error during AI turn: " + ex.getMessage());
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
     */
    private void setControlsEnabled(boolean enabled) {
        drawCardButton.setEnabled(enabled);
        // The state of nextTurnButton is managed separately by action handlers and handleGameUpdate
        for (JButton btn : cardButtons) btn.setEnabled(enabled);
        redButton.setEnabled(enabled);
        blueButton.setEnabled(enabled);
        greenButton.setEnabled(enabled);
        yellowButton.setEnabled(enabled);
        tealButton.setEnabled(enabled);
        purpleButton.setEnabled(enabled);
        pinkButton.setEnabled(enabled);
        orangeButton.setEnabled(enabled);

        // Only show color selection if human player needs to choose
        boolean showColorPanel = (controller.isPendingColourSelection() ||
                controller.isPendingDrawColourSelection()) &&
                !controller.isPlayerAI();
        colorSelectionPanel.setVisible(showColorPanel);
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
            currentPlayerLabel.setForeground(currentPlayer.isAI() ? Color.CYAN : Color.YELLOW);

            // Only show current player's hand if they're human
            if (!currentPlayer.isAI()) {
                updatePlayerHand(currentPlayer.getHand());
            } else {
                // Show placeholder for AI hand
                playerHandPanel.removeAll();
                cardButtons.clear();
                JLabel aiHandLabel = new JLabel("AI Player - " + currentPlayer.getNumCards() + " cards (Waiting for click...)");
                aiHandLabel.setForeground(Color.CYAN);
                aiHandLabel.setFont(new Font("Arial", Font.BOLD, 16));
                playerHandPanel.add(aiHandLabel);
                playerHandPanel.revalidate();
                playerHandPanel.repaint();
            }
        }

        updateScores();
        updateColorButtons(dark);

        // Color panel visibility is now handled within setControlsEnabled to ensure it is hidden for AI
        setControlsEnabled(!controller.isPlayerAI());

        updateUndoRedoButtons();
    }

    /**
     * Updates which color buttons are visible depending on whether the current
     * side is light or dark.
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
     * Updates the timer display
     */
    private void updateTimerDisplay() {
        boolean timedMode = controller.isTimeModeEnabled();
        Uno_Model.GameStatus status = controller.getGameStatus();

        // Only show and process the timer if the game is active and timed mode is enabled
        if (status == Uno_Model.GameStatus.IN_PROGRESS && timedMode) {
            int remainingTime = controller.getRemainingTurnTime();
            timerLabel.setVisible(true);

            if (remainingTime > 0) {
                // Display remaining time, turn red for the last 5 seconds
                timerLabel.setText("Time: " + remainingTime + "s");
                timerLabel.setForeground(remainingTime <= 5 ? Color.RED : Color.WHITE);
            } else if (remainingTime == 0) {
                timerLabel.setText("Time: EXPIRED");
                timerLabel.setForeground(Color.RED);

                // If time has expired and it's a human player's turn, force the timeout action
                boolean expired = controller.handleTurnTimeout();
                if (!controller.isPlayerAI()) {
                    if (expired) {
                        logAction(controller.getCurrentPlayer().getName() + "'s turn expired! Drew card and passed.");
                    }
                }
            } else {
                // remainingTime is -1 (timed mode enabled but timer not running, e.g., initial turn state)
                timerLabel.setText("Time: --s");
                timerLabel.setForeground(Color.WHITE);
            }
        } else {
            // Not in progress or timed mode disabled: hide/reset display
            timerLabel.setText("Time: --s");
            timerLabel.setVisible(false);
        }
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
        
        Player_Model currentPlayer = controller.getCurrentPlayer();
        if (currentPlayer != null) {
            sb.append(currentPlayer.getName())
                    .append(currentPlayer.isAI() ? " (AI)" : "")
                    .append(": ").append(currentPlayer.getNumCards()).append(" cards\n");
        }
        for (Player_Model player : controller.getParticipants()) {
            if (player != currentPlayer) {
                sb.append(player.getName())
                        .append(player.isAI() ? " (AI)" : "")
                        .append(": ").append(player.getNumCards()).append(" cards\n");
            }
        }
        gameStateArea.setText(sb.toString());
    }


    /**
     * Displays an error message to the user.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Handles game state update events.
     */
    @Override
    public void handleGameUpdate(Uno_Event event) {
        updateFullView();
        updateUndoRedoButtons();

        // Logic to enforce the "Next Turn" requirement for AI
        if (controller.getGameStatus() == Uno_Model.GameStatus.IN_PROGRESS) {
            if (controller.isPlayerAI()) {
                // It's the AI's turn, waiting for user trigger
                setControlsEnabled(false);
                nextTurnButton.setEnabled(true);
            } else {
                // It's a human's turn. setControlsEnabled(true) is managed elsewhere.
                // Next Turn button state (enabled after draw/play, disabled otherwise) is maintained by action handlers.
            }
        }
    }

    /**
     * Handles round end events.
     */
    @Override
    public void handleRoundEnd(Uno_Event event) {
        logAction("Round ended! Check scores.");
        newRoundButton.setVisible(true);
        newGameButton.setVisible(true);
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);
    }

    /**
     * Handles game over events.
     */
    @Override
    public void handleGameOver(Uno_Event event) {
        Player_Model winner = controller.getWinner();
        if (winner != null) {
            logAction("GAME OVER! " + winner.getName() + " wins with " + winner.getScore() + " points!");
            JOptionPane.showMessageDialog(this,
                    "GAME OVER! " + winner.getName() + " wins with " + winner.getScore() + " points!");
        }
        newRoundButton.setVisible(false);
        newGameButton.setVisible(true);
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);
    }

}