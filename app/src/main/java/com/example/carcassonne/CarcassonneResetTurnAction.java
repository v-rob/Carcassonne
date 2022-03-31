package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

/**
 * Action class that signifies that a player wishes to reset their turn back to tile
 * placing mode so they can choose a different position to place the tile at.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class CarcassonneResetTurnAction extends GameAction {
    /**
     * Constructs a new action for resetting the turn for the specified player.
     *
     * @param player The player performing the action.
     */
    public CarcassonneResetTurnAction(GamePlayer player) {
        super(player);
    }
}
