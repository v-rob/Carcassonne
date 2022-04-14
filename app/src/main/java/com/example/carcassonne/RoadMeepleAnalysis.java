package com.example.carcassonne;

/**
 * Analysis class for road sections. This is a simple class; since PartMeepleAnalysis
 * does all the hard lifting, all this class must do is calculate the score, which is
 * trivial in the case of roads.
 *
 * When roads are closed, they are considered complete and should be scored.
 */
public class RoadMeepleAnalysis extends PartMeepleAnalysis {
    /**
     * Calculates the score awarded by the road sections, which is the total number
     * of connected tiles. It does not distinguish between complete and incomplete
     * roads since the score is identical in both cases.
     *
     * @return The score awarded by this road.
     */
    @Override
    protected int getScore() {
        return this.visitedTiles.size();
    }

    /**
     * Creates a new road meeple analysis on the specified section. It is an error
     * if the section is not a road section.
     *
     * @param board        The board containing the tiles and sections being analyzed.
     * @param startSection The road section to start the analysis from.
     */
    public RoadMeepleAnalysis(Board board, Section startSection) {
        super(board, startSection);
        assert startSection.getType() == Tile.TYPE_ROAD;
        runAnalysis();
    }
}
