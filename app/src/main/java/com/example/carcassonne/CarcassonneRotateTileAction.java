package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

/**
 * Creates the action for a user to rotate current tile 90 degrees
 *
 * @author Cheyanne Yim
 */

public class CarcassonneRotateTileAction extends GameAction {
    private int rotation;
    public CarcassonneRotateTileAction(GamePlayer player, int rotate) {
        super(player);
        rotation = rotate;
    }

    public int getRotation(){
        return rotation;
    }


}
