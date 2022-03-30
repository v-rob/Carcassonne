package com.example.carcassonne;

import com.example.carcassonne.infoMsg.GameState;

/**
 * Represents the game state of Carcassonne that includes instance variables
 * that displays all the information of the current state of the game to help
 * the human user or the computer player to make decisions
 *
 * @author DJ Backus
 * @author Sophie Arcangel
 * @author Alex Martinez-Lopez
 * @author Cheyanne Yim,
 * @author Vincent Robinson
 */
public class CarcassonneGameState extends GameState {
    public static final int MAX_PLAYERS = 5;
    public static final int STARTING_MEEPLES = 7;

    private int numPlayers;

    private int[] playerMeeples;
    private int[] playerCompleteScores;
    private int[] playerIncompleteScores;

    private int currentPlayer;
    private boolean isPlacementStage;

    private Deck deck;
    private Board board;

    /**
     * Creates a new game state given the number of players the game has.
     *
     * @param numPlayers The number of players the game has.
     */
    public CarcassonneGameState(int numPlayers) {
        this.numPlayers = numPlayers;

        this.playerMeeples = new int[numPlayers];
        for (int i = 0; i < this.playerMeeples.length; i++) {
            this.playerMeeples[i] = STARTING_MEEPLES;
        }

        this.playerCompleteScores = new int[numPlayers];
        this.playerIncompleteScores = new int[numPlayers];

        this.deck = new Deck();
        this.board = new Board(this.deck.drawStartingTile());

        // Finish setting things up by starting a new turn.
        newTurn(0);
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

        this.currentPlayer = other.currentPlayer;
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
        toStr.add("currentTurn", this.currentPlayer);
        toStr.add("isPlacementStage", this.isPlacementStage);
        toStr.add("deck", this.deck);
        toStr.add("board", this.board);

        return toStr.toString();
    }

    public int getNumPlayers() {
        return this.numPlayers;
    }

    public int getPlayerMeeples(int player) {
        return this.playerMeeples[player];
    }

    public int getPlayerCompleteScore(int player) {
        return this.playerCompleteScores[player];
    }

    public int getPlayerIncompleteScore(int player) {
        return this.playerIncompleteScores[player];
    }

    public int getPlayerScore(int player) {
        return this.getPlayerCompleteScore(player) + this.getPlayerIncompleteScore(player);
    }

    public int getCurrentPlayer() {
        return this.currentPlayer;
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
     * Returns the Board object used by this game state.
     *
     * @return The board.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Returns the Deck object used by this game state.
     *
     * @return The deck.
     */
    public Deck getDeck(){
        return this.deck;
    }

    public void newTurn(int newPlayer) {
        this.currentPlayer = newPlayer;
        this.isPlacementStage = true;

        // Draw a new tile if the deck is not empty. If it is empty, the game will be
        // over when LocalGame checks for the emptiness of the deck.
        while (!this.deck.isEmpty()) {
            this.board.setCurrentTile(this.deck.drawTile(this.currentPlayer));

            // If there is a valid tile placement for this tile, break since we've found
            // our tile. Otherwise, continue to draw tiles.
            if (this.board.getValidTilePlacements().size() != 0) {
                break;
            }
        }
    }

    /**
     * Determine whether or not to quit the game
     *
     * @return true if the game was quit and false otherwise
     */
    public boolean quitGame() {
        /*
         * External Citation
         * Date: 28 March 2022
         * Problem: Didn't know how to EXIT_ON_CLOSE from outside main activity, and JFrame
         * Resource:
         *     https://www.codegrepper.com/code-examples/java/quit+android+app+programmatically
         * Solution: We found a method call that uses the action to close the app that
         *     we decided to implement
         */
        android.os.Process.killProcess(android.os.Process.myPid());
        return true;
    }

    /**
     * All actions follow a similar format, first checking if it is the turn
     * of the player taking that action.
     *
     * @return true if player places a tile and false otherwise
     */
    public boolean placeTile(int x, int y) {
        if (isPlacementStage) {
            this.board.setCurrentTilePosition(x,y);
            return true;
        }
        return false;
    }

    /**
     * Determine if the player wants to rotate the current tile
     *
     * @return true if the tile is rotated and false otherwise
     */
    public boolean rotateTile() {
        if (isPlacementStage) {
            this.board.getCurrentTile().rotate();
            return true;
        }
        return false;
    }

    /**
     * Determine if the player confirmed where they wanted to place the tile
     *
     * @return if the player confirmed the tile and if the tile placement is legal. False otherwise
     */
    public boolean confirmTile() {
        if (isPlacementStage && board.isCurrentTilePlacementValid()) {
            this.isPlacementStage = false;
            return true;
        }
        return false;
    }

    /**
     * Determine whether or not the user wants to reset their turn to move the tile again
     *
     * @return true if player resets on current turn and false otherwise
     */
    public boolean resetTurn() {
        this.board.getCurrentTile().removeMeeple();
        this.isPlacementStage = true;
        return true;
    }

    /**
     * Determine if the player wants to place a Meeple on the tile that was just placed
     *
     * @return true if Meeple is placed and false otherwise
     */
    public boolean placeMeeple(int x, int y) {
        if (!isPlacementStage && this.playerMeeples[this.currentPlayer] > 0) {
            this.board.getCurrentTile().setMeeple(x, y);
            return true;
        }
        return false;
    }

    /**
     * Determine if the current player confirmed if they wanted to place a Meeple on the
     * tile and where the player wanted to place
     *
     * @return true if the player placed a Meeple and if the placement is valid. False otherwise.
     */
    public boolean confirmMeeple() {
        if (!isPlacementStage && board.isCurrentMeeplePlacementValid()) {
            // Before we confirm and the current tile becomes null, subtract the meeple
            // if the player placed one.
            if (this.board.getCurrentTile().hasMeeple()) {
                this.playerMeeples[this.currentPlayer]--;
            }

            this.board.confirmCurrentTile();

            newTurn((currentPlayer + 1) % this.numPlayers);

            return true;
        }
        return false;
    }
}
