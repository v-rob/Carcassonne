package com.example.carcassonne;

import com.example.carcassonne.actionMsg.GameAction;

public class CarcassonnePlaceMeepleAction extends GameAction {
    private int x;
    private int y;

    public CarcassonnePlaceMeepleAction(GamePlayer player, int x, int y) {
        super(player);
        this.x = x;
        this.y = y;
    }

    int getX() {
        return this.x;
    }

    int getY() {
        return this.y;
    }
}