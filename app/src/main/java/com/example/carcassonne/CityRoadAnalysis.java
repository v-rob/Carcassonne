package com.example.carcassonne;

import java.util.ArrayList;
import java.util.HashSet;

public class CityRoadAnalysis extends Analysis {
    private boolean isCity;
    private int numPennants;
    private HashSet<Tile> visitedTiles;
    private HashSet<Section> visitedSections;
    private boolean isComplete;

    @Override
    public boolean isComplete() {
        return this.isComplete;
    }

    @Override
    public boolean isMeepleValid() {
        return false;
    }

    @Override
    public int getCompleteScore(int player) {
        return 0;
    }

    @Override
    public int getIncompleteScore(int player) {
        return 0;
    }

    @Override
    public ArrayList<Tile> getCompletedMeeples() {
        return null;
    }

    @Override
    protected void runAnalysis(int x, int y, int part) {
        Tile tile = this.board.getTile(x, y);
        if (tile == null) {
            // If we managed to find a non-existent tile, this means that the road
            // or city is not complete. Mark it as such and return.
            this.isComplete = false;
            return;
        }

        // Get the section from the part we were provided.
        Section section;
        if (this.isCity) {
            section = tile.getSection(part);
        } else {
            section = tile.getRoadSection(part);
        }

        // Don't count sections we've already searched through so that we don't run
        // into infinite recursion.
        if (visitedSections.contains(section)) {
            return;
        }

        // Otherwise, add this section and tile to the visited sets. If the tile has
        // already been visited, but this is a different section, adding the tile to
        // the set again will be a no-op.
        this.visitedSections.add(section);
        this.visitedTiles.add(tile);

        // Run this same function on all adjacent parts connected to this section.
        for (int other_part : section.getParts()) {
            if (this.isCity) {
                runAnalysis(x + Tile.partXOffset(other_part),
                        y + Tile.partYOffset(other_part), Tile.flipPart(other_part));
            } else {
                runAnalysis(x + Tile.roadPartXOffset(other_part),
                        y + Tile.roadPartYOffset(other_part), Tile.flipRoadPart(other_part));
            }
        }
    }

    public CityRoadAnalysis(Board board, int x, int y, Section startSection, boolean isCity) {
        super(board, x, y, startSection);
        this.isCity = isCity;
        this.numPennants = 0;
        this.visitedSections = new HashSet<>();
        this.visitedTiles = new HashSet<>();
        this.isComplete = true;

        for (int part : this.startSection.getParts()) {
            runAnalysis(x, y, part);
        }
    }
}
