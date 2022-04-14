package com.example.carcassonne;

import java.util.HashSet;

/**
 * Analysis class for farm sections. Farms are scored in a very different manner
 * than every other class: they are scored only at the end of the game, and the
 * points are based on the number of complete cities connected to the farm
 * rather than the number of farm tiles themselves, making this the only analysis
 * that performs analyses inside of it.
 *
 * In other words, while the isClosed() method returns whether the farm is closed
 * or not, this has no bearing on whether the farm should be scored or not. The
 * farms should always be scored as incomplete points, and these points added
 * to the final score only at the very end of the game. returnMeeples() should
 * never be called.
 */
public class FarmMeepleAnalysis extends PartMeepleAnalysis {
    /**
     * Calculates the score awarded by these farm sections. This score is three
     * points for each completed city that is connected to this farm section.
     * These points are always counted as incomplete points.
     *
     * The algorithm works by looping through every part in every connected farm
     * section. It then gets the _diagonal_ of the part with Tile.getDiagonalPart().
     * Cities that are adjacent to farms are always adjacent over a diagonal on the
     * tile. This also means that there can't be any issues with having to check
     * for roads in between the farm and the city.
     *
     * If that diagonal is a city that has not been visited before, it performs a
     * CityMeepleAnalysis on it, checking if the city is closed. If it is, it adds
     * three points to the total score.
     *
     * @return The score awarded by this farm.
     */
    @Override
    protected int getScore() {
        int score = 0;

        // Keep a set of visited city tiles so we don't count cities multiple times.
        HashSet<Section> visitedCitySections = new HashSet<>();

        // Loop through every part in each farm section.
        for (Section section : this.visitedSections) {
            for (int part : section.getParts()) {
                // Get the diagonal part to this one since cities will always be on
                // the diagonal.
                int diagonalPart = Tile.getDiagonalPart(part);
                Section diagonalSection = section.getParent().getSection(diagonalPart);

                // Do nothing if this is a city or has already been seen before.
                if (diagonalSection.getType() != Tile.TYPE_CITY ||
                        visitedCitySections.contains(diagonalSection)) {
                    continue;
                }

                // Otherwise, run the analysis and add the points if the city is complete.
                CityMeepleAnalysis analysis = new CityMeepleAnalysis(
                        this.board, diagonalSection);

                visitedCitySections.addAll(analysis.getVisitedSections());

                if (analysis.isClosed()) {
                    score += 3;
                }
            }
        }

        return score;
    }


    /**
     * Creates a new farm meeple analysis on the specified section. It is an error
     * if the section is not a farm section.
     *
     * @param board        The board containing the tiles and sections being analyzed.
     * @param startSection The farm section to start the analysis from.
     */
    public FarmMeepleAnalysis(Board board, Section startSection) {
        super(board, startSection);
        assert startSection.getType() == Tile.TYPE_FARM;
        runAnalysis();
    }
}
