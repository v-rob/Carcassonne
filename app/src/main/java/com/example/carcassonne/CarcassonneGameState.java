package com.example.carcassonne;

import com.example.carcassonne.infoMsg.GameState;

/**
 * Represents the entire game state of Carcassonne, including the deck of tiles
 * and the current state of the board, the list of player meeples and scores, the
 * current player, and the current tile and its state.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class CarcassonneGameState extends GameState {
    /** The maximum number of players that are allowed to play Carcassonne. */
    public static final int MAX_PLAYERS = 5;
    /** The number of meeples each player starts out with. */
    public static final int STARTING_MEEPLES = 7;

    /** The number of players playing right now. */
    private int numPlayers;

    /** The number of meeples each player has. */
    private int[] playerMeeples;

    /**
     * The completed score of each player, i.e. the score of all their completed
     * cities, roads, and cloisters.
     */
    private int[] playerCompleteScores;
    /**
     * The incomplete score of each player, i.e. the score they would get for their
     * farms and incomplete cities, roads, and cloisters if the game were to end right
     * now.
     */
    private int[] playerIncompleteScores;

    /** The player index of the player whose turn it is. */
    private int currentPlayer;
    /** Whether the current player is currently placing tiles or meeples. */
    private boolean isPlacementStage;

    /** Whether the game is now over, after the last tile has been placed. */
    private boolean isGameOver;

    /** The deck of tiles used this game. */
    private Deck deck;
    /** The current state of the board and the current tile being played. */
    private Board board;

    /**
     * Creates a new game state given the number of players the game has by filling
     * out all instance variables to their defaults and starting a new turn for
     * player 0.
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

        this.currentPlayer = 0;
        this.isPlacementStage = false;
        this.isGameOver = false;

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
        this.isGameOver = other.isGameOver;

        this.deck = new Deck(other.deck);
        this.board = new Board(other.board);
    }

    /**
     * Converts the game state to a string representation showing all instance
     * variables.
     *
     * @return The string representation of the tile.
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

    /**
     * Gets the number of players playing the game right now.
     *
     * @return The number of players.
     */
    public int getNumPlayers() {
        return this.numPlayers;
    }

    /**
     * Gets the number of meeples the specified player has.
     *
     * @param player The player to get the meeple number of.
     * @return The number of meeples that player has.
     */
    public int getPlayerMeeples(int player) {
        return this.playerMeeples[player];
    }

    /**
     * Adds a certain number of meeples to the specified player's number of meeples.
     * Used in the Analysis classes in returnMeeples().
     *
     * @param player     The player to add meeples to.
     * @param numMeeples The number of meeples to add to that player's set of meeples.
     */
    public void addPlayerMeeples(int player, int numMeeples) {
        this.playerMeeples[player] += numMeeples;
    }

    /**
     * Gets the completed score of the specified player.
     *
     * @param player The player to get the completed score of.
     * @return The completed score of that player.
     */
    public int getPlayerCompleteScore(int player) {
        return this.playerCompleteScores[player];
    }

    /**
     * Gets the incomplete score of the specified player.
     *
     * @param player The player to get the incomplete score of.
     * @return The incomplete score of that player.
     */
    public int getPlayerIncompleteScore(int player) {
        return this.playerIncompleteScores[player];
    }

    /**
     * Gets the total score of the specified player, i.e. the sum of the complete
     * and incomplete scores.
     *
     * @param player The player to get the full score of.
     * @return The full score of that player.
     */
    public int getPlayerScore(int player) {
        return this.getPlayerCompleteScore(player) + this.getPlayerIncompleteScore(player);
    }

    /**
     * Gets the index of the player whose turn it currently is.
     *
     * @return The index of the current player.
     */
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
     * Returns the game is now over, after the last tile has been placed.
     *
     * @return True if the game is over, false if not.
     */
    public boolean isGameOver() {
        return this.isGameOver;
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
    public Deck getDeck() {
        return this.deck;
    }

    /**
     * Called when the player places a tile at some X and Y position on the board.
     *
     * @param x The X position to place the tile at.
     * @param y The Y position to place the tile at.
     * @return True if it is the tile placement stage and it is valid to place a tile
     *         at the specified position, false otherwise. If false, the game state
     *         does not change.
     */
    public boolean placeTile(int x, int y) {
        if (this.isPlacementStage && this.board.getConfirmedTile(x, y) == null) {
            this.board.setCurrentTilePosition(x, y);
            return true;
        }
        return false;
    }

    /**
     * Called when the player rotates a tile a specified number of degrees.
     *
     * @return True if it is the tile placement stage, false otherwise. If false,
     *         the game state does not change.
     */
    public boolean rotateTile(int rotation) {
        if (this.isPlacementStage) {
            this.board.getCurrentTile().setRotation(rotation);
            return true;
        }
        return false;
    }

    /**
     * Called when the player confirms their current tile placement and starts
     * the meeple placement stage.
     *
     * @return True if it is the tile placement stage and the current tile's position
     *         is valid, false otherwise. If false, the game state does not change.
     */
    public boolean confirmTile() {
        if (this.isPlacementStage && this.board.isCurrentTilePlacementValid()) {
            this.isPlacementStage = false;
            return true;
        }
        return false;
    }

    /**
     * Called when the player wishes to reset from meeple placement back to tile
     * placement, removing any meeples currently on the tile.
     *
     * @return True if it is the meeple placement stage, false otherwise. If false,
     *         the game state does not change.
     */
    public boolean resetTurn() {
        if (!this.isPlacementStage) {
            this.board.getCurrentTile().removeMeeple();
            this.isPlacementStage = true;
            return true;
        }
        return false;
    }

    /**
     * Called when the player wishes to place a meeple on a tile at the specified
     * X and Y pixel positions.
     *
     * @return True if it is the meeple placement stage and the player has enough
     *         meeples, false otherwise. If false, the game state does not change.
     */
    public boolean placeMeeple(int x, int y) {
        if (!this.isPlacementStage && this.playerMeeples[this.currentPlayer] > 0) {
            this.board.getCurrentTile().setMeeple(x, y);
            return true;
        }
        return false;
    }

    /**
     * Called when the player wishes to confirm their meeple placement, finishing
     * their turn and starting a new turn for the next player.
     *
     * @return True if it is the meeple placement stage and the meeple placement is
     *         valid, false otherwise. If false, the game state will not be changed.
     */
    public boolean confirmMeeple() {
        if (!this.isPlacementStage && this.board.isCurrentMeeplePlacementValid()) {
            // Before we confirm and the current tile becomes null, subtract the meeple
            // if the player placed one.
            if (this.board.getCurrentTile().hasMeeple()) {
                this.playerMeeples[this.currentPlayer]--;
            }

            // Confirm the tile and start a new turn.
            this.board.confirmCurrentTile();
            newTurn((currentPlayer + 1) % this.numPlayers);

            return true;
        }
        return false;
    }

    /**
     * Starts a new turn by setting the current player to the specified player, setting
     * the game to the placement stage, and drawing a new tile. If there is no valid
     * placement for the drawn tile, more will be drawn until a valid one is found.
     * If the deck is empty when trying to draw a tile, the game is over.
     *
     * @param newPlayer The index of the player whose turn it will be next.
     */
    private void newTurn(int newPlayer) {
        this.currentPlayer = newPlayer;
        this.isPlacementStage = true;

        // Keep drawing tiles until the deck is empty. If it is, set the game as over
        // and break. LocalGame will detect this and end the game.
        while (true) {
            if (this.deck.isEmpty()) {
                this.isGameOver = true;
                break;
            }

            this.board.setCurrentTile(this.deck.drawTile(this.currentPlayer));

            // If there is a valid tile placement for this tile, break since we've found
            // our tile. Otherwise, continue to draw tiles.
            if (this.board.getValidTilePlacements().size() != 0) {
                break;
            }
        }
    }
}
