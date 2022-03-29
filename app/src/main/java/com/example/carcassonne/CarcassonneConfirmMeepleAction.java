package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

/**
 * Creates actions that allows a user to confirm the placement of their meeple.
 *
 * @author Cheyanne Yim
 */

public class CarcassonneConfirmMeepleAction extends GameAction {
    public CarcassonneConfirmMeepleAction(GamePlayer player) {
        super(player);
    }
}
