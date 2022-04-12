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

    /*@Override
    public void returnMeeples() {
        assert isComplete();

        // If we have a meeple, remove it and add it to the player's meeples.
        if (this.startSection.hasMeeple()) {
            this.gameState.addPlayerMeeples(this.startSection.getOwner(), 1);
            this.startSection.getParent().removeMeeple();
        }
    }*/

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
