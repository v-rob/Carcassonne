package com.example.carcassonne;

public abstract class MeepleAnalysis {
    protected CarcassonneGameState gameState;
    protected Board board;

    protected Tile startTile;
    protected Section startSection;

    public abstract boolean isComplete();

    public abstract int getScore();

    public abstract boolean isMeepleValid();

    public abstract void returnMeeples();

    protected abstract void runAnalysis(int x, int y);

    public MeepleAnalysis(CarcassonneGameState gameState, int x, int y, Section startSection) {
        this.gameState = gameState;
        this.board = this.gameState.getBoard();

        this.startTile = board.getTile(x, y);
        this.startSection = startSection;
    }

    public static MeepleAnalysis create(CarcassonneGameState gameState, int x, int y,
                                        Section startSection) {
        switch (startSection.getType()) {
            case Tile.TYPE_FARM:
                return new FarmMeepleAnalysis(gameState, x, y, startSection);
            case Tile.TYPE_CITY:
                return new CityMeepleAnalysis(gameState, x, y, startSection);
            case Tile.TYPE_ROAD:
                return new RoadMeepleAnalysis(gameState, x, y, startSection);
            case Tile.TYPE_CLOISTER:
                return new CloisterMeepleAnalysis(gameState, x, y, startSection);
        }

        // Shouldn't be able to happen.
        assert false;
        return null;
    }
}
