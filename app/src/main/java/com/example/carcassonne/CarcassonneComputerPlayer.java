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
    private Board.TilePlacement chosenTile;
    private int nextAction;
    private static final int ROTATE = 0;
    private static final int PLACE = 1;
    private static final int CONFIRM_TILE = 2;
    private static final int CONFIRM_MEEPLE = 3;
    //private boolean isSmart;

    public CarcassonneComputerPlayer(String name, boolean isSmart) {
        super(name);
        nextAction = ROTATE;

        //this.isSmart = isSmart;
    }

    @Override
    protected void receiveInfo(GameInfo info) {

        if(info instanceof CarcassonneGameState){

            android.util.Log.i("", " " + playerNum + " " + nextAction);

            this.gameState = (CarcassonneGameState)info;
            if(gameState.getCurrentPlayer() == this.playerNum) {
                switch (nextAction) {
                    case ROTATE:
                        sleep(1500);
                        ArrayList<Board.TilePlacement> placements = this.gameState.getBoard().getValidTilePlacements();
                        int moves = (int) (Math.random() * placements.size());
                        this.chosenTile = placements.get(moves);
                        game.sendAction(new CarcassonneRotateTileAction(this, chosenTile.rotation));
                        nextAction = PLACE;
                        break;
                    case PLACE:
                        
                        android.util.Log.i("", " " + chosenTile.x + " " + chosenTile.y);

                        game.sendAction(new CarcassonnePlaceTileAction(this, chosenTile.x, chosenTile.y));
                        nextAction = CONFIRM_TILE;
                        break;
                    case CONFIRM_TILE:
                        game.sendAction(new CarcassonneConfirmTileAction(this));
                        nextAction = CONFIRM_MEEPLE;
                        break;
                    case CONFIRM_MEEPLE:
                        game.sendAction(new CarcassonneConfirmMeepleAction(this));
                        nextAction = ROTATE;
                        break;

                }

            }
        }
        else{
            return;
        }

    }
}
