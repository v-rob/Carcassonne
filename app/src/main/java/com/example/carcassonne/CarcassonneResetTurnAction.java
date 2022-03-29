package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

/**
 * Creates action that undoes the placement of the current tile
 *
 * @author Cheyanne Yim
 */

public class CarcassonneResetTurnAction extends GameAction {
    public CarcassonneResetTurnAction(GamePlayer player) {
        super(player);
    }
}
