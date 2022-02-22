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

    public GameState(GameState gs){

    }

    @Override
    public String toString(){
        return "hi";
    }

    //actions here


}
