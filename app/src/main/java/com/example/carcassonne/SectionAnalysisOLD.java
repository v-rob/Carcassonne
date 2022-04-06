package com.example.carcassonne;

import java.util.ArrayList;
import java.util.HashSet;

// IN PROGRESS: NOT TO BE USED YET
public class SectionAnalysisOLD extends AnalysisOLD {
    private int type;
    private int pennantCount;
    private boolean complete;

    private ArrayList<TileOLD> meeples;

    private HashSet<HashSet<Integer>> visited;

    @Override
    public boolean isMeepleValid() {
        int total = this.meeples.size();
        if (this.start.getMeepleType() != this.type) {
            return total == 0;
        }
        return total == 1;
    }

    @Override
    public int getCompleteScore(int player) {
        return getSomeScore(player, this.complete, 2);
    }

    @Override
    public int getIncompleteScore(int player) {
        return getSomeScore(player, !this.complete, 1);
    }

    @Override
    public ArrayList<TileOLD> getCompletedMeeples() {
        if (this.complete) {
            return new ArrayList<>(this.meeples);
        }
        return new ArrayList<>();
    }

    public void runAnalysis(int x, int y, int part) {
        this.start = this.board.getTile(x, y);

        this.type = this.start.getMeepleType();
        this.pennantCount = 0;

        // We assume the city/farm is completed until we call doAnalysis on a
        // position with no tile, in which case we know it is not complete.
        this.complete = true;

        this.meeples = new ArrayList<>();
    }

    public SectionAnalysisOLD(BoardOLD board) {
        super(board);

        // For each analysis object, tiles are not checked twice even across
        // calls to runAnalysis().
        this.visited = new HashSet<>();
    }

    private int getSomeScore(int player, boolean isValid, int multiplier) {
        if (!isValid) {
            return 0;
        }

        Scorers scorers = AnalysisOLD.getScorers(this.meeples);
        if (!scorers.players.contains(player)) {
            return 0;
        }

        if (type == TileOLD.TYPE_CITY) {
            // If this is a city, add the pennants to the score. If the city is
            // complete, the multiplier will cause us to score twice the amount.
            return (scorers.score + this.pennantCount) * multiplier;
        }
        return scorers.score + this.pennantCount;
    }

    private void doAnalysis(int x, int y, int part) {
        TileOLD tile = this.board.getTile(x, y);

        if (tile == null) {
            this.complete = false;
            return;
        }

        HashSet<Integer> section = tile.getSectionFromPart(part);
        if (this.visited.contains(section)) {
            return;
        }
        this.visited.add(section);

        if (tile.getMeepleType() == this.type) {
            this.meeples.add(tile);
        }

        if (tile.hasPennant()) {
            this.pennantCount++;
        }

        for (int other_part : section) {
            doAnalysis(x + TileOLD.partXOffset(other_part),
                    y + TileOLD.partYOffset(other_part), other_part);
        }
    }
}
