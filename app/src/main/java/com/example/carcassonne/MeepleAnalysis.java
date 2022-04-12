package com.example.carcassonne;

import java.util.HashSet;

public abstract class MeepleAnalysis {
    protected Board board;

    protected Tile startTile;
    protected Section startSection;

    public abstract boolean isComplete();

    public abstract boolean isMeepleValid();

    public abstract HashSet<Section> getVisitedSections();

    public abstract int getScore();

    public abstract HashSet<Integer> getScoringPlayers();

    public abstract void returnMeeples(CarcassonneGameState gameState);

    protected abstract void runAnalysis();

    public MeepleAnalysis(Board board, Section startSection) {
        this.board = board;

        this.startTile = startSection.getParent();
        this.startSection = startSection;
    }

    public static MeepleAnalysis create(Board board, Section startSection) {
        switch (startSection.getType()) {
            case Tile.TYPE_FARM:
                return new FarmMeepleAnalysis(board, startSection);
            case Tile.TYPE_CITY:
                return new CityMeepleAnalysis(board, startSection);
            case Tile.TYPE_ROAD:
                return new RoadMeepleAnalysis(board, startSection);
            case Tile.TYPE_CLOISTER:
                return new CloisterMeepleAnalysis(board, startSection);
        }

        // Shouldn't be able to happen.
        assert false;
        return null;
    }
}
