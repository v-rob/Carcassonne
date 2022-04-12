package com.example.carcassonne;

import java.util.HashSet;

public class CloisterMeepleAnalysis extends MeepleAnalysis {
    private int neighbors;

    @Override
    public boolean isComplete() {
        return this.neighbors == 9;
    }

    @Override
    public boolean isMeepleValid() {
        return true;
    }

    @Override
    protected HashSet<Section> getVisitedSections() {
        HashSet<Section> visitedSections = new HashSet<>();
        visitedSections.add(this.startSection);
        return visitedSections;
    }

    @Override
    protected int getScore() {
        return this.neighbors;
    }

    @Override
    protected HashSet<Integer> getScoringPlayers() {
        HashSet<Integer> scoringPlayers = new HashSet<>();

        // If we have a meeple, add it to the set.
        if (this.startSection.hasMeeple()) {
            scoringPlayers.add(this.startSection.getOwner());
        }

        return scoringPlayers;
    }

    @Override
    protected void runAnalysis() {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (this.board.getTile(this.startTile.getX() + dx,
                        this.startTile.getY() + dy) != null) {
                    this.neighbors++;
                }
            }
        }
    }

    public CloisterMeepleAnalysis(Board board, Section startSection) {
        super(board, startSection);
        runAnalysis();
    }
}
