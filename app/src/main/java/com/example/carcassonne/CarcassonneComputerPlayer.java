package com.example.carcassonne;

import com.example.carcassonne.infoMsg.GameInfo;
import java.util.ArrayList;

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
    //private boolean isSmart;

    public CarcassonneComputerPlayer(String name, boolean isSmart) {
        super(name);
        //this.isSmart = isSmart;
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        if(info instanceof CarcassonneGameState){
            this.gameState = (CarcassonneGameState)info;
            if(gameState.getCurrentPlayer() == this.playerNum) {
                    sleep(1500);
                    ArrayList<Board.TilePlacement> placements = this.gameState.getBoard().getValidTilePlacements();
                    int moves = (int) (Math.random() * placements.size());
                    game.sendAction(new CarcassonneRotateTileAction(this, placements.get(moves).rotation));
                    game.sendAction(new CarcassonnePlaceTileAction(this, placements.get(moves).x, placements.get(moves).y));
                    game.sendAction(new CarcassonneConfirmTileAction(this));
                    game.sendAction(new CarcassonneConfirmMeepleAction(this));

            }
        }
        else{
            return;
        }

    }
}
