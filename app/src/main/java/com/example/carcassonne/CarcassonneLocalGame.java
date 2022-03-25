package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

public class CarcassonneLocalGame extends LocalGame{
    @Override
    protected void sendUpdatedStateTo(GamePlayer player) {

    }

    @Override
    protected boolean canMove(int player) {
        return false;
    }

    @Override
    protected String checkIfGameOver() {
        return null;
    }

    @Override
    protected boolean makeMove(GameAction action) {
        return false;
    }
}
