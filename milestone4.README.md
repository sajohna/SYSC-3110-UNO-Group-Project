# Uno Game README

This README provides an overview of the Uno game, its components, deliverables, and how to play it.

## Introduction
Uno is a popular card game where the goal is to be the first player to get rid of all the cards in their hand by matching the colour, number or symbol of the previously played card. In this simulated UNO game, the objective of this version is to be the first player to reach 500 points. This newest implementation of the game adds the UNO Flip feature. The game now includes:
- GUI built with SWING
-AI players with automated decision making
-Light and dark UNO Flip mechanisms
-Event driven updates

## Game Components
The UNO game is implemented in Java and consists of the following components:

1. Uno_Model class
This is the main class that manages the game flow.
This class handles player turns, card validation, round progression, and scores. it also includes the main method used to start the game. This class also handles Light/Dark side card flipping (UNO Flip mechanic) andRound and score tracking until a player reaches 500 points

2. Uno_Controller class
This class represents the Controller for UNO game, handles the interactions between the model and view. it also handles the player and AI input and manages wild colour selections and AI logic.

3. Uno_View class
This class handles the UI of the UNO game (View component for the game) Handles GUI elements and implements UnoViewHandler for event-driven updates. this class features the card based hand display with buttons, interactive color selection for wild cards, action log and score tracking and automatic AI turns with timed animations.

4. Uno_Event class
This class represents the Event object for UNO game, captures key events to be communicated throughout the model (cards played, player changes, game status updates)

5. Uno_ViewHandler.java
This class represents the Interface for handling UNO view, it defines the view components that respond to game state changes

6. Card_Model Class
defines the UNO card attributes including color (light/ dark side), value (number or special action), flip behaviour and scoring system.

7. Deck_Model Class
This class manages the draw and discard piles and handles deck creation, shuffling and reshuffling, drawing cards, flipping sides and discarding

8. Player_model Class
This class represents the player whether human or AI. where each player has a hand of cards and a score along with logic for drawing, playing and choosing cards.

9. Uno_GaemState Class
This class captures complete game state snapshots for save/load and undo/redo functionality. It stores, the player status(hands, score, names, AI status), turn information(current player, play direction), game state(active card, match colour, side, pending selections), deck state( draw pile and discard pile)



## How to Play
### Starting the Game
1. First, run the game by executing the Uno_Controller's main method and setting all the values after starting a new game using:
 Uno_Model game = new Uno_Model();

2. Configure game set up
    - Player Confihuration: Choose between Human, AI or none  for each slot (2–4 total).  
    - Enter names or use defaults.
    - Turn  Timer(optional): Check "Enable Turn Timer" and set the limit between 10 to 60 seconds.

3. Click "Start Game" to begin

### Game Play
Then, this is the third version with the GUI component, the following should be done to play:
Each palyers starts out with a hand of 7 cards
Play a card based on the active color or value mentioned in the GUI of the top card
Use special cards to change gameplay (Reverse, Skip, Wild, etc.).  
Flip cards to the Dark Side when a `FLIP` card is played.
If no card matches in player hand, draw card using the draw card button
If done playing a card, click next turn to switch to the next player
When wild card played, promoted to choose a colour
Error messages and notifications about game status will be popped up often

#### AI Players
AI players can take their automatically when its their turn
Clikc "Next Turn" to initiate AI player's turn
The action log shows what actions the AI performed
AI players cannot be undone by human players


## New Features for MILESTONE 4
1. Save/Load Game
    - Save Game: Click the "Save Game" button during an active game to save your progress to a file
    - Load Game: Click the "Load Game" buttorn to restore a previously saved game
    - Game state is fully preserved including player hands, score, deck state and current turn
    - Note: Loading a game will discard the current game in progress
2. Undo/Redo Game
    - Undo: Click the "Uno" button ro reverse the last action
    - Redo: Click the "Redo" button to reapply an undone actoion
    - Up to 50 actions can be undone
    - Undo/Redo is onyl avaiable during an active game
    - Undo/Redo is cleared when starting a new round or loading a game
    Status messages appear briefly to confirum undo/redo actions
3. Turn Timer (Milestone 5)
    - Enable at Setup: Check "Enable Turn Timer" before starting the game
    - Set Time Limit: Choose between 10-60 seconds per turn (default: 30s)
    - During Game: Timer displays remaining time for the current player
    - Time Warning: Timer turns red in the last 5 seconds
    - Timeout: If time expires, the player automatically draws a card and their turn ends
    - Timer only runs during active gameplay

## Special Actions
### Light Side Cards
Reverse: Reverses the play direction.
Skip: Skips the next player’s turn.
Draw One: Next player draws one card and loses a turn.
Wild: Player chooses the next colour.
Wild Draw Two: Player chooses the next colour, next player draws two cards.
Flip: Switches the game to the dark side 

### Dark Side Cards
Reverse: Reverses the play direction.
Skip: Skips the next player’s turn.
Draw Five: Next player draws five card and loses a turn.
Wild: Player chooses the next colour (dark colours only).
Wild Draw Colour: Current player chooses a colour, the next player draws until the chosen color appears. 
Flip: Switches the game to the light side

## Winning the game
The round ends when one player runs out of cards.
That player’s score increases by the total value of all other players’ remaining cards.
The next round automatically begins.
The first player to reach 500 points wins the game.

## Team member contribution
### Milestone 1 - Basic Game Logic
Saan John – Game Logic (Uno_Model.java and associted test files)
Lasya Erukulla – Deck & Card Modeling (Deck_Model.java, Card_Model.java and associted test files)
Lucas Baker – Player Logic (Player_Model.java and associted test files)
Rola Elghonimy - UML/Sequence Diagrams and README documentation 

### Milestone 2 - GUI Implementation
Saan John – Game Logic (Uno_Model.java and associted test files) 
Lasya Erukulla – Controller Logic (Uno_Controller.java, Uno_Event.java and associted test files)
Lucas Baker – View Logic (Uno_View.java, Uno_ViewHandler.java and associted test files)
Rola Elghonimy - UML/Sequence Diagrams and README documentation 

### Milestone 3 - UNO FLip & AI
Saan John – Game Logic (Uno_Model.java and associated test files)
Lasya Erukulla – Controller Logic (Uno_Controller.java, Uno_Event.java, Card_Model.java, Deck_Model.java and associated test files)
Lucas Baker – View Logic (Uno_View.java, Uno_ViewHandler.java, Player_Model.java, AI implementation and associated test files)
Rola Elghonimy - UML/Sequence Diagrams and README documentation 

### Milestone 4 - Save/Load, Undo/Redo
Saan John – Game Logic (Uno_Model.java and associated test files)
Lasya Erukulla – Controller Logic (Uno_Controller.java, Uno_Event.java, Card_Model.java, Deck_Model.java and associated test files), Save/Load functionality(Uno_GameState.java, SerializationTest.java), Undo/Redo State management
Lucas Baker – View Logic (Uno_View.java, Uno_ViewHandler.java, Player_Model.java, AI implementation and associated test files), UI for Undo/Redo (UndoRedoTest.java)
Rola Elghonimy - UML/Sequence Diagrams and README documentation 

### Milestone 5 - Turn Timer BONUS
Saan John – Game Logic (Uno_Model.java and associated test files), Turn Timer functionality
Lasya Erukulla – Controller Logic (Uno_Controller.java, Uno_Event.java, Card_Model.java, Deck_Model.java and associated test files, Uno_GameState.java, SerializationTest.java), Turn Timer controls
Lucas Baker – View Logic (Uno_View.java, Uno_ViewHandler.java, Player_Model.java and associated test files, UnodRedoTest.java), Turn Timer display
Rola Elghonimy - UML/Sequence Diagrams, README and other documentation  

## Techinal Notes
- Serialization Framework: Game state is serialized using Java's Serialization framework with serialVersionUID for version compatibility
- State Management: Undo/Redo uses a stack-based state management system with a maximum of 50 stored states
- Timer Implementation: Turn timer uses millisecond precision (System.currentTimeMillis()) for accurate time tracking
- Data Persistence: All game state including player hands, deck composition, scores, turn information, and game settings are preserved in save files
- MVC Architecture: Clean separation between Model (game logic), View (GUI), and Controller (coordination)

## Known Issues
- Timer label displays "tim e" instead of "time" due to a spacing issue in the UI component (cosmetic issue, DOES NOT affect the functionality and its not a implementation logic issue)

## Enjoy the Game!
