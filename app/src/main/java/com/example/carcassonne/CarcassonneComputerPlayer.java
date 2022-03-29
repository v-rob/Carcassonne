package com.example.carcassonne;

import com.example.carcassonne.infoMsg.GameInfo;

/**
 * Controls computer player in Carcassonne
 *
 * @author Cheyanne Yim
 * @author Sophie Arcangel
 * @author Vincent Robinson
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 */

public class CarcassonneComputerPlayer extends GameComputerPlayer {
    private CarcassonneGameState gameState;
    private boolean isSmart;

    public CarcassonneComputerPlayer(String name, boolean isSmart) {
        super(name);
        this.isSmart = isSmart;
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        this.gameState = (CarcassonneGameState)info;
    }
}
