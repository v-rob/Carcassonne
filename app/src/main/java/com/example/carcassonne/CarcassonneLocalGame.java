package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

public class CarcassonneLocalGame extends LocalGame{
    private CarcassonneGameState gameState;

    @Override
    protected void sendUpdatedStateTo(GamePlayer player) {
        player.sendInfo(new CarcassonneGameState(this.gameState));
    }

    @Override
    protected boolean canMove(int player) {
        return false;
    }
    //TODO put code here :)

    @Override
    protected String checkIfGameOver() {
            if(gameState.getDeck().isEmpty()) {
                return "Game Over";
            }
            else return null;
        }

    @Override
    protected boolean makeMove(GameAction action) {
        return false;
    }
    //TODO and hereee
}
