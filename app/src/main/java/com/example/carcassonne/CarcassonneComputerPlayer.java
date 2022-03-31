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
     * Constant for nextAction: the AI should choose a position and then rotate the
     * current tile.
     */
    private static final int ROTATE = 0;
    /** Constant for nextAction: The AI should place the tile. */
    private static final int PLACE = 1;
    /** Constant for nextAction: The AI should confirm the tile. */
    private static final int CONFIRM_TILE = 2;
    /** Constant for nextAction: The AI should confirm the meeple placement. */
    private static final int CONFIRM_MEEPLE = 3;

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
        this.nextAction = ROTATE;
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

        if (gameState.getCurrentPlayer() != this.playerNum) {
            // Do nothing if it's not our turn.
            return;
        }

        // Perform each action that needs to be sent sequentially in each call.
        switch (this.nextAction) {
            case ROTATE:
                // Sleep to give the illusion of thought.
                sleep(1500);

                // Get a list of valid positions that we can place our tiles at and choose
                // a random one to place the tile at.
                Board board = gameState.getBoard();
                ArrayList<Board.TilePlacement> placements = board.getValidTilePlacements();
                this.chosenPlacement = placements.get((int)(Math.random() * placements.size()));

                // Commence sending actions one after another: rotation, place the tile, confirm
                // it, and confirm the meeple.
                this.game.sendAction(new CarcassonneRotateTileAction(this, this.chosenPlacement.rotation));
                nextAction = PLACE;
                break;
            case PLACE:
                this.game.sendAction(new CarcassonnePlaceTileAction(this, this.chosenPlacement.x, this.chosenPlacement.y));
                this.nextAction = CONFIRM_TILE;
                break;
            case CONFIRM_TILE:
                this.game.sendAction(new CarcassonneConfirmTileAction(this));
                this.nextAction = CONFIRM_MEEPLE;
                break;
            case CONFIRM_MEEPLE:
                this.game.sendAction(new CarcassonneConfirmMeepleAction(this));

                // Our turn ends after we confirm the meeple, so set the action to ROTATE to
                // prepare for our next turn.
                this.nextAction = ROTATE;
                break;
        }
    }
}
