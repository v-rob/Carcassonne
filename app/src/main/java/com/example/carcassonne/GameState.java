package com.example.carcassonne;

import java.util.ArrayList;

/**
 * Represents the game state of Carcassonne that includes instance variables
 * that displays all the information of the current state of the game to help
 * the human user or the computer player to make decisions
 *
 * @author DJ Backus, Sophie Arcangel, Vincent Robinson, Alex Martinez-Lopez, Cheyanne Yim
 */

public class GameState {
    private ArrayList<Player> playerList;

    private int currentTurn;
    private boolean isPlacementStage;

    private Deck deck;
    private Board board;

    public GameState(ArrayList<Player> players) {
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

    public boolean quitGame(){
        return false;
    }

    public boolean resetTurn(Player p){
        if(playerList.indexOf(p) == currentTurn ){
            return true;
        }
        return false;
    }

    public boolean placeMeeple(Player p){
        if(!isPlacementStage && playerList.indexOf(p) == currentTurn ){
            return true;
        }
        return false;
    }

    public boolean rotateTile(Player p){
        if(isPlacementStage && playerList.indexOf(p) == currentTurn ){
            return true;
        }
        return false;
    }

    public boolean confirmTile(Player p){
        if(isPlacementStage && playerList.indexOf(p) == currentTurn ){
            //if(tile placement is legal)
            return true;
        }
        return false;
    }

    public boolean confirmMeeple(Player p){
        if(!isPlacementStage && playerList.indexOf(p) == currentTurn ){
            //if(meeple placement is legal)
            currentTurn++;
            return true;
        }
        return false;
    }

}
