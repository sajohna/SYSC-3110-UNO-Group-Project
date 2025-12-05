For Milestone 4, we added to our UML to reflect the changes we made to support serialization, the undo/redo functionality, and loading saved games. We introduced a new class Uno_GameState to capture complete snapshots of the game state so at a later time, it can fully restore them and the entire game. There, ArrayLists were used to store players, cards and deck contents for efficient copying and restoration.
This class also stores:
AI strategy
Turn index and play direction
Active card, match colour, match type
Deck contents (draw pile + discard pile)
Pending wild colour selections
Light/Dark side mode

Some of the main data structure elements we added were:
List<PlayerStateShots>, for the fixed number of players for efficient iteration and ordering.
List<Card_Model> drawPileCards and List<Card_Model> discardPileCards for dynamic sizing and easy indexing and copying.
And very importantly, the PlayerStateShots inner class as it cleanly captures the individual players state.


Next, The controller now maintains two stacks to implement the undo and redo functionality, two new attributes, stackUNDO and stackREDO were added to the UML, UNDO utilizes LIFO which makes undoing the last move efficient and Redo supports the opposite. We used Stacks because they were the most ideal data structure for undo and redo as they Restore the previous state immediately
Keep chronological ordering automatically

some of the methods added to controller to implement this were:
saveGameStateForUndo()
undoGameState()
redoGameState()
clearUndoRedoHistory()

New save/load methods were also added to uno controller to implement the functionality. 
the mehods added to the UML for this were:
saveGame(String fileName) : boolean
loadGame(String fileName) : boolean

These operations use Java I/O streams, specifically ObjectOutputStream to serialize in Uno_Model and ObjectInputStream to restore the saved state. after loading, the controller is able to replace its internal uno model with the deserialized version.

Several classes were also updated to implement Serializable, they included a serial Version UID to maintain compatibility with the saved game files. 