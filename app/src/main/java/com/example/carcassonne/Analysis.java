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

    protected static ArrayList<Tile> getScoringMeeples(HashSet<Tile> allTiles) {
        // Create arrays for each player containing all their meeples.
        ArrayList<Tile>[] playerMeeples = new ArrayList[CarcassonneGameState.MAX_PLAYERS];
        for (int i = 0; i < playerMeeples.length; i++) {
            playerMeeples[i] = new ArrayList<>();
        }

        // Iterate through the set of tiles, find each with a meeple, and put it in
        // the proper player's array.
        for (Tile tile : allTiles) {
            if (tile.hasMeeple()) {
                playerMeeples[tile.getOwner()].add(tile);
            }
        }

        // Find the number of meeples of the player(s) with the most meeples.
        int highest = 0;
        for (int i = 0; i < playerMeeples.length; i++) {
            int size = playerMeeples[i].size();
            if (size > highest) {
                highest = size;
            }
        }

        // Combine all the meeples from the player(s) with the most meeples into a
        // single array.
        ArrayList<Tile> scoringMeeples = new ArrayList<>();
        for (int i = 0; i < playerMeeples.length; i++) {
            if (playerMeeples[i].size() == highest) {
                scoringMeeples.addAll(playerMeeples[i]);
            }
        }

        return scoringMeeples;
    }
}
