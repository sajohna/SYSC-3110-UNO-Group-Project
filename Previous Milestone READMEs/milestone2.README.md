# Uno Game README

This README provides an overview of the Uno game, its components, and how to play it.

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


## How to Play
First, run the game by executing the Uno_View's main method and setting all the values after starting a new game using:
 Uno_Model game = new Uno_Model();

Then, set up players. 
Choose between Human, AI, or None for each slot (2–4 total).  
Enter names or use defaults.
   
Then, this is the third version with the GUI component, the following should be done to play:
Select the number of players - Controller creates players
Each palyers starts out with a hand of 7 cards
Play a card based on the active color or value mentioned in the GUI of the top card
Use special cards to change gameplay (Reverse, Skip, Wild, etc.).  
Flip cards to the Dark Side when a `FLIP` card is played.
If no card matches in player hand, draw card using the draw card button
If done playing a card, click next turn to switch to the next player
When wild card played, promoted to choose a colour
Error messages and notifications about game status will be popped up often

Finally, the round ends when one player runs out of cards.
That player’s score increases by the total value of all other players’ remaining cards.
The next round automatically begins.

## special actions

Reverse: Reverses the play direction.
Skip: Skips the next player’s turn.
Draw One: Next player draws one card and loses a turn.
Wild: Player chooses the next colour.
Wild Draw Two: Player chooses the next colour, next player draws two cards.
Wild Draw Colour: (Dark side) Next player draws until the chosen color appears 
Flip: Switches the game to the opposite side (Light ↔ Dark) 

## Team member contribution
Saan John – Game Logic (Uno_Model.java)
Lasya Erukulla – Controller Logic (Uno_Controller.java, Uno_Event.java)
Lucas Baker – View Logic (Uno_View.java, Uno_ViewHandler.java)
Rola Elghonimy - Diagrams and Readme 

## Enjoy the Game!
