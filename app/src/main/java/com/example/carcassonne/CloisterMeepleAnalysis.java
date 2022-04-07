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
    public int getScore(int player) {
        // If there is a meeple owned by the specified player, return the score.
        if (this.startSection.hasMeeple() &&
                this.startSection.getMeepleOwner() == player) {
            return this.neighbors;
        }
        return 0;
    }

    @Override
    public void returnMeeples() {
        super.returnMeeples();

        // If we have a meeple, remove it and add it to the player's meeples.
        if (this.startSection.hasMeeple()) {
            this.gameState.addPlayerMeeples(this.startTile.getMeepleOwner(), 1);
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
