import java.util.*;
import java.util.stream.IntStream;

public class Uno_Model {
    private static final int CARDS_PER_PLAYER = 7;
    private static final int MIN_PARTICIPANTS = 2;
    private static final int MAX_PARTICIPANTS = 4;
    private static final int TARGET_SCORE = 500;

    public enum GameStatus { NOT_STARTED, IN_PROGRESS, ROUND_ENDED, GAME_OVER }
    public enum TurnAction { CARD_PLAYED, CARD_DRAWN, TURN_PASSED, INVALID_PLAY, INVALID_CARD_INDEX }
    public enum SpecialCardEffect { NONE, REVERSE, SKIP, DRAW_ONE, WILD, WILD_DRAW_TWO }

    private List<Player_Model> participants;
    private Deck_Model stack;
    private Card_Model activeCard;
    private Card_Model initialCard;
    private Card_Model.CardValue matchType;
    private Card_Model.CardColour matchColour;
    private Player_Model victor;
    private int turnIdx;
    private GameStatus status;
    private int playDirection;
    private boolean pendingColourSelection;
    private Map<Player_Model, Integer> roundScores;

    public Uno_Model() {
        participants = new ArrayList<>();
        stack = new Deck_Model();
        turnIdx = 0;
        status = GameStatus.NOT_STARTED;
        playDirection = 1;
        pendingColourSelection = false;
        roundScores = new HashMap<>();
    }

    // ======== GETTERS ========
    public Player_Model getCurrentPlayer() { return participants.isEmpty() ? null : participants.get(turnIdx); }
    public Card_Model getActiveCard() { return activeCard; }
    public Card_Model.CardColour getMatchColour() { return matchColour; }
    public int getRemainingDeckCards() { return stack.getNumDrawCards(); }
    public GameStatus getGameStatus() { return status; }
    public boolean isPendingColourSelection() { return pendingColourSelection; }
    public boolean isRoundEnded() { return status == GameStatus.ROUND_ENDED || status == GameStatus.GAME_OVER; }
    public boolean isGameOver() { return status == GameStatus.GAME_OVER; }
    public Player_Model getWinner() { return victor; }

    // ======== SETUP ========
    public boolean addPlayer(Player_Model player) {
        if (status != GameStatus.NOT_STARTED || player == null || participants.size() >= MAX_PARTICIPANTS)
            return false;
        participants.add(player);
        roundScores.put(player, 0);
        return true;
    }

    public void initializeGame() {
        distributeInitialCards();
        do { initialCard = stack.draw(); }
        while (initialCard.getCardValue() == Card_Model.CardValue.WILD_DRAW_TWO);

        activeCard = initialCard;
        matchColour = initialCard.getColour();
        matchType = initialCard.getCardValue();
        status = GameStatus.IN_PROGRESS;
        processInitialCardEffect();
        System.out.println(getGameStateString());
    }

    private void distributeInitialCards() {
        for (int i = 0; i < CARDS_PER_PLAYER; i++)
            for (Player_Model p : participants)
                p.addCardToHand(stack);
    }

    private void processInitialCardEffect() {
        SpecialCardEffect effect = identifySpecialCard();
        switch (effect) {
            case REVERSE -> { if (participants.size()==2) advanceToNextTurn(); else playDirection*=-1; }
            case SKIP -> advanceToNextTurn();
            case DRAW_ONE -> { forceCurrentPlayerDraw(1); advanceToNextTurn(); }
            case WILD -> matchColour = Card_Model.CardColour.values()[new Random().nextInt(4)];
            default -> {}
        }
    }

    // ======== TURN MANAGEMENT ========
    public void advanceToNextTurn() {
        turnIdx = (turnIdx + playDirection + participants.size()) % participants.size();
    }
    public void skipNextPlayer() { advanceToNextTurn(); advanceToNextTurn(); }
    public void reversePlayDirection() { playDirection *= -1; if (participants.size()==2) advanceToNextTurn(); }

    // ======== CARD LOGIC ========
    public boolean isValidPlay(Card_Model card) {
        if (card == null) return false;
        if (card.getCardValue() == Card_Model.CardValue.WILD || card.getCardValue() == Card_Model.CardValue.WILD_DRAW_TWO) return true;
        return card.getColour() == matchColour || card.getCardValue() == matchType;
    }

    public TurnAction playCard(int index) {
        Player_Model player = getCurrentPlayer();
        if (index<0 || index>=player.handSize()) return TurnAction.INVALID_CARD_INDEX;
        Card_Model card = player.viewCard(index);
        if (!isValidPlay(card)) return TurnAction.INVALID_PLAY;

        activeCard = card;
        matchType = card.getCardValue();
        if (card.getColour()!=Card_Model.CardColour.WILD) matchColour = card.getColour();
        player.discardCard(index);

        if (player.handSize()==0) { endRound(player); return TurnAction.CARD_PLAYED; }

        SpecialCardEffect effect = identifySpecialCard();
        if (effect==SpecialCardEffect.WILD || effect==SpecialCardEffect.WILD_DRAW_TWO) pendingColourSelection=true;
        else processSpecialCardEffect(effect);
        return TurnAction.CARD_PLAYED;
    }

    public TurnAction drawCardAndPass() { getCurrentPlayer().addCardToHand(stack); advanceToNextTurn(); return TurnAction.TURN_PASSED; }
    public TurnAction drawCard() { getCurrentPlayer().addCardToHand(stack); return TurnAction.CARD_DRAWN; }

    private void processSpecialCardEffect(SpecialCardEffect effect) {
        switch(effect){
            case REVERSE -> reversePlayDirection();
            case SKIP -> skipNextPlayer();
            case DRAW_ONE -> { forceNextPlayerDraw(1); skipNextPlayer(); }
            default -> advanceToNextTurn();
        }
    }

    private void forceCurrentPlayerDraw(int n) { for(int i=0;i<n;i++) getCurrentPlayer().addCardToHand(stack); }
    private void forceNextPlayerDraw(int n){
        int nextIdx = (turnIdx+playDirection+participants.size())%participants.size();
        Player_Model next = participants.get(nextIdx);
        for(int i=0;i<n;i++) next.addCardToHand(stack);
    }

    public SpecialCardEffect identifySpecialCard() {
        if(activeCard==null) return SpecialCardEffect.NONE;
        return switch(activeCard.getCardValue()){
            case REVERSE -> SpecialCardEffect.REVERSE;
            case SKIP -> SpecialCardEffect.SKIP;
            case DRAW_ONE -> SpecialCardEffect.DRAW_ONE;
            case WILD -> SpecialCardEffect.WILD;
            case WILD_DRAW_TWO -> SpecialCardEffect.WILD_DRAW_TWO;
            default -> SpecialCardEffect.NONE;
        };
    }

    public boolean setActiveColour(Card_Model.CardColour colour){
        if(colour==null || colour==Card_Model.CardColour.WILD) return false;
        matchColour=colour; pendingColourSelection=false;
        SpecialCardEffect effect=identifySpecialCard();
        if(effect==SpecialCardEffect.WILD_DRAW_TWO){ forceNextPlayerDraw(2); skipNextPlayer(); }
        else advanceToNextTurn();
        return true;
    }

    private void endRound(Player_Model winner){
        int points = participants.stream().filter(p->p!=winner).flatMap(p->p.getHand().stream()).mapToInt(c->c.getCardValue().cardScore).sum();
        winner.incrementScore(points);
        roundScores.put(winner, points);
        if(winner.fetchScore()>=TARGET_SCORE){ victor=winner; status=GameStatus.GAME_OVER; }
        else status=GameStatus.ROUND_ENDED;
    }

    public void startNewRound(){ participants.forEach(p->p.getHand().clear()); stack=new Deck_Model(); turnIdx=0; playDirection=1; pendingColourSelection=false; activeCard=null; initialCard=null; roundScores.clear(); initializeGame(); }
    public void resetGame(){ participants.forEach(p->{p.resetScore(); p.getHand().clear();}); stack=new Deck_Model(); turnIdx=0; playDirection=1; pendingColourSelection=false; activeCard=null; initialCard=null; victor=null; status=GameStatus.NOT_STARTED; roundScores.clear(); }

    public String getGameStateString(){
        StringBuilder sb=new StringBuilder("\n=== UNO GAME STATE ===\n");
        sb.append("Active Card: ").append(activeCard).append("\n");
        sb.append("Match Colour: ").append(matchColour).append("\n");
        sb.append("Deck Cards: ").append(getRemainingDeckCards()).append("\n");
        sb.append("Direction: ").append(playDirection==1?"Forward":"Reverse").append("\n");
        sb.append("Status: ").append(status).append("\n");
        Player_Model current=getCurrentPlayer();
        if(current!=null){ sb.append("\n--- CURRENT PLAYER ---\n"); sb.append(current.getName()).append("'s Hand: ").append(current.getHand()).append("\n"); }
        sb.append("\n=== SCORES ===\n"); participants.forEach(p->sb.append(p.getName()).append(": ").append(p.fetchScore()).append("\n"));
        return sb.toString();
    }

    // ======== MAIN METHOD ======== (for testing purposes)
    public static void main(String[] args){
        Uno_Model game = new Uno_Model();
        Player_Model alice = new Player_Model("Alice");
        Player_Model bob = new Player_Model("Bob");
        game.addPlayer(alice);
        game.addPlayer(bob);
        game.initializeGame();

        Scanner sc = new Scanner(System.in);

        while(!game.isGameOver()){
            Player_Model current = game.getCurrentPlayer();
            System.out.println("\n"+current.getName()+"'s turn!");
            System.out.println(game.getGameStateString());
            System.out.print("Enter card index to play (-1 to draw): ");
            int index = sc.nextInt();

            if(index==-1){ game.drawCardAndPass(); }
            else{
                Uno_Model.TurnAction res=game.playCard(index);
                if(res==TurnAction.INVALID_PLAY) System.out.println("❌ Invalid play!");
                else if(res==TurnAction.INVALID_CARD_INDEX) System.out.println("❌ Invalid index!");
                else if(res==TurnAction.CARD_PLAYED && game.isPendingColourSelection()){
                    System.out.print("You played a Wild! Choose colour (RED, BLUE, GREEN, YELLOW): ");
                    String c=sc.next();
                    Card_Model.CardColour col;
                    try{ col=Card_Model.CardColour.valueOf(c.toUpperCase()); }
                    catch(Exception e){ col=Card_Model.CardColour.RED; }
                    game.setActiveColour(col);
                }
            }

            if(game.isRoundEnded() && !game.isGameOver()){
                System.out.println("\n--- ROUND OVER ---\n"+game.roundScores);
                System.out.println("Starting next round...");
                game.startNewRound();
            }
        }
        System.out.println("\n🏆 GAME OVER! Winner: "+game.getWinner().getName());
    }
}
