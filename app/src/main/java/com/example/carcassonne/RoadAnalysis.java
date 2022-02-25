package com.example.carcassonne;

import java.util.ArrayList;
import java.util.HashSet;

public class RoadAnalysis extends Analysis {
    private static class RoadEnd {
        public int x;
        public int y;
        public Tile tile;

        public RoadEnd(int x, int y, Tile tile) {
            this.x = x;
            this.y = y;
            this.tile = tile;
        }
    }

    private static class SubRoad {
        public boolean complete;
        public int tileCount;
        public ArrayList<Tile> meeples;

        public SubRoad() {
            this.complete = true;
            this.tileCount = 0;
            this.meeples = new ArrayList<>();
        }
    }

    private static class RoadPart {
        public Tile tile;
        public int part;

        public RoadPart(Tile tile, int part) {
            this.tile = tile;
            this.part = part;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof RoadPart)) {
                return false;
            }

            RoadPart that = (RoadPart) other;
            return this.tile == that.tile &&
                    this.part == that.part;
        }

        @Override
        public int hashCode() {
            return tile.hashCode() + part;
        }
    }

    private ArrayList<RoadEnd> roadEnds;
    private ArrayList<SubRoad> subRoads;
    private int meepleCount = 0;

    private HashSet<RoadPart> visited;

    @Override
    public boolean isMeepleValid() {
        if (this.start.getMeepleType() != Tile.TYPE_ROAD) {
            return this.meepleCount == 0;
        }
        return this.meepleCount == 1;
    }

    @Override
    public int getCompleteScore(int player) {
        return getSomeScore(player, true);
    }

    @Override
    public int getIncompleteScore(int player) {
        return getSomeScore(player, false);
    }

    @Override
    public ArrayList<Tile> getCompletedMeeples() {
        ArrayList<Tile> completed = new ArrayList<>();

        for (SubRoad subRoad : this.subRoads) {
            if (subRoad.complete) {
                completed.addAll(subRoad.meeples);
            }
        }

        return completed;
    }

    public void runAnalysis(int x, int y) {
        this.start = this.board.getTile(x, y);

        this.roadEnds = new ArrayList<>();
        this.subRoads = new ArrayList<>();
        this.meepleCount = 0;

        findRoadEnds(x, y, new HashSet<>());

        for (RoadEnd end : this.roadEnds) {
            for (int road : end.tile.getRoads()) {
                if (this.visited.contains(new RoadPart(end.tile, road))) {
                    continue;
                }

                SubRoad subRoad = new SubRoad();
                this.subRoads.add(subRoad);

                subRoadAnalysis(subRoad, end.x, end.y, road);
            }
        }
    }

    public RoadAnalysis(Board board) {
        super(board);

        this.visited = new HashSet<>();
    }

    private int getSomeScore(int player, boolean requiresComplete) {
        int totalScore = 0;

        for (SubRoad subRoad : this.subRoads) {
            if (subRoad.complete != requiresComplete) {
                continue;
            }

            Scorers scorers = Analysis.getScorers(subRoad.meeples);
            if (!scorers.players.contains(player)) {
                continue;
            }

            totalScore += subRoad.tileCount;
        }

        return totalScore;
    }

    private void findRoadEnds(int x, int y, HashSet<Tile> visited) {
        Tile tile = this.board.getTile(x, y);
        if (tile == null || visited.contains(tile)) {
            return;
        }
        visited.add(tile);

        HashSet<Integer> roads = tile.getRoads();

        // If a road does not have exactly two parts, it's either the direct end
        // of a road or an intersection that counts as the end of a road.
        if (roads.size() != 2) {
            this.roadEnds.add(new RoadEnd(x, y, tile));
        }

        for (int road : roads) {
            findRoadEnds(x + Tile.roadPartXOffset(road),
                    y + Tile.roadPartYOffset(road), visited);
        }
    }

    private void subRoadAnalysis(SubRoad subRoad, int x, int y, int part) {
        Tile tile = this.board.getTile(x, y);
        if (tile == null) {
            subRoad.complete = false;
            return;
        }

        HashSet<Integer> roads = tile.getRoads();

        int visitedPart = part;
        if (roads.size() == 2) {
            visitedPart = -1;
        }

        RoadPart roadPart = new RoadPart(tile, visitedPart);
        if (this.visited.contains(roadPart)) {
            return;
        }
        this.visited.add(roadPart);

        subRoad.tileCount++;
        if (tile.getMeepleType() == Tile.TYPE_ROAD) {
            // TODO: Thieves need to be on only one branch of the intersection.
            subRoad.meeples.add(tile);
            this.meepleCount++;
        }

        if (roads.size() == 2) {
            for (int road : roads) {
                subRoadAnalysis(subRoad, x + Tile.roadPartXOffset(road),
                        y + Tile.roadPartYOffset(road), Tile.flipRoadPart(part));
            }
        }
    }
}
