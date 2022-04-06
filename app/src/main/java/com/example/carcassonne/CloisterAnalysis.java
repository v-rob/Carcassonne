package com.example.carcassonne;

import java.util.ArrayList;

public class CloisterAnalysis extends Analysis {
    private int score;

    @Override
    public boolean isComplete() {
        return this.score == 9;
    }

    @Override
    public boolean isMeepleValid() {
        return true;
    }

    @Override
    public int getCompleteScore(int player) {
        return isComplete() ? this.score : 0;
    }

    @Override
    public int getIncompleteScore(int player) {
        return !isComplete() ? this.score : 0;
    }

    @Override
    public ArrayList<Tile> getCompletedMeeples() {
        ArrayList<Tile> meeple = new ArrayList<>();
        if (isComplete()) {
            meeple.add(this.startTile);
        }
        return meeple;
    }

    @Override
    protected void runAnalysis(int x, int y, int part) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (this.board.getTile(x + dx, y + dy) != null) {
                    this.score++;
                }
            }
        }
    }

    public CloisterAnalysis(Board board, int x, int y, Section startSection) {
        super(board, x, y, startSection);
        runAnalysis(x, y, 0);
    }
}
