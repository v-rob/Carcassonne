package com.example.carcassonne;

import java.util.HashSet;

/**
 * The base class for all meeple analysis code: these classes traverse the board,
 * finding out information about meeples and their respective cities, roads, farms,
 * and cloisters. These classes are the heart of everything meeple and scoring
 * related in Carcassonne. They provide the following features:
 *
 * - Checking if some section is complete and ready for scoring
 * - Finding if the current meeple placement is valid
 * - Scoring and returning meeples after scoring is done
 *
 * Since each type of section have wildly different scoring and completion rules,
 * everything is split into subclasses of this class and implement the required
 * methods.
 *
 * There are four methods of using the MeepleAnalysis classes:
 *
 * - If the type of the section is known, construct a new analysis class directly,
 *   e.g. new CityMeepleAnalysis(board, section).
 * - If the type of the section is unknown, use the static factory method
 *   MeepleAnalysis.create(), which will construct the proper MeepleAnalysis subclass
 *   and return it.
 * - If analyzing an entire tile rather than a single section (such as scoring a
 *   tile after it was placed), use the MeepleAnalysis.analyzeTile() method and
 *   provide an appropriate lambda to run for each section analysis.
 * - If analyzing the entire board (such as for incomplete scores), use the
 *   MeepleAnalysis.analyzeBoard() with an appropriate lambda to run for each
 *   section analysis on every section of each tile on the board.
 *
 * Once constructed, the analysis will automatically run, and then any information
 * needed from the analysis can be extracted from the appropriate public methods.
 *
 * Analyses should never be run if board.isCurrentTilePlacementValid() is false;
 * this will break all the assumptions of the analyses and certainly lead to errors
 * and crashes.
 */
public abstract class MeepleAnalysis {
    /** The board containing the tiles and sections being analyzed. */
    protected Board board;

    /**
     * The tile that the analysis started from. This is a convenience instance
     * variable, equivalent to this.startSection.getParent(), but simpler.
     */
    protected Tile startTile;
    /** The section that the analysis started from. */
    protected Section startSection;

    /**
     * Gets the board containing the tiles and sections being analyzed.
     *
     * @return The board.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Gets the tile that the analysis started from. Equivalent to
     * getStartSection().getParent().
     *
     * @return The starting tile.
     */
    public Tile getStartTile() {
        return this.startTile;
    }

    /**
     * Gets the section that the analysis started from.
     *
     * @return The starting section.
     */
    public Section getStartSection() {
        return this.startSection;
    }

    /**
     * Queries whether the section being analyzed is closed or not. The exact
     * meaning of this varies between analyses, but generally means that every
     * possible connected section is closed off; consult the subclasses for more
     * information. Often, but not always, this means that the section is ready
     * for scoring.
     *
     * @return True if the section is closed, false if not.
     */
    public abstract boolean isClosed();

    /**
     * If there is a meeple on the starting section, this method checks if it is
     * valid to place that meeple there. If there is no meeple, returns true since
     * no meeple is always valid. This should only be called on the current tile
     * since it makes no sense to check the validity of already established meeples.
     *
     * @return True if the meeple being placed on this section is valid, false
     *         otherwise.
     */
    public abstract boolean isMeepleValid();

    /**
     * Scores the section being analyzed, finding the score that the section
     * achieved with getScore() and the players that will receive the score with
     * getScoringPlayers() and adds the score to each of those players in the
     * provided playerScores array.
     *
     * This method is used for both incomplete and complete scores: getScore()
     * and getScoringPlayers() will adjust based on whether the section is
     * complete or not automatically. Consult each subclass of MeepleAnalysis
     * for more information on how each performs scoring.
     *
     * @param playerScores The array to add the scores of the scoring players
     *                     to. Since it is passed by reference, it is modified
     *                     in place.
     */
    public void tallyScores(int[] playerScores) {
        int score = getScore();
        for (int player : getScoringPlayers()) {
            playerScores[player] += score;
        }
    }

    /**
     * After the complete scoring the section, this method can be used to
     * return the meeples of the scoring players back to their number of meeples.
     * It searches through all visited sections and returns the meeples of
     * players that scored with getScoringPlayers().
     *
     * @param playerMeeples The array to add the meeples of the scoring players
     *                      back to. Since it is passed by reference, it is
     *                      modified in place.
     */
    public void returnMeeples(int[] playerMeeples) {
        // It doesn't make sense to return meeples if the stuff isn't complete.
        assert isClosed();

        HashSet<Integer> scoringPlayers = getScoringPlayers();

        // Search through ever section for meeples.
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

    /**
     * Internal method provided by each subclass. This returns a set of all the
     * sections that were visited in the analysis. The only sections that should
     * be included are ones that can contain meeples that should be removed in
     * returnMeeples(), which is not necessarily every section that was visited.
     *
     * This set must also be universal: analyses that started from one section
     * should yield the same visited sections as another analysis that started
     * from a different one of those sections.
     *
     * @return The set of sections visited by the analysis.
     */
    protected abstract HashSet<Section> getVisitedSections();

    /**
     * Internal method provided by each subclass. This calculates the score that
     * the sections analyzed should award. It should not take into account which
     * players will receive the score or if there are even any meeples at all.
     * It must automatically detect (when applicable) whether the section is
     * complete or not and adjust the score based on that as needed.
     *
     * @return The score awarded by the analyzed sections.
     */
    protected abstract int getScore();

    /**
     * Internal method provided by each subclass. This returns the set of players,
     * if any, that should receive points from the sections analyzed. If there are
     * no players, an empty set should be returned.
     *
     * @return The set of players that should receive points from the sections
     *         analyzed.
     */
    protected abstract HashSet<Integer> getScoringPlayers();

    /**
     * Internal method provided by each subclass. It does the main analysis of all the
     * sections and tiles on the board, providing all the information necessary for
     * every other method to work. It should be called by the constructor of each
     * section type subclass.
     */
    protected abstract void runAnalysis();

    /**
     * Constructor; initializes the shared instance variables of each meeple
     * analysis class.
     *
     * @param board        The board containing the tiles and sections being analyzed.
     * @param startSection The section to start the analysis from.
     */
    public MeepleAnalysis(Board board, Section startSection) {
        this.board = board;

        this.startTile = startSection.getParent();
        this.startSection = startSection;
    }

    /**
     * Factory constructor; given a section of any type, it constructs the proper
     * MeepleAnalysis subclass with the specified board and starting section and
     * returns it.
     *
     * @param board        The board containing the tiles and sections being analyzed.
     * @param startSection The section to start the analysis from.
     * @return The constructed analysis class.
     */
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

    /**
     * An interface for the analysis lambdas passed to analyzeTile() and
     * analyzeBoard() to use the results of each section analysis.
     */
    public interface Analyzer {
        void analyze(MeepleAnalysis analysis);
    }

    /**
     * Tile analysis method; this creates MeepleAnalysis classes to analyze every
     * section on the provided tile. The provided lambda is given the analysis
     * for each section, and then the lambda can use the results of the analysis
     * as needed.
     *
     * The main difference between this and simple loop over the sections in a
     * single tile is that this method takes into account separate sections being
     * connected on the same tile: for instance, a tile can have two separate cities
     * on the same tile, but these cities are actually connected through other tiles.
     * A simple loop would score this city twice, which is wrong. This method will
     * never analyze the same section twice.
     *
     * @param board    The board containing the tiles and sections being analyzed.
     * @param tile     The tile to analyze all the sections in.
     * @param analyzer The lambda for using the results of each analysis.
     */
    public static void analyzeTile(Board board, Tile tile, Analyzer analyzer) {
        analyzeTileLow(board, tile, analyzer, new HashSet<>());
    }

    /**
     * Board analysis class; this creates MeepleAnalysis classes to analyze
     * every section of every tile on the entire board. The provided lambda is
     * given the analysis for each section, and then the lambda can use the
     * results of the analysis as needed.
     *
     * Like analyzeTile(), this method makes sure to never analyze the same
     * section twice, and is therefore more robust for scoring than simple loops
     * over each tile and section on the board.
     *
     * @param board    The board containing the tiles and sections to analyze.
     * @param analyzer The lambda for using the results of each analysis.
     */
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

    /**
     * Helper method for analyzeBoard() and analyzeTile() that actually does the
     * work. Given a board, tile, and set of already visited sections, it iterates
     * over each section on the tile. If the section has not been visited yet, it
     * creates a MeepleAnalysis, adds the analysis's visited sections to the set of
     * visited sections, and then calls the lambda on the analysis.
     *
     * @param board           The board containing the tile to analyze.
     * @param tile            The tile currently being analyzed.
     * @param analyzer        The lambda for using the results of each analysis.
     * @param visitedSections The set of already visited sections from previous
     *                        calls of analyzeTileLow(), or an empty set if this is
     *                        the first call.
     */
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
