package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

/**
 * Represents the place tile action a player can take in Carcassonne.
 * Uses x,y to represent desired position.
 *
 * @author Cheyanne
 */

public class CarcassonnePlaceTileAction extends GameAction {
    private int x;
    private int y;

    /**
     * Creates action to be sent to CarcassonneGameState
     *
     * @param player player taking action
     * @param x x position for tile placement
     * @param y y position for tile placement
     */

    public CarcassonnePlaceTileAction(GamePlayer player, int x, int y) {
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
