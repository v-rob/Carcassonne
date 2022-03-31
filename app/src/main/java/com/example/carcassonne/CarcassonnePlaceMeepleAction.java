package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

/**
 * Action class that signifies that a player wishes to place a meeple on the current
 * tile. It contains the X and Y pixel position to place it at.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class CarcassonnePlaceMeepleAction extends GameAction {
    /** The X pixel position on the tile to place the meeple at. */
    private int x;
    /** The Y pixel position on the tile to place the meeple at. */
    private int y;

    /**
     * Constructs a new action for placing a meeple for the specified player.
     *
     * @param player The player performing the action.
     * @param x      The X pixel position on the tile to place the meeple at.
     * @param y      The Y pixel position on the tile to place the meeple at.
     */
    public CarcassonnePlaceMeepleAction(GamePlayer player, int x, int y) {
        super(player);
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the X pixel position on the tile to place the meeple at.
     *
     * @return The X pixel position.
     */
    int getX() {
        return this.x;
    }

    /**
     * Gets the Y pixel position on the tile to place the meeple at.
     *
     * @return The Y pixel position.
     */
    int getY() {
        return this.y;
    }
}
