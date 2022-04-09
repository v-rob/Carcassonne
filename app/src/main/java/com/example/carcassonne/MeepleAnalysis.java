package com.example.carcassonne;

public abstract class MeepleAnalysis {
    protected CarcassonneGameState gameState;
    protected Board board;

    protected Tile startTile;
    protected Section startSection;

    public abstract boolean isComplete();

    public abstract boolean isMeepleValid();

    public abstract int getScore();

    public abstract void returnMeeples();

    protected abstract void runAnalysis();

    public MeepleAnalysis(CarcassonneGameState gameState, Section startSection) {
        this.gameState = gameState;
        this.board = this.gameState.getBoard();

        this.startTile = startSection.getParent();
        this.startSection = startSection;
    }

    public static MeepleAnalysis create(CarcassonneGameState gameState, Section startSection) {
        switch (startSection.getType()) {
            case Tile.TYPE_FARM:
                return new FarmMeepleAnalysis(gameState, startSection);
            case Tile.TYPE_CITY:
                return new CityMeepleAnalysis(gameState, startSection);
            case Tile.TYPE_ROAD:
                return new RoadMeepleAnalysis(gameState, startSection);
            case Tile.TYPE_CLOISTER:
                return new CloisterMeepleAnalysis(gameState, startSection);
        }

        // Shouldn't be able to happen.
        assert false;
        return null;
    }
}
