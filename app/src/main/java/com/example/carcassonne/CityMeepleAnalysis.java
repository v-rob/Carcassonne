package com.example.carcassonne;

public class CityMeepleAnalysis extends PartMeepleAnalysis {
    private int numPennants;

    @Override
    public boolean isComplete() {
        return this.isClosed;
    }

    @Override
    public int getScore() {
        int score = this.visitedTiles.size() + numPennants;
        if (isComplete()) {
            return score * 2;
        }
        return score;
    }

    @Override
    protected void runAnalysis() {
        super.runAnalysis();

        for (Tile tile : this.visitedTiles) {
            if (tile.hasPennant()) {
                this.numPennants++;
            }
        }
    }

    public CityMeepleAnalysis(CarcassonneGameState gameState, Section startSection) {
        super(gameState, startSection, false);
        this.numPennants = 0;

        runAnalysis();
    }
}
