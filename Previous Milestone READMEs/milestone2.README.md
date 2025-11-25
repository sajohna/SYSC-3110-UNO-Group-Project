# Uno Game README

This README provides an overview of the Uno game, its components, and how to play it.

## Introduction
Uno is a popular card game where the goal is to be the first player to get rid of all the cards in their hand by matching the colour, number or symbol of the previously played card. In this simulated UNO game, the objective of this version is to be the first player to reach 500 points.

## Game Components
The UNO game is implemented in Java and consists of the following components:

1. Uno_Model class
This is the main class that manages the game flow.
This class handles player turns, card validation, round progression, and scores. it also includes the main method used to start the game

2. Uno_Controller class
This class represents the Controller for UNO game, handles the interactions between the model and view

3. Uno_View class
This class handles the UI of the UNO game (View component for the game) Handles GUI elements and implements UnoViewHandler for event-driven updates

4. Uno_Event class
This class represents the Event object for UNO game, captures key events to be communicated throughout the model (cards played, player changes, game status updates)

5. Uno_ViewHandler.java
This class represents the Interface for handling UNO view, it defines the view components that respond to game state changes


## How to Play
First, run the game by executing the Uno_View's main method and setting all the values after starting a new game using:
 Uno_Model game = new Uno_Model();

Then, this is the second version with the GUI component, the following should be done to play:
Select the number of players - Controller creates players
Each palyers starts out with a hand of cards
Play a card based on the active color mentioned in the GUI
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

## Team member contribution
Saan John – Game Logic (Uno_Model.java)
Lasya Erukulla – Controller Logic (Uno_Controller.java, Uno_Event.java)
Lucas Baker – View Logic (Uno_View.java, Uno_ViewHandler.java)
Rola Elghonimy - Diagrams and Readme 

## Enjoy the Game!
