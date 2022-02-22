package com.example.carcassonne;

public class GameState {
    //instance variables
    private Player[] playerList;
    private Deck deck;
    private int currentTurn;
    private Tile currentTile;
    private boolean isTileValid;
    private boolean isPlacementStage;

    public GameState(Player[] players, Tile t){
        playerList = players;
        isPlacementStage = true;
        deck = new Deck();
        currentTurn = 0;
        currentTile = t;
    }

    // Copy constructor for gamestate constructor
    // link for citation: https://www.javainterviewpoint.com/copy-constructor/
    public GameState(GameState gs){
        this.playerList = new Player[gs.playerList.length];
        for(int i = 0; i < this.playerList.length; i++){
            this.playerList[i] = new Player(gs.playerList[i]);
        }
        this.deck = new Deck(gs.deck);
        this.currentTurn = gs.currentTurn;
        this.currentTile = new Tile(gs.currentTile);
        this.isTileValid = gs.isTileValid;
        this.isPlacementStage = gs.isPlacementStage;

    }

    @Override
    public String toString(){
        return "hi";
    }

    //actions here


}
