package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

/**
 * This class represents the action of placing a Meeple in Carcassonne.
 * Uses the x,y positions to place the meeple in the correct spot on the tile.
 *
 * @author Cheyanne Yim
 */

public class CarcassonnePlaceMeepleAction extends GameAction {
    private int x;
    private int y;

    /**
     * Creates the action to be sent to GameState
     *
     * @param player the player taking the action
     * @param x x position on the tile
     * @param y y position on the tile
     */
    public CarcassonnePlaceMeepleAction(GamePlayer player, int x, int y) {
        super(player);
        this.x = x;
        this.y = y;
    }

    int getX() {
        return this.x;
    }

    int getY() {
        return this.y;
    }
}
