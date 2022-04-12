package com.example.carcassonne;

import java.util.HashSet;

public abstract class MeepleAnalysis {
    protected Board board;

    protected Tile startTile;
    protected Section startSection;

    public Board getBoard() {
        return this.board;
    }

    public Tile getStartTile() {
        return this.startTile;
    }

    public Section getStartSection() {
        return this.startSection;
    }

    public abstract boolean isComplete();

    public abstract boolean isMeepleValid();

    public void tallyScores(int[] playerScores) {
        int score = getScore();
        for (int player : getScoringPlayers()) {
            playerScores[player] += score;
        }
    }

    public void returnMeeples(int[] playerMeeples) {
        // It doesn't make sense to return meeples if the stuff isn't complete.
        assert isComplete();

        HashSet<Integer> scoringPlayers = getScoringPlayers();

        for (Section section : getVisitedSections()) {
            int owner = section.getOwner();

            // If this section has one of the scoring player's meeples, remove it
            // and increment their player meeples count.
            if (section.hasMeeple() && scoringPlayers.contains(owner)) {
                section.getParent().removeMeeple();
                playerMeeples[owner]++;
            }
        }
    }

    protected abstract HashSet<Section> getVisitedSections();

    protected abstract int getScore();

    protected abstract HashSet<Integer> getScoringPlayers();

    protected abstract void runAnalysis();

    public MeepleAnalysis(Board board, Section startSection) {
        this.board = board;

        this.startTile = startSection.getParent();
        this.startSection = startSection;
    }

    public static MeepleAnalysis create(Board board, Section startSection) {
        switch (startSection.getType()) {
            case Tile.TYPE_FARM:
                return new FarmMeepleAnalysis(board, startSection);
            case Tile.TYPE_CITY:
                return new CityMeepleAnalysis(board, startSection);
            case Tile.TYPE_ROAD:
                return new RoadMeepleAnalysis(board, startSection);
            case Tile.TYPE_CLOISTER:
                return new CloisterMeepleAnalysis(board, startSection);
        }

        // Shouldn't be able to happen.
        assert false;
        return null;
    }

    public interface Analyzer {
        void analyze(MeepleAnalysis analysis);
    }

    public static void analyzeTile(Board board, Tile tile, Analyzer analyzer) {
        analyzeTileLow(board, tile, analyzer, new HashSet<>());
    }

    public static void analyzeBoard(Board board, Analyzer analyzer) {
        HashSet<Section> visitedSections = new HashSet<>();

        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                Tile tile = board.getTile(x, y);

                // Don't analyze null tiles.
                if (tile == null) {
                    continue;
                }

                analyzeTileLow(board, tile, analyzer, visitedSections);
            }
        }
    }

    private static void analyzeTileLow(Board board, Tile tile, Analyzer analyzer,
                                       HashSet<Section> visitedSections) {
        for (Section section : tile.getSections()) {
            // If we've already seen this section, i.e. it's connected to a section
            // we've already visited before, don't analyze it again.
            if (visitedSections.contains(section)) {
                continue;
            }

            MeepleAnalysis analysis = MeepleAnalysis.create(board, section);

            // Make sure we don't visit these sections again.
            visitedSections.addAll(analysis.getVisitedSections());

            // Now let the user apply the results of this analysis.
            analyzer.analyze(analysis);
        }
    }
}
