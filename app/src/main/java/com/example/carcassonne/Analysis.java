package com.example.carcassonne;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Analysis {
    protected Board board;

    protected Tile startTile;
    protected Section startSection;

    public abstract boolean isComplete();

    public abstract boolean isMeepleValid();

    public abstract int getCompleteScore(int player);
    public abstract int getIncompleteScore(int player);

    public abstract ArrayList<Tile> getCompletedMeeples();

    protected abstract void runAnalysis(int x, int y, int part);

    public Analysis(Board board, int x, int y, Section startSection) {
        this.board = board;
        this.startTile = board.getTile(x, y);
        this.startSection = startSection;
    }

    public static Analysis create(Board board, int x, int y, Section startSection) {
        switch (startSection.getType()) {
            case Tile.TYPE_FARM:
                return new FarmAnalysis(board, x, y, startSection);
            case Tile.TYPE_CITY:
                return new CityRoadAnalysis(board, x, y, startSection, true);
            case Tile.TYPE_ROAD:
                return new CityRoadAnalysis(board, x, y, startSection, false);
            case Tile.TYPE_CLOISTER:
                return new CloisterAnalysis(board, x, y, startSection);
        }

        // Shouldn't be able to happen.
        assert false;
        return null;
    }

    protected static ArrayList<Tile> getScoringMeeples(HashSet<Tile> allMeeples) {
        ArrayList<Tile>[] playerMeeples = new ArrayList[CarcassonneGameState.MAX_PLAYERS];
        int highest = 0;
        ArrayList<Tile> scoringMeeples = new ArrayList<>();
        for (int i = 0; i < playerMeeples.length; i++) {
            playerMeeples[i] = new ArrayList<>();
        }
        for (Tile tile : allMeeples) {
            playerMeeples[tile.getOwner()].add(tile);
        }
        for (int i = 0; i < playerMeeples.length; i++) {
            int size = playerMeeples[i].size();
            if (size > highest) {
                highest = size;
            }
        }
        for (int i = 0; i < playerMeeples.length; i++) {
            if (playerMeeples[i].size() == highest) {
                scoringMeeples.addAll(playerMeeples[i]);
            }
        }
        return scoringMeeples;
    }
}
