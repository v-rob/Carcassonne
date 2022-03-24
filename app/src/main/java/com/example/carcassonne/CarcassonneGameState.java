package com.example.carcassonne;

/**
 * Represents the game state of Carcassonne that includes instance variables
 * that displays all the information of the current state of the game to help
 * the human user or the computer player to make decisions
 *
 * @author DJ Backus, Sophie Arcangel, Alex Martinez-Lopez, Cheyanne Yim,
 * Vincent Robinson
 */
public class CarcassonneGameState {
    private Player[] playerList;

    private int currentTurn;
    private boolean isPlacementStage;

    private Deck deck;
    private Board board;

    /**
     * Creates a new game state given the number of players the game has.
     *
     * @param numPlayers The number of players the game has.
     */
    public CarcassonneGameState(int numPlayers) {
        this.playerList = new Player[numPlayers];
        for (int i = 0; i < this.playerList.length; i++) {
            this.playerList[i] = new Player();
        }

        this.currentTurn = 0;
        this.isPlacementStage = true;

        this.deck = new Deck();
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
     * @param gs The game state to make a deep copy of.
     */
    public CarcassonneGameState(CarcassonneGameState gs) {
        this.playerList = new Player[gs.playerList.length];
        for (int i = 0; i < this.playerList.length; i++) {
            this.playerList[i] = new Player(gs.playerList[i]);
        }

        this.currentTurn = gs.currentTurn;
        this.isPlacementStage = gs.isPlacementStage;

        this.deck = new Deck(gs.deck);
        this.board = new Board(gs.board);
    }

    /**
     * Prints information on all instance variables
     *
     * @return a String with all relevant information on the state of the game
     */
    @Override
    public String toString() {
        String str = "CarcassonneGameState {\n" +
                "    playerList = {";

        for (int i = 0; i < this.playerList.length; i++) {
            str += "        " + Util.indent(this.playerList[i].toString()) + "\n";
        }

        str += "    currentTurn = " + this.currentTurn + "\n" +
                "    isPlacementStage = " + this.isPlacementStage + "\n" +
                Util.indent(this.deck.toString()) + "\n" +
                Util.indent(this.board.toString()) + "\n}";

        return str;
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
            currentTurn = (currentTurn + 1) % this.playerList.length;
            return true;
        }
        return false;
    }
}
