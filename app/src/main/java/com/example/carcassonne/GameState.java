package com.example.carcassonne;

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
    // link for citation: https://www.javainterviewpoint.com/copy-constructor/
    public GameState(GameState gs) {
        this.playerList = new Player[gs.playerList.length];
        for (int i = 0; i < this.playerList.length; i++){
            this.playerList[i] = new Player(gs.playerList[i]);
        }

        this.currentTurn = gs.currentTurn;
        this.isPlacementStage = gs.isPlacementStage;

        this.deck = new Deck(gs.deck);
        this.board = new Board(gs.board);
    }

    @Override
    public String toString() {
        return "hi";
    }
}
