package com.example.carcassonne;

/**
 * Analysis class for city sections. This is a simple class; since PartMeepleAnalysis
 * does all the hard lifting, all this class must do is calculate the score, which
 * is fairly simple for cities.
 *
 * When cities are closed, they are considered complete and should be scored.
 */
public class CityMeepleAnalysis extends PartMeepleAnalysis {
    /**
     * Calculates the score awarded by the city sections. If the city is incomplete,
     * this is the number of city tiles plus the number of pennants on each tile in
     * the city. If the city is complete, the score is twice that of the incomplete
     * score.
     *
     * @return The score awarded by this city.
     */
    @Override
    protected int getScore() {
        // Count the number of pennants on all the tiles.
        int numPennants = 0;
        for (Tile tile : this.visitedTiles) {
            if (tile.hasPennant()) {
                numPennants++;
            }
        }

        // The score is a combination of the number of city tiles and pennants
        int score = this.visitedTiles.size() + numPennants;

        if (isClosed()) {
            // Completed cities have double the score
            return score * 2;
        }
        return score;
    }

    /**
     * Creates a new city meeple analysis on the specified section. It is an error
     * if the section is not a city section.
     *
     * @param board        The board containing the tiles and sections being analyzed.
     * @param startSection The city section to start the analysis from.
     */
    public CityMeepleAnalysis(Board board, Section startSection) {
        super(board, startSection);
        assert startSection.getType() == Tile.TYPE_CITY;
        runAnalysis();
    }
}
