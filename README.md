# SYSC-3110-UNO-Group-Project
# Uno Game README

This README provides an overview of the Uno game, its components, and how to play it.

## Introduction
Uno is a popular card game where the goal is to be the first player to get rid of all the cards in their hand by matching the colour, number or symbol of the previously played card. In this simulated UNO game, the objective of this version is to be the first player to reach 500 points.

## Game Components
The UNO game is implemented in Java and consists of the following components:

1. Uno_Model class
This is the main class that manages the game flow.
This class handles player turns, card validation, round progression, and scores. it also includes the main method used to start the game

2. Player_Model class
This class represents a single player in the game where each player has a name, a hand of UNO cards, and a score.
This class has the methods for drawing, playing, and removing cards.

3. Deck_Model class
this class represents the draw pile of uno cards. it handles deck creation, shuffling, drawing and keeps track of how many cards are left in the pile.

4. Card_Model class
This class defines the individual cards with a value and colour. it contains the enums for CardValue and CardColour


## How to Play
First, run the game by executing the Main method and setting all the values after starting a new game using:
 Uno_Model game = new Uno_Model();

Then, as this is still the first version, the following methods must be used to perform their actionalities:
addPlayer(Player_Model player) Adds a player before the game starts (max 4).
initializeGame()	Starts the first round and deals cards.
advanceToNextTurn()	Moves to the next player based on play direction.
skipNextPlayer()	Skips the next player’s turn.
reversePlayDirection()	Reverses the play direction.
playCard(int index)	Plays a card from the current player's hand.
drawCardAndPass()	Draws one card and passes turn.
drawCard()	Draws one card without ending turn.
setActiveColour(Card_Model.CardColour colour)	Sets the colour after a Wild is played.
startNewRound()	Starts a new round while keeping scores.
resetGame()	Resets all scores and states.
getGameStateString()	Returns a formatted snapshot of the current game state.
addCard(Card_Model card)	Adds a card to the player’s hand.
drawCard(Deck_Model deck)	Draws a card from the deck.
removeCard(int index)	Removes a card from the hand by index.
playCard(int index)	Returns the card to be played.
displayCards()	Prints the player's current hand.
resetScore()	Resets the player's total score to 0.
getCardValue()	Returns the card’s value.
getColour()	Returns the card’s colour.
toString()	Returns a readable “COLOUR VALUE” format.
makePile()	Builds and shuffles a standard UNO deck.
draw()	Draws (and removes) the top card from the deck.
getNumDrawCards()	Returns how many cards remain.
isEmpty()	Checks if the deck is out of cards.

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
Lasya Erukulla – Deck & Card Modeling (Deck_Model.java, Card_Model.java)
Lucas Baker – Player Logic (Player_Model.java)
Rola Elghonimy - Diagrams and Readme 

## Enjoy the Game!
