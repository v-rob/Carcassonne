package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

/**
 * Action class that signifies that a player wishes to confirm their current
 * meeple placement.
 *
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
 */
public class CarcassonneConfirmMeepleAction extends GameAction {
    /**
     * Constructs a new action for confirming the meeple for the specified player.
     *
     * @param player The player performing the action.
     */
    public CarcassonneConfirmMeepleAction(GamePlayer player) {
        super(player);
    }
}
