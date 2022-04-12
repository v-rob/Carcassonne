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

    public HashSet<Integer> getScoringPlayers() {
        // Create arrays for each player containing all their meeples.
        ArrayList<Section>[] playerMeeples = new ArrayList[CarcassonneGameState.MAX_PLAYERS];
        for (int i = 0; i < playerMeeples.length; i++) {
            playerMeeples[i] = new ArrayList<>();
        }

        // Iterate through the set of tiles, find each with a meeple, and put it in
        // the proper player's array.
        for (Section section : this.visitedSections) {
            if (section.hasMeeple()) {
                playerMeeples[section.getOwner()].add(section);
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

        HashSet<Integer> scoringPlayers = new HashSet<>();

        // If the highest score is zero, there are no meeples, so return no players.
        if (highest == 0) {
            return scoringPlayers;
        }

        // Find the meeples for the player(s) with the highest number of meeples.
        for (int i = 0; i < playerMeeples.length; i++) {
            if (playerMeeples[i].size() == highest) {
                scoringPlayers.add(i);
            }
        }

        return scoringPlayers;
    }

    /*@Override
    public void returnMeeples() {
        assert isComplete();

        HashSet<Integer> scoringPlayers = getScoringPlayers();

        for (Section section : this.visitedSections) {
            if (section.hasMeeple() && scoringPlayers.contains(section.getOwner())) {
                this.gameState.addPlayerMeeples(section.getOwner(), 1);
                section.getParent().removeMeeple();
            }
        }
    }*/

    @Override
    protected void runAnalysis() {
        for (int part : this.startSection.getParts()) {
            partAnalysis(this.startTile.getX(), this.startTile.getY(), part);
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
        if (this.visitedSections.contains(section)) {
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

    public PartMeepleAnalysis(Board board, Section startSection,
                              boolean isRoad) {
        super(board, startSection);

        this.isRoad = isRoad;
        this.visitedTiles = new HashSet<>();
        this.visitedSections = new HashSet<>();
        this.isClosed = true;

        // Do not run the analysis; that is for subclasses to do in their
        // respective constructors.
    }
}
