package com.example.carcassonne;

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
    public int getScore() {
        return this.neighbors;
    }

    @Override
    public void returnMeeples() {
        assert isComplete();

        // If we have a meeple, remove it and add it to the player's meeples.
        if (this.startSection.hasMeeple()) {
            this.gameState.addPlayerMeeples(this.startSection.getMeepleOwner(), 1);
            this.startSection.removeMeeple();
        }
    }

    @Override
    protected void runAnalysis(int x, int y) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (this.board.getTile(x + dx, y + dy) != null) {
                    this.neighbors++;
                }
            }
        }
    }

    public CloisterMeepleAnalysis(CarcassonneGameState gameState, int x, int y,
                                  Section startSection) {
        super(gameState, x, y, startSection);
        runAnalysis(x, y);
    }
}
