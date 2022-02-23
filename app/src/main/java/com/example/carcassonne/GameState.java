package com.example.carcassonne;

/**
 * Represents the game state of Carcassonne that contains the instance variables
 * that display the needed information about the current state of the game
 * for a human user or the computer player to make decisions.
 *
 *  * @author DJ Backus, Sophie Arcangel
 */

public class GameState {
    private Player[] playerList;

    private int currentTurn;
    private boolean isPlacementStage;

    private Deck deck;
    private Board board;



    public GameState(Player[] players) {
        this.playerList = players;

        this.currentTurn = 0;
        this.isPlacementStage = true;

        this.deck = new Deck();
        this.board = new Board(this.deck.drawStartingTile());
    }

    // Copy constructor for GameState constructor

    /**
    External Citation
    Date: 21 February 2022
    Problem: Didn't know how to make a deep copy
    Resource:
     https://www.javainterviewpoint.com/copy-constructor/
    Solution: We used this website to learn how to deep copy
     and used it as an example for our code
*/
    public GameState(GameState gs) {
        this.playerList = new Player[gs.playerList.length];
        for (int i = 0; i < this.playerList.length; i++) {
            this.playerList[i] = new Player(gs.playerList[i]);
        }

        this.currentTurn = gs.currentTurn;
        this.isPlacementStage = gs.isPlacementStage;

        this.deck = new Deck(gs.deck);
        this.board = new Board(gs.board);
    }

    // Describes the state of the game as a string by printing all
    // the variables in the GameState
    @Override
    public String toString() {
        return "hi";
    }
}
