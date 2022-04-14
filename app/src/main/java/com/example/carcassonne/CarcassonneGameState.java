package com.example.carcassonne;

import com.example.carcassonne.infoMsg.GameState;

import java.io.Serializable;
import java.util.Arrays;

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
public class CarcassonneGameState extends GameState implements Serializable {
    /** The maximum number of players that are allowed to play Carcassonne. */
    public static final int MAX_PLAYERS = 5;
    /** The number of meeples each player starts out with. */
    public static final int NUM_MEEPLES = 7;

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
    private boolean isTileStage;

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
        Arrays.fill(this.playerMeeples, NUM_MEEPLES);

        this.playerCompleteScores = new int[numPlayers];
        this.playerIncompleteScores = new int[numPlayers];

        this.currentPlayer = 0;
        this.isTileStage = false;
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
        this.isTileStage = other.isTileStage;
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
        toStr.add("isPlacementStage", this.isTileStage);
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
     * Gets the completed score of the specified player.
     *
     * @param player The player to get the completed score of.
     * @return The completed score of that player.
     */
    public int getPlayerCompleteScore(int player) {
        return this.playerCompleteScores[player];
    }

    /**
     * Gets the incomplete score of the specified player. It will be zero when the
     * game ends and added to the complete score.
     *
     * @param player The player to get the incomplete score of.
     * @return The incomplete score of that player.
     */
    public int getPlayerIncompleteScore(int player) {
        return this.playerIncompleteScores[player];
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
     * Returns whether the game is currently in the tile or meeple placement stage.
     *
     * @return True if in tile placement, false if in meeple placement.
     */
    public boolean isTileStage() {
        return this.isTileStage;
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
     * @return True if it is the tile placement stage and it is possible to place a tile
     *         at the specified position, false otherwise. If false, the game state
     *         does not change.
     */
    public boolean placeTile(int x, int y) {
        if (this.isTileStage && this.board.getConfirmedTile(x, y) == null) {
            this.board.getCurrentTile().setPosition(x, y);
            return true;
        }
        return false;
    }

    /**
     * Called when the player rotates a tile to a specified number of degrees.
     *
     * @param rotation The rotation to rotate the tile to.
     * @return True if it is the tile placement stage, false otherwise. If false,
     *         the game state does not change.
     */
    public boolean rotateTile(int rotation) {
        if (this.isTileStage) {
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
        if (this.isTileStage && this.board.isCurrentTilePlacementValid()) {
            this.isTileStage = false;
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
        if (!this.isTileStage) {
            this.board.getCurrentTile().removeMeeple();
            this.isTileStage = true;
            return true;
        }
        return false;
    }

    /**
     * Called when the player wishes to place a meeple on a tile on the specified
     * section.
     *
     * @param section The section to place the meeple on.
     * @return True if it is the meeple placement stage and the player has enough
     *         meeples, false otherwise. If false, the game state does not change.
     */
    public boolean placeMeeple(Section section) {
        // We can't place meeples in the tile placement stage.
        if (this.isTileStage) {
            return false;
        }

        // If we're placing a meeple (not removing it), ensure we have enough meeples.
        if (section != null && this.playerMeeples[this.currentPlayer] <= 0) {
            return false;
        }

        this.board.getCurrentTile().setMeepleSection(section);
        return true;
    }

    /**
     * Called when the player wishes to confirm their meeple placement, finishing
     * their turn, scoring the tile, and starting a new turn for the next player.
     *
     * @return True if it is the meeple placement stage and the meeple placement is
     *         valid, false otherwise. If false, the game state will not be changed.
     */
    public boolean confirmMeeple() {
        // Do nothing if it's invalid to confirm a meeple right now.
        if (this.isTileStage || !this.board.isCurrentMeeplePlacementValid()) {
            return false;
        }

        // Before we confirm and the current tile becomes null, subtract the meeple
        // if the player placed one.
        if (this.board.getCurrentTile().hasMeeple()) {
            this.playerMeeples[this.currentPlayer]--;
        }

        // Analyze the current tile for city/road scoring.
        MeepleAnalysis.analyzeTile(this.board, this.board.getCurrentTile(), (analysis) -> {
            int type = analysis.getStartSection().getType();
            if (type != Tile.TYPE_CITY && type != Tile.TYPE_ROAD) {
                return;
            }

            /* If this section is a road or city, check if it was just now completed
             * by this last placement. If it is, score it and return the relevant meeples.
             * Since it is closed off, there's no possibility of scoring it again.
             */
            if (analysis.isComplete()) {
                analysis.tallyScores(this.playerCompleteScores);
                analysis.returnMeeples(this.playerMeeples);
            }
        });

        // Analyze the board for cloister scoring. This must analyze the entire board
        // because a cloister may be completed by placing any adjacent tile.
        MeepleAnalysis.analyzeBoard(this.board, (analysis) -> {
            if (analysis.getStartSection().getType() != Tile.TYPE_CLOISTER) {
                return;
            }

            /* If this section is a cloister and it is complete, score it and return
             * the relevant meeples. Note that this code will run every time a tile
             * is placed, not just when the cloister is completed; this is fine because
             * all meeples will be removed from cloisters when scoring, so there's no
             * possibility of scoring them twice.
             */
            if (analysis.isComplete()) {
                analysis.tallyScores(this.playerCompleteScores);
                analysis.returnMeeples(this.playerMeeples);
            }
        });

        // Clear the incomplete score since we re-tally them all from scratch.
        Arrays.fill(this.playerIncompleteScores, 0);

        // Analyze the entire board for incomplete meeple scoring.
        MeepleAnalysis.analyzeBoard(this.board, (analysis) -> {
            // If this section is not complete, add it to the incomplete scores. Do not
            // score complete sections because that will result in doubly counted scores.
            if (!analysis.isComplete()) {
                analysis.tallyScores(this.playerIncompleteScores);
            }
        });

        // Confirm the tile and start a new turn.
        this.board.confirmCurrentTile();
        newTurn((currentPlayer + 1) % this.numPlayers);

        return true;
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
        this.isTileStage = true;

        // Keep drawing tiles until the deck is empty. If it is, set the game as over
        // and break. CarcassonneLocalGame will detect this and end the game.
        while (true) {
            if (this.deck.isEmpty()) {
                /* The deck is now empty, so the game is about to end. Add incomplete
                 * scores to the final complete scores. Note that there is no separate
                 * farm scoring stage: farms are counted in the incomplete scores, so
                 * they are accounted for here. Also clear the incomplete scores now
                 * that the game is complete.
                 */
                for (int i = 0; i < this.numPlayers; i++) {
                    this.playerCompleteScores[i] += this.playerIncompleteScores[i];
                    this.playerIncompleteScores[i] = 0;
                }

                // Now the game really is over. Thanks for playing!
                this.isGameOver = true;
                break;
            }

            // Draw a tile and make it current so we can check its validity.
            this.board.setCurrentTile(this.deck.drawTile(this.currentPlayer));

            // If there is a valid tile placement for this tile, break since we've found
            // our tile. Otherwise, continue to draw tiles.
            if (this.board.getValidTilePlacements().size() != 0) {
                break;
            }
        }
    }
}
