package com.example.carcassonne;

import java.util.ArrayList;
import java.util.HashSet;

public class CloisterAnalysis extends Analysis {
    private int score;

    private HashSet<Tile> visited;

    @Override
    public boolean isMeepleValid() {
        return true;
    }

    @Override
    public int getCompleteScore(int player) {
        return (this.score == 8) ? this.score : 0;
    }

    @Override
    public int getIncompleteScore(int player) {
        return (this.score != 8) ? this.score : 0;
    }

    @Override
    public ArrayList<Tile> getCompletedMeeples() {
        ArrayList<Tile> completed = new ArrayList<>();
        if (this.score == 8) {
            completed.add(this.start);
        }
        return completed;
    }

    public void runAnalysis(int x, int y) {
        this.score = 0;
    }

    public CloisterAnalysis(Board board) {
        super(board);
        this.visited = new HashSet<>();
    }

    private void doAnalysis(int x, int y) {
        Tile tile = this.board.getAnyTile(x, y);
        if (this.visited.contains(tile) ||
                tile.getMeepleType() != Tile.TYPE_CLOISTER) {
            return;
        }

        this.start = tile;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (this.board.getAnyTile(dx, dy) != null) {
                    this.score++;
                }
            }
        }
    }
}
