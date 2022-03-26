package com.example.carcassonne;

import com.example.carcassonne.infoMsg.GameState;

/**
 * Represents the game state of Carcassonne that includes instance variables
 * that displays all the information of the current state of the game to help
 * the human user or the computer player to make decisions
 *
 * @author DJ Backus, Sophie Arcangel, Alex Martinez-Lopez, Cheyanne Yim,
 * Vincent Robinson
 */
public class CarcassonneGameState extends GameState {
    public static final int MAX_PLAYERS = 5;

    private int numPlayers;

    private int[] playerMeeples;
    private int[] playerCompleteScores;
    private int[] playerIncompleteScores;

    private int currentTurn;
    private boolean isPlacementStage;

    private Deck deck;
    private Board board;

    /**
     * Creates a new game state given the number of players the game has.
     *
     * @param numPlayers The number of players the game has.
     */
    public CarcassonneGameState(int numPlayers, BitmapProvider bitmapProvider) {
        this.numPlayers = numPlayers;

        this.playerMeeples = new int[numPlayers];
        this.playerCompleteScores = new int[numPlayers];
        this.playerIncompleteScores = new int[numPlayers];

        this.currentTurn = 0;
        this.isPlacementStage = true;

        this.deck = new Deck(bitmapProvider);
        this.board = new Board(this.deck.drawStartingTile());
    }

    /*
     * External Citation
     * Date: 21 February 2022
     * Problem: Didn't know how to make a deep copy
     * Resource:
     *     https://www.javainterviewpoint.com/copy-constructor/
     * Solution: We used this website to learn how to deep copy
     *     and used it as an example for our code
     */

    /**
     * Makes a deep copy of the game state and all its instance variables.
     *
     * @param other The game state to make a deep copy of.
     */
    public CarcassonneGameState(CarcassonneGameState other) {
        this.numPlayers = other.numPlayers;

        this.playerMeeples = Util.copyArray(other.playerMeeples);
        this.playerCompleteScores = Util.copyArray(other.playerCompleteScores);
        this.playerIncompleteScores = Util.copyArray(other.playerIncompleteScores);

        this.currentTurn = other.currentTurn;
        this.isPlacementStage = other.isPlacementStage;

        this.deck = new Deck(other.deck);
        this.board = new Board(other.board);
    }

    /**
     * Prints information on all instance variables
     *
     * @return a String with all relevant information on the state of the game
     */
    @Override
    public String toString() {
        ToStringer toStr = new ToStringer("CarcassonneGameState");

        toStr.add("numPlayers", this.numPlayers);
        toStr.add("playerMeeples", this.playerMeeples);
        toStr.add("playerCompleteScores", this.playerCompleteScores);
        toStr.add("playerIncompleteScores", this.playerIncompleteScores);
        toStr.add("currentTurn", this.currentTurn);
        toStr.add("isPlacementStage", this.isPlacementStage);
        toStr.add("deck", this.deck);
        toStr.add("board", this.board);

        return toStr.toString();
    }

    /**
     * Returns the Board object used by this game state.
     *
     * @return The board.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Returns whether the game is currently in the tile or meeple placement stage
     *
     * @return True if in tile placement, false if in meeple placement.
     */
    public boolean isPlacementStage() {
        return this.isPlacementStage;
    }

    /**
     * All actions follow a similar format, first checking if it is the turn
     * of the player taking that action.
     *
     * @param p the index of the player
     * @return true if player places a tile and false otherwise
     */
    public boolean placeTile(int p) {
        if (isPlacementStage && p == currentTurn) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether or not to quit the game
     *
     * @param p the index of the player
     * @return true if the game was quit and false otherwise
     */
    public boolean quitGame(int p) {
        if (p == currentTurn) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether or not the user wants to reset their turn to move the tile again
     *
     * @param p the index of the player
     * @return true if player resets on current turn and false otherwise
     */
    public boolean resetTurn(int p) {
        if (p == currentTurn) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the player wants to place a Meeple on the tile that was just placed
     *
     * @param p index of the player
     * @return true if Meeple is placed and false otherwise
     */
    public boolean placeMeeple(int p) {
        if (!isPlacementStage && p == currentTurn) {
            return true;
        }
        return false;
    }


    /**
     * Determine if the player wants to rotate the current tile
     *
     * @param p index of the player
     * @return true if the tile is rotated and false otherwise
     */
    public boolean rotateTile(int p) {
        if (isPlacementStage && p == currentTurn) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the player confirmed where they wanted to place the tile
     *
     * @param p index of the player
     * @return if the player confirmed the tile and if the tile placement is legal. False otherwise
     */
    public boolean confirmTile(int p) {
        if (isPlacementStage && p == currentTurn && board.isCurrentTilePlacementValid()) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the current player confirmed if they wanted to place a Meeple on the
     * tile and where the player wanted to place
     *
     * @param p index of the player
     * @return true if the player placed a Meeple and if the placement is valid. False otherwise.
     */
    public boolean confirmMeeple(int p) {
        if (!isPlacementStage && p == currentTurn && board.isCurrentMeeplePlacementValid()) {
            currentTurn = (currentTurn + 1) % this.numPlayers;
            return true;
        }
        return false;
    }
}
