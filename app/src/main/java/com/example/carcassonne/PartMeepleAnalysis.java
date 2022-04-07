package com.example.carcassonne;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class PartMeepleAnalysis extends MeepleAnalysis {
    private boolean isRoad;

    protected HashSet<Tile> visitedTiles;
    protected HashSet<Section> visitedSections;
    protected boolean isClosed;

    @Override
    public boolean isMeepleValid() {
        // If the starting section doesn't have a meeple, it can't be invalid.
        if (!this.startTile.hasMeeple()) {
            return true;
        }

        for (Section section : this.visitedSections) {
            // If we find any meeple that is not on the section we're analyzing,
            // then it's invalid to place a meeple on the starting section.
            if (section.hasMeeple() && section != this.startSection) {
                return false;
            }
        }

        // Otherwise, this is the only meeple, so it's valid.
        return true;
    }

    @Override
    public void returnMeeples() {
        // Create arrays for each player containing all their meeples.
        ArrayList<Section>[] playerMeeples = new ArrayList[CarcassonneGameState.MAX_PLAYERS];
        for (int i = 0; i < playerMeeples.length; i++) {
            playerMeeples[i] = new ArrayList<>();
        }

        // Iterate through the set of tiles, find each with a meeple, and put it in
        // the proper player's array.
        for (Section section : this.visitedSections) {
            if (section.hasMeeple()) {
                playerMeeples[section.getMeepleOwner()].add(section);
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

        // Return the meeples for the player with the highest number of meeples/the
        // players that are tied for the highest number of meeples.
        for (int i = 0; i < playerMeeples.length; i++) {
            // Ignore players who don't have the highest number of meeples.
            int numMeeples = playerMeeples[i].size();
            if (numMeeples != highest) {
                continue;
            }

            // Add the meeples back to the player's meeples.
            this.gameState.addPlayerMeeples(this.startTile.getMeepleOwner(), numMeeples);

            // Remove the meeples from each tile.
            for (Section section : playerMeeples[i]) {
                section.removeMeeple();
            }
        }
    }

    @Override
    protected void runAnalysis(int x, int y) {
        for (int part : this.startSection.getParts()) {
            partAnalysis(x, y, part);
        }
    }

    protected void partAnalysis(int x, int y, int part) {
        Tile tile = this.board.getTile(x, y);
        if (tile == null) {
            // If we found a position with no tile, that means that the farm, road, or
            // city is not closed off. Mark it as such and return.
            this.isClosed = false;
            return;
        }

        // Get the section from the part we were provided.
        Section section = this.isRoad ?
                tile.getRoadSection(part) :
                tile.getSection(part);

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
            if (this.isRoad) {
                partAnalysis(x + Tile.roadPartXOffset(other_part),
                        y + Tile.roadPartYOffset(other_part), Tile.flipRoadPart(other_part));
            } else {
                partAnalysis(x + Tile.partXOffset(other_part),
                        y + Tile.partYOffset(other_part), Tile.flipPart(other_part));
            }
        }
    }

    public PartMeepleAnalysis(CarcassonneGameState gameState, int x, int y,
                              Section startSection, boolean isRoad) {
        super(gameState, x, y, startSection);

        this.isRoad = isRoad;
        this.visitedTiles = new HashSet<>();
        this.visitedSections = new HashSet<>();
        this.isClosed = true;
    }
}
