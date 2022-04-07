package com.example.carcassonne;

public class CityMeepleAnalysis extends PartMeepleAnalysis {
    private int numPennants;

    @Override
    public boolean isComplete() {
        return this.isClosed;
    }

    @Override
    public int getScore(int player) {
        return 0;
    }

    @Override
    public boolean isMeepleValid() {
        return false;
    }

    @Override
    protected void runAnalysis(int x, int y) {
        super.runAnalysis(x, y);

        for (Tile tile : this.visitedTiles) {
            if (tile.hasPennant()) {
                this.numPennants++;
            }
        }
    }

    public CityMeepleAnalysis(CarcassonneGameState gameState, int x, int y,
                              Section startSection) {
        super(gameState, x, y, startSection, false);
        this.numPennants = 0;

        runAnalysis(x, y);
    }
}
