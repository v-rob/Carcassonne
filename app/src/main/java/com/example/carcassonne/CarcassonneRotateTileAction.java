package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

/**
 * Action class that signifies that a player wishes to rotate the current tile.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class CarcassonneRotateTileAction extends GameAction {
    /** The new rotation, in degrees, of the tile. */
    private int rotation;

    /**
     * Constructs a new action for rotating the current tile for the specified player.
     *
     * @param player   The player performing the action.
     * @param rotation The new rotation to give the tile in degrees. It must be
     *                 a multiple of 90 and be in the range 0-270.
     */
    public CarcassonneRotateTileAction(GamePlayer player, int rotation) {
        super(player);
        this.rotation = rotation;
    }

    /**
     * Gets the new rotation that the current tile should be rotated to.
     *
     * @return The rotation of the tile in degrees. It will be a multiple of 90 and
     *         in the range 0-270.
     */
    public int getRotation() {
        return this.rotation;
    }
}
