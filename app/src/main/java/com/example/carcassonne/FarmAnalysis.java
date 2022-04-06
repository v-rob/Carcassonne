package com.example.carcassonne;

import java.util.ArrayList;

public class FarmAnalysis extends Analysis {
    @Override
    public boolean isComplete(){
        return false;
    }

    @Override
    public boolean isMeepleValid() {
        return false;
    }

    @Override
    public int getCompleteScore(int player) {
        return 0;
    }

    @Override
    public int getIncompleteScore(int player) {
        return 0;
    }

    @Override
    public ArrayList<Tile> getCompletedMeeples() {
        return null;
    }

    @Override
    protected void runAnalysis(int x, int y, int part) {
    }

    public FarmAnalysis(Board board, int x, int y, Section startSection) {
        super(board, x, y, startSection);
        for (int part : this.startSection.getParts()) {
            runAnalysis(x, y, part);
        }
    }
}
