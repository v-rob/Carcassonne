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

        for (int x = 0; x < this.board.getWidth(); x++) {
            for (int y = 0; y < this.board.getHeight(); y++) {
                Tile tile = this.board.getTile(x, y);
                if (!this.visitedTiles.contains(tile)) {
                    continue;
                }

                for (Section section : tile.getSections()) {
                    if (!this.visitedSections.contains(section)) {
                        continue;
                    }

                    for (int part : section.getParts()) {
                        int diagonalPart = Tile.getDiagonalPart(part);
                        Section diagonalSection = tile.getSection(diagonalPart);

                        if (section.getType() != Tile.TYPE_CITY ||
                                visitedCitySections.contains(diagonalSection)) {
                            continue;
                        }

                        CityMeepleAnalysis analysis = new CityMeepleAnalysis(
                                this.gameState, x, y, diagonalSection);
                        visitedCitySections.addAll(analysis.visitedSections);

                        if (analysis.isComplete()) {
                            score += 3;
                        }
                    }
                }
            }
        }

        return score;
    }

    public FarmMeepleAnalysis(CarcassonneGameState gameState, int x, int y,
                              Section startSection) {
        super(gameState, x, y, startSection, false);
        runAnalysis(x, y);
    }

}
