package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

/**
 * Action class that signifies that a player wishes to place the current tile at
 * some position on the board. It contains the X and Y board position to place it at.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class CarcassonnePlaceTileAction extends GameAction {
    /** The X position on the board to place the tile at. */
    private int x;
    /** The Y position on the board to place the tile at. */
    private int y;

    /**
     * Constructs a new action for placing a tile for the specified player.
     *
     * @param player The player performing the action.
     * @param x      The X board position on the tile to place the meeple at.
     * @param y      The Y board position on the tile to place the meeple at.
     */
    public CarcassonnePlaceTileAction(GamePlayer player, int x, int y) {
        super(player);
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the X position on the board to place the tile at.
     *
     * @return The X board position.
     */
    int getX() {
        return this.x;
    }

    /**
     * Gets the Y position on the board to place the tile at.
     *
     * @return The Y board position.
     */
    int getY() {
        return this.y;
    }
}
