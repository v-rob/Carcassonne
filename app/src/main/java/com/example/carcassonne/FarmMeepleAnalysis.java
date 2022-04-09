package com.example.carcassonne;

import java.util.HashSet;

public class FarmMeepleAnalysis extends PartMeepleAnalysis {
    @Override
    public boolean isComplete() {
        return this.gameState.isGameOver();
    }

    @Override
    public int getScore() {
        int score = 0;

        HashSet<Section> visitedCitySections = new HashSet<>();

        for (Section section : this.visitedSections) {
            for (int part : section.getParts()) {
                int diagonalPart = Tile.getDiagonalPart(part);
                Section diagonalSection = section.getParent().getSection(diagonalPart);

                if (section.getType() != Tile.TYPE_CITY ||
                        visitedCitySections.contains(diagonalSection)) {
                    continue;
                }

                CityMeepleAnalysis analysis = new CityMeepleAnalysis(this.gameState,
                        diagonalSection);

                visitedCitySections.addAll(analysis.visitedSections);

                if (analysis.isComplete()) {
                    score += 3;
                }
            }
        }

        return score;
    }

    public FarmMeepleAnalysis(CarcassonneGameState gameState, Section startSection) {
        super(gameState, startSection, false);
        runAnalysis();
    }

}
