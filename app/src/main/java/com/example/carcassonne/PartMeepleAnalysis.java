package com.example.carcassonne;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Analysis class for all section types that consist of parts, i.e. roads, cities,
 * and farms. Since these classes share the exact same code for everything but
 * finding the final score, their analysis classes all inherit from this one.
 *
 * The main shared part of this code is that all these section types consist of
 * connected sections that can have many meeples on them, so all the part-based
 * sections share code for finding all these sections, checking if they are
 * closed, and finding the scoring players. Hence, the only thing subclasses
 * are required to implement is getScore().
 */
public abstract class PartMeepleAnalysis extends MeepleAnalysis {
    /** Whether this analysis should use road parts or normal parts. */
    private boolean isRoad;

    /** The set of all tiles that have been visited during partAnalysis(). */
    protected HashSet<Tile> visitedTiles;
    /** The set of all sections that have been visited during partAnalysis(). */
    protected HashSet<Section> visitedSections;

    /**
     * True if the sections are closed, i.e. each section has another section
     * connected to each of its parts.
     */
    protected boolean isClosed;

    /**
     * Returns whether the sections are closed, i.e. each section has another
     * section connected to each of its parts. This does not necessarily mean
     * that the class should be scored if it is closed.
     *
     * @return True if the sections are closed, false if not.
     */
    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

    /**
     * Returns whether the meeple placed on the starting section is valid or
     * not by checking to see if there are any meeples on any of the connected
     * tiles. If there are any other meeples, the one being placed is therefore
     * invalid. If there is no meeple, it is automatically valid.
     *
     * @return True if the meeple on the starting section is valid or there is
     *         no meeple, false otherwise.
     */
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

    /**
     * Gets the set of all sections connected to the starting section, which is all
     * of the sections visited by partAnalysis().
     *
     * @return The set of visited sections.
     */
    @Override
    protected HashSet<Section> getVisitedSections() {
        return this.visitedSections;
    }

    /**
     * Gets the set of players who will receive points from the analyzed sections.
     * If there are no meeples anywhere, it returns an empty set. If there are meeples,
     * it finds the player with the most meeples, or, if multiple players tied for
     * the most meeples, it returns the set of all those players.
     *
     * @return The set of players who will receive points from the analyzed sections.
     */
    @Override
    protected HashSet<Integer> getScoringPlayers() {
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

    /**
     * Runs the analysis. This consists solely of iterating through the parts
     * in the starting section and calling partAnalysis() on them.
     */
    @Override
    protected void runAnalysis() {
        for (int part : this.startSection.getParts()) {
            partAnalysis(this.startTile.getX(), this.startTile.getY(), part);
        }
    }

    /**
     * This is the meat of the PartMeepleAnalysis class. It is a recursive method:
     * starting from an initial part or road part number in some tile, it gets the
     * section at that part. If the section has already been visited, it returns.
     * Otherwise, it adds the section to the set of visited sections and then
     * recursively calls itself on all parts connected to this section.
     *
     * @param x    The X position of the tile being looked at in this call.
     * @param y    The Y position of the tile being looked at in this call.
     * @param part The part or road part of the section being looked at in this call.
     */
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

    /**
     * Creates a new part meeple analysis on the specified section. runAnalysis() is
     * not called; that is for subclasses to do in their respective constructors.
     *
     * @param board        The board containing the tiles and sections being analyzed.
     * @param startSection The cloister section to start the analysis from.
     */
    public PartMeepleAnalysis(Board board, Section startSection) {
        super(board, startSection);

        this.isRoad = startSection.getType() == Tile.TYPE_ROAD;
        this.visitedTiles = new HashSet<>();
        this.visitedSections = new HashSet<>();
        this.isClosed = true;
    }
}
