package com.example.carcassonne;

import com.example.carcassonne.infoMsg.GameInfo;
import java.util.ArrayList;

/**
 * Represents the Carcassonne AI computer player and contains all the logic for it to
 * make its moves in the game.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class CarcassonneComputerPlayer extends GameComputerPlayer {
    /**
     * The position and rotation of the tile that the AI has chosen to place its
     * tile for this move.
     */
    private Board.TilePlacement chosenPlacement;

    /**
     * It is impossible to send multiple actions in the same receiveInfo() because
     * it causes race conditions where the actions are sent in order, but the
     * updated game state is sent to receiveInfo() again, potentially causing the
     * placement to be re-chosen and actions to be resent. Thus, this member
     * tracks which action the AI should perform in the next receiveInfo().
     */
    private int nextAction;

    /**
     * Constant for nextAction: the AI should choose its tile and meeple placements
     * in this initial stage, and then start by rotating the current tile.
     */
    private static final int ROTATE_TILE = 0;
    /** Constant for nextAction: The AI should place the tile. */
    private static final int PLACE_TILE = 1;
    /** Constant for nextAction: The AI should confirm the tile. */
    private static final int CONFIRM_TILE = 2;
    /** Constant for nextAction: The AI should place its meeple, if it wants to. */
    private static final int PLACE_MEEPLE = 3;
    /** Constant for nextAction: The AI should confirm the meeple placement. */
    private static final int CONFIRM_MEEPLE = 4;

    /** Defines whether this is the smart AI or the dumb AI. */
    private boolean isSmart;

    /**
     * Creates a new computer player with the specified name and smartness.
     *
     * @param name    The name of the computer player.
     * @param isSmart True if the AI is to be smart, false otherwise.
     */
    public CarcassonneComputerPlayer(String name, boolean isSmart) {
        super(name);
        this.nextAction = ROTATE_TILE;
        this.isSmart = isSmart;
    }

    /**
     * The handler for receiving game state information: when it is this player's turn,
     * it handles the logic for choosing a position and rotation to place its tile at
     * and then sequentially sends each necessary action to the game state in each
     * consecutive call.
     *
     * @param info The game state to be received, or anything else for a no-op.
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        if (!(info instanceof CarcassonneGameState)) {
            // Do nothing if we don't receive a game state.
            return;
        }

        CarcassonneGameState gameState = (CarcassonneGameState)info;

        if (gameState.isGameOver() || gameState.getCurrentPlayer() != this.playerNum) {
            // Do nothing if it's not our turn or the game is over.
            return;
        }

        // Perform each action that needs to be sent sequentially in each call.
        switch (this.nextAction) {
            case ROTATE_TILE:
                // Sleep to give the illusion of thought.
                // TODO: Longer for smart AI, intersperse it
                sleep(1500);

                // Get a list of valid positions that we can place our tiles at.
                Board board = gameState.getBoard();
                ArrayList<Board.TilePlacement> placements = new ArrayList<>();

                // If we're smart and have enough meeples, place a meeple at random at
                // approximately even intervals throughout the game.
                if (this.isSmart && gameState.getPlayerMeeples(this.playerNum) > 0 &&
                        Math.random() < 1.0) {
                    placements = board.getValidMeeplePlacements();
                }

                // If we're dumb or there are no valid meeple placements, just choose a normal
                // tile to place at.
                if (placements.size() == 0) {
                    placements = board.getValidTilePlacements();
                }

                // Now choose our placement at random, regardless of whether there are meeples
                // on the tile or not.
                this.chosenPlacement = placements.get((int)(Math.random() * placements.size()));

                // Commence sending actions one after another, starting with rotation.
                this.game.sendAction(new CarcassonneRotateTileAction(this,
                        this.chosenPlacement.rotation));
                this.nextAction = PLACE_TILE;
                break;
            case PLACE_TILE:
                this.game.sendAction(new CarcassonnePlaceTileAction(this,
                        this.chosenPlacement.x, this.chosenPlacement.y));
                this.nextAction = CONFIRM_TILE;
                break;
            case CONFIRM_TILE:
                this.game.sendAction(new CarcassonneConfirmTileAction(this));
                this.nextAction = PLACE_MEEPLE;
                break;
            case PLACE_MEEPLE:
                // Send the meeple even if it's null so we get another game state, which will
                // trigger the next action.
                this.game.sendAction(new CarcassonnePlaceMeepleAction(this,
                        this.chosenPlacement.meepleSection));
                this.nextAction = CONFIRM_MEEPLE;
                break;
            case CONFIRM_MEEPLE:
                this.game.sendAction(new CarcassonneConfirmMeepleAction(this));

                // Our turn ends after we confirm the meeple, so set the action to ROTATE to
                // prepare for our next turn.
                this.nextAction = ROTATE_TILE;
                break;
        }
    }
}
