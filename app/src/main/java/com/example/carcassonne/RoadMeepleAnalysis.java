package com.example.carcassonne;

public class RoadMeepleAnalysis extends PartMeepleAnalysis {
    @Override
    public boolean isComplete() {
        return this.isClosed;
    }

    @Override
    public int getScore() {
        return this.visitedTiles.size();
    }

    public RoadMeepleAnalysis(CarcassonneGameState gameState, Section startSection) {
        super(gameState, startSection, true);
        runAnalysis();
    }
}
