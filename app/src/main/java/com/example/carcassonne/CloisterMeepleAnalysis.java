package com.example.carcassonne;

import java.util.HashSet;

/**
 * Analysis class for cloisters. Cloisters are both the simplest and most
 * different of the types of sections in terms of scoring: they don't have to
 * deal with parts or connected sections, but are localized to a single section
 * and all directly adjacent tiles.
 *
 * Complete scoring should be performed when the analysis is closed with
 * isClosed(). Otherwise, only incomplete scoring should be done.
 */
public class CloisterMeepleAnalysis extends MeepleAnalysis {
    /**
     * The number of neighboring tiles, including the tile with the cloister
     * itself and tiles diagonal to the cloister.
     */
    private int neighbors;

    /**
     * Whether the cloister is closed off, which means that the cloister is
     * completely surrounded by tiles and is ready for complete scoring.
     *
     * @return True if the cloister is closed off and ready to be scored,
     *         false otherwise.
     */
    @Override
    public boolean isClosed() {
        return this.neighbors == 9;
    }

    /**
     * Queries whether the meeple placed on the tile is valid or not. This is
     * always true because it is always valid to place a meeple on a cloister,
     * and no meeple is, of course, valid.
     *
     * @return True since the meeple is always valid no matter what.
     */
    @Override
    public boolean isMeepleValid() {
        return true;
    }

    /**
     * Returns the set of sections visited by this analysis. This will always be
     * a one-element set containing exclusively the cloister section.
     *
     * @return A set containing the cloister section.
     */
    @Override
    protected HashSet<Section> getVisitedSections() {
        HashSet<Section> visitedSections = new HashSet<>();
        visitedSections.add(this.startSection);
        return visitedSections;
    }

    /**
     * Calculates the score awarded by this cloister, which is the number of adjacent
     * tiles plus the cloister tile itself. If the cloister is complete, this score
     * will therefore be nine.
     *
     * @return The score awarded by this cloister.
     */
    @Override
    protected int getScore() {
        return this.neighbors;
    }

    /**
     * Returns the set of players who will receive points from this cloister. If
     * there is a meeple on the cloister, the set will contain that player.
     * Otherwise, the set will be empty.
     *
     * @return A set containing the player on the cloister, or an empty set if no
     *         player has a meeple on this tile.
     */
    @Override
    protected HashSet<Integer> getScoringPlayers() {
        HashSet<Integer> scoringPlayers = new HashSet<>();

        // If we have a meeple, add it to the set.
        if (this.startSection.hasMeeple()) {
            scoringPlayers.add(this.startSection.getOwner());
        }

        return scoringPlayers;
    }

    /**
     * Runs the analysis on the cloister section. This simply consists of adding
     * up the number of tiles surrounding and including the tile with the cloister
     * on it.
     */
    @Override
    protected void runAnalysis() {
        // Iterate over every relative adjacent position and check for a tile.
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (this.board.getTile(this.startTile.getX() + dx,
                        this.startTile.getY() + dy) != null) {
                    this.neighbors++;
                }
            }
        }
    }

    /**
     * Creates a new cloister meeple analysis on the specified section. It is an
     * error if the section is not a cloister section.
     *
     * @param board        The board containing the tiles and sections being analyzed.
     * @param startSection The cloister section to start the analysis from.
     */
    public CloisterMeepleAnalysis(Board board, Section startSection) {
        super(board, startSection);
        assert startSection.getType() == Tile.TYPE_CLOISTER;
        runAnalysis();
    }
}
