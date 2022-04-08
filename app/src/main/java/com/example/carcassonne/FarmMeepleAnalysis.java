package com.example.carcassonne;

public class FarmMeepleAnalysis extends PartMeepleAnalysis {
    @Override
    public boolean isComplete() {
        return this.gameState.isGameOver();
    }

    @Override
    public int getScore(int player) {
        return 0;
    }

    @Override
    public boolean isMeepleValid() {
        return false;
    }

    public FarmMeepleAnalysis(CarcassonneGameState gameState, int x, int y,
                              Section startSection) {
        super(gameState, x, y, startSection, false);
        runAnalysis(x, y);
    }
}
