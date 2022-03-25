package com.example.carcassonne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

// TODO: Document
// TODO: Make sure externally-facing functions return copies of sets/arrays
public abstract class Analysis {
    protected BoardOLD board;
    protected TileOLD start;

    public abstract boolean isMeepleValid();

    public abstract int getCompleteScore(int player);
    public abstract int getIncompleteScore(int player);

    public abstract ArrayList<TileOLD> getCompletedMeeples();

    public Analysis(BoardOLD board) {
        this.board = board;
    }

    public static class Scorers {
        public int score;
        public HashSet<Integer> players;

        public Scorers() {
            this.score = 0;
            this.players = new HashSet<>();
        }
    }

    public static Scorers getScorers(ArrayList<TileOLD> meeples) {
        HashMap<Integer, Integer> playerMeeples = new HashMap<>();

        for (TileOLD tile : meeples) {
            int owner = tile.getOwner();
            playerMeeples.put(owner, getOrDefault(playerMeeples, owner, 0) + 1);
        }

        Scorers scorers = new Scorers();

        for (int other_player : playerMeeples.keySet()) {
            int score = playerMeeples.get(other_player);

            if (score > scorers.score) {
                // Clear the list because we have a new highest score
                scorers.score = score;
                scorers.players.clear();
            }
            scorers.players.add(other_player);
        }

        return scorers;
    }

    public static int getOrDefault(HashMap<Integer, Integer> map, int key, int def) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return def;
    }
}
