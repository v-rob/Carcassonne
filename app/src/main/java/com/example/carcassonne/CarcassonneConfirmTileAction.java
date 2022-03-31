package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

/**
 * Action class that signifies that a player wishes to confirm their current tile
 * placement.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class CarcassonneConfirmTileAction extends GameAction {
    /**
     * Constructs a new action for confirming the tile for the specified player.
     *
     * @param player The player performing the action.
     */
    public CarcassonneConfirmTileAction(GamePlayer player) {
        super(player);
    }
}
