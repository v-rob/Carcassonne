package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

import java.util.ArrayList;

/**
 * The local game class of Carcassonne; handles the sending and receiving of
 * actions to and from the master game state and the players.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class CarcassonneLocalGame extends LocalGame {
    /** The master game state of which all player game states are copies. */
    private CarcassonneGameState gameState;

    /**
     * Sends the updated game state to the specified player. If the master game
     * state does not exist yet, it is created.
     *
     * @param player The player to send the game state to.
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer player) {
        /* The reason why the game state must be created here and not in a LocalGame
         * constructor is that the game state needs to know how many players there
         * are at construction, but the LocalGame.players array is not filled out at
         * that point, so there's no way to tell how many players there are.
         *
         * Since this method must be called at the very beginning of the game to give
         * each player an initial game state, creating the game state here when it's
         * first needed is fine.
         */
        if (this.gameState == null) {
            this.gameState = new CarcassonneGameState(this.players.length);
        }

        player.sendInfo(new CarcassonneGameState(this.gameState));
    }

    /**
     * Returns whether the specified player is allowed to move, i.e. if the specified
     * player index matches the current player in the game state.
     *
     * @param player The player to check the mobility of.
     * @return True if the player can move, false otherwise.
     */
    @Override
    protected boolean canMove(int player) {
        return this.gameState.getCurrentPlayer() == player;
    }

    /**
     * Checks if the game is now over, which is when the deck has run out of tiles, and
     * return a string containing the message indicating that the game is over and the
     * winner (or list of winners if there's a tie).
     *
     * @return The string to display when the game is over.
     */
    @Override
    protected String checkIfGameOver() {
        // Do nothing if the game isn't over.
        if (!this.gameState.getDeck().isEmpty()) {
            return null;
        }

        // Compute a list of player(s) that tied for the highest amount of total
        // points, including incomplete points and final farm scoring.
        int highestScore = 0;
        ArrayList<String> highestNames = new ArrayList<>();

        for (int i = 0; i < this.gameState.getNumPlayers(); i++) {
            int score = this.gameState.getPlayerScore(i);
            if (score > highestScore) {
                // If we found a higher score, update the highest score, clear the
                // list, and add the player.
                highestScore = score;

                highestNames.clear();
                highestNames.add(this.playerNames[i]);
            } else if (score >= highestScore) {
                // If this player is tied for highest, add this player as well.
                highestNames.add(this.playerNames[i]);
            }
        }

        // Concatenate the list of names with commas.
        StringBuilder names = new StringBuilder("The winners are ");
        for (int i = 0; i < highestNames.size(); i++) {
            names.append(highestNames.get(i));

            if (i < highestNames.size() - 1) {
                names.append(", ");
            }
        }

        return names.toString();
    }

    /**
     * Receives an action from a player and updates the master game state accordingly
     * assuming the action is valid at this time and state.
     *
     * @param action The action send from the player.
     * @return True if the action is valid, false otherwise.
     */
    @Override
    protected boolean makeMove(GameAction action) {
        if (action instanceof CarcassonnePlaceTileAction) {
            CarcassonnePlaceTileAction placeTileAction = (CarcassonnePlaceTileAction)action;
            return this.gameState.placeTile(placeTileAction.getX(), placeTileAction.getY());
        } else if (action instanceof CarcassonneRotateTileAction) {
            CarcassonneRotateTileAction rotateTileAction = (CarcassonneRotateTileAction)action;
            return this.gameState.rotateTile(rotateTileAction.getRotation());
        } else if (action instanceof CarcassonneConfirmTileAction) {
            return this.gameState.confirmTile();
        } else if (action instanceof CarcassonneResetTurnAction) {
            return this.gameState.resetTurn();
        } else if (action instanceof CarcassonnePlaceMeepleAction) {
            CarcassonnePlaceMeepleAction placeMeepleAction = (CarcassonnePlaceMeepleAction)action;
            return this.gameState.placeMeeple(placeMeepleAction.getX(), placeMeepleAction.getY());
        } else if (action instanceof CarcassonneConfirmMeepleAction) {
            return this.gameState.confirmMeeple();
        }

        // Any other action is invalid automatically.
        return false;
    }
}
