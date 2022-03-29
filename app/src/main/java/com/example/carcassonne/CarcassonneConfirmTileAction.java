package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

/**
 * Creates action that allows user to confirm the placement of the current tile.
 *
 * @author Cheyanne Yim
 */

public class CarcassonneConfirmTileAction extends GameAction {
    public CarcassonneConfirmTileAction(GamePlayer player) {
        super(player);
    }
}
