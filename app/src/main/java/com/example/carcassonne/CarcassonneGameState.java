package com.example.carcassonne;

import java.util.ArrayList;

/**
 * Represents the game state of Carcassonne that includes instance variables
 * that displays all the information of the current state of the game to help
 * the human user or the computer player to make decisions
 *
 * @author DJ Backus, Sophie Arcangel, Vincent Robinson, Alex Martinez-Lopez, Cheyanne Yim
 */

public class CarcassonneGameState {
    private ArrayList<Player> playerList;

    private int currentTurn;
    private boolean isPlacementStage;

    private Deck deck;
    private Board board;

    public CarcassonneGameState(ArrayList<Player> players) {
        this.playerList = players;

        this.currentTurn = 0;
        this.isPlacementStage = true;

        this.deck = new Deck();
        this.board = new Board(this.deck.drawStartingTile());
    }

    // Copy constructor for CarcassonneGameState constructor

    /*
     * External Citation
     * Date: 21 February 2022
     * Problem: Didn't know how to make a deep copy
     * Resource:
     *     https://www.javainterviewpoint.com/copy-constructor/
     * Solution: We used this website to learn how to deep copy
     *     and used it as an example for our code
     */
    public CarcassonneGameState(CarcassonneGameState gs) {
        this.playerList = new ArrayList<Player>();
        for (int i = 0; i < this.playerList.size(); i++) {
            Player x = new Player(gs.playerList.get(i));
            this.playerList.add(x);
        }

        this.currentTurn = gs.currentTurn;
        this.isPlacementStage = gs.isPlacementStage;

        this.deck = new Deck(gs.deck);
        this.board = new Board(gs.board);
    }

    /**
     *Prints information on all instance variables
     *
     * @return a String with all relevant information on the state of the game
     */
    @Override
    public String toString() {
        String allPlayers = null;
        for(int i = 0; i < playerList.size(); i++){
            allPlayers = playerList.get(i) + " ";
        }
        return "Current Player Turn: " + currentTurn + " Is it the placement stage? "
                + isPlacementStage + " Player List: " +  allPlayers + " Board: " + board
                + " Deck: " + deck;
    }

    //ACTIONS
    /**
     * All actions follow a similar format, first checking if it is the turn
     * of the player taking that action. The one exception is quit, as
     * it can be taken at any time.
     */
    public boolean placeTile(Player p){
        if(isPlacementStage && playerList.indexOf(p) == currentTurn ){
            return true;
        }
        return false;
    }

    /**
     * Determine whether or not to quit the game
     * @return true if the user wants to quit game and false otherwise
     */
    public boolean quitGame(){
        return false;
    }

    /**
     * Determine whether or not the user wants to reset their turn to move the tile again
     * @param p the name of the player
     * @return true if player resets on current turn and false otherwise
     */
    public boolean resetTurn(Player p){
        if(playerList.indexOf(p) == currentTurn ){
            return true;
        }
        return false;
    }

    /**
     * Determine if the player wants to place a Meeple on the tile that was just placed
     * @param p name of the player
     * @return true if Meeple is placed and false otherwise
     */
    public boolean placeMeeple(Player p){
        if(!isPlacementStage && playerList.indexOf(p) == currentTurn ){
            return true;
        }
        return false;
    }


    /**
     * Determine if the player wants to rotate the current tile
     * @param p name of the player
     * @return true if the tile is rotated and false otherwise
     */
    public boolean rotateTile(Player p){
        if(isPlacementStage && playerList.indexOf(p) == currentTurn ){
            return true;
        }
        return false;
    }

    /**
     * Determine if the player confirmed where they wanted to place the tile
     * @param p name of the player
     * @return if the player confirmed the tile and if the tile placement is legal. False otherwise
     */
    public boolean confirmTile(Player p){
        if(isPlacementStage && playerList.indexOf(p) == currentTurn ){
            //if(tile placement is legal)
            return true;
        }
        return false;
    }

    /**
     * Determine if the current player confirmed if they wanted to place a Meeple on the
     * tile and where the player wanted to place
     * @param p name of the player
     * @return true if the player placed a Meeple and if the placement is valid. False otherwise.
     */
    public boolean confirmMeeple(Player p){
        if(!isPlacementStage && playerList.indexOf(p) == currentTurn ){
            //if(meeple placement is legal)
            currentTurn++;
            return true;
        }
        return false;
    }

}
