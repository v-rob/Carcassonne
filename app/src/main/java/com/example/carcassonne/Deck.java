package com.example.carcassonne;

import java.util.ArrayList;

/**
 * Represents a deck of Tiles that can be drawn from until empty as well as the
 * special starting tile that can be retrieved separately.
 *
 * @author Vincent Robinson
 */
public class Deck {
    ArrayList<Tile> tiles;
    Tile startingTile;

    private void addTiles(char id, int num, int[][] farmSections,
                          int[][] citySections, int[] roads, boolean hasPennant,
                          boolean isCloister) {
        for (int i = 0; i < num; i++) {
            this.tiles.add(new Tile(id, farmSections, citySections, roads,
                    hasPennant, isCloister));
        }
    }

    private void addPennantTiles(char normalId, int normalNum, char pennantId,
                                 int pennantNum, int[][] farmSections, int[][] citySections,
                                 int[] roads) {
        addTiles(normalId, normalNum,
                farmSections, citySections, roads, false, false);
        addTiles(pennantId, pennantNum,
                farmSections, citySections, roads, true, false);
    }

    public Deck() {
        // Set a capacity of the number of tiles the deck will have.
        this.tiles = new ArrayList<>(71);

        // These are helpful constants for tiles that have simple layouts that are
        // shared with many other tiles.
        final int[][] ALL_SECTIONS = new int[][]{{0, 1, 2, 3, 4, 5, 6, 7}};
        final int[][] NO_SECTIONS = new int[][]{{}};
        final int[] NO_ROADS = new int[]{};

        addTiles('A', 2,
                ALL_SECTIONS,
                NO_SECTIONS,
                new int[]{2},
                false,
                true
        );

        addTiles('B', 4,
                ALL_SECTIONS,
                NO_SECTIONS,
                NO_ROADS,
                false,
                true
        );

        addTiles('C', 1,
                NO_SECTIONS,
                ALL_SECTIONS,
                NO_ROADS,
                true,
                false
        );

        addTiles('D', 4,
                new int[][]{
                        {1, 4},
                        {0, 5, 6, 7}
                },
                new int[][]{{2, 3}},
                new int[]{0, 2},
                false,
                false
        );

        // The starting tile is special: it's always tile D, so we remove one from
        // the ArrayList and set it as the starting tile.
        this.startingTile = this.tiles.remove(this.tiles.size() - 1);

        addTiles('E', 5,
                new int[][]{{2, 3, 4, 5, 6, 7}},
                new int[][]{{0, 1}},
                NO_ROADS,
                false,
                false
        );

        addTiles('F', 2,
                new int[][]{
                        {0, 1},
                        {4, 5}
                },
                new int[][]{{2, 3, 6, 7}},
                NO_ROADS,
                true,
                false
        );

        addTiles('G', 1,
                new int[][]{
                        {2, 3},
                        {6, 7}
                },
                new int[][]{{0, 1, 4, 5}},
                NO_ROADS,
                false,
                false
        );

        addTiles('H', 3,
                new int[][]{{0, 1, 4, 5}},
                new int[][]{
                        {2, 3},
                        {6, 7}
                },
                NO_ROADS,
                false,
                false
        );

        addTiles('I', 2,
                new int[][]{{0, 1, 6, 7}},
                new int[][]{
                        {2, 3},
                        {4, 5},
                },
                NO_ROADS,
                false,
                false
        );

        addTiles('J', 3,
                new int[][]{
                        {2, 5, 6, 7},
                        {3, 4}
                },
                new int[][]{{0, 1}},
                new int[]{1, 2},
                false,
                false
        );

        addTiles('K', 3,
                new int[][]{
                        {1, 4, 5, 6},
                        {7, 0}
                },
                new int[][]{{2, 3}},
                new int[]{0, 3},
                false,
                false
        );

        addTiles('L', 3,
                new int[][]{
                        {1, 4},
                        {5, 6},
                        {7, 0}
                },
                new int[][]{{2, 3}},
                new int[]{0, 2, 3},
                false,
                false
        );

        addPennantTiles('M', 2, 'N', 3,
                new int[][]{{2, 3, 4, 5}},
                new int[][]{{6, 7, 0, 1}},
                NO_ROADS
        );

        addPennantTiles('O', 2, 'P', 3,
                new int[][]{
                        {2, 5},
                        {3, 4}
                },
                new int[][]{{6, 7, 0, 1}},
                new int[]{1, 2}
        );

        addPennantTiles('Q', 1, 'R', 3,
                new int[][]{{4, 5}},
                new int[][]{{6, 7, 0, 1, 2, 3}},
                NO_ROADS
        );

        addPennantTiles('S', 2, 'T', 1,
                new int[][]{
                        {4},
                        {5}
                },
                new int[][]{{6, 7, 0, 1, 2, 3}},
                new int[]{2}
        );

        addTiles('U', 8,
                new int[][]{
                        {1, 2, 3, 4},
                        {5, 6, 7, 0},
                },
                NO_SECTIONS,
                new int[]{0, 2},
                false,
                false
        );

        addTiles('V', 9,
                new int[][]{
                        {7, 0, 1, 2, 3, 4},
                        {5, 6}
                },
                NO_SECTIONS,
                new int[]{2, 3},
                false,
                false
        );

        addTiles('W', 4,
                new int[][]{
                        {7, 0, 1, 2},
                        {3, 4},
                        {5, 6}
                },
                NO_SECTIONS,
                new int[]{1, 2, 3},
                false,
                false
        );

        addTiles('X', 1,
                new int[][]{
                        {1, 2},
                        {3, 4},
                        {5, 6},
                        {7, 0}
                },
                NO_SECTIONS,
                new int[]{0, 1, 2, 3},
                false,
                false
        );

        // This is 71 instead of 72 because the starting tile is not stored in the ArrayList.
        assert this.tiles.size() == 71;
    }

    public Deck(Deck other) {
        int size = other.tiles.size();
        this.tiles = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            this.tiles.set(i, new Tile(other.tiles.get(i)));
        }

        this.startingTile = new Tile(other.startingTile);
    }

    public Tile drawTile() {
        return this.tiles.remove((int)(Math.random() * this.tiles.size()));
    }

    public Tile drawStartingTile() {
        Tile ret = this.startingTile;
        this.startingTile = null;
        return ret;
    }

    public boolean isEmpty() {
        return this.tiles.size() == 0;
    }
}
