package com.example.carcassonne;

import java.util.ArrayList;

/**
 * Represents a deck of Tiles that can be drawn from until empty. The Deck also
 * contains the special starting tile, which is drawn separately from the rest
 * of the tiles.
 *
 * @author Vincent Robinson
 */
public class Deck {
    /**
     * The array of tiles in the deck, not including the starting tile. When tiles
     * are drawn, a random tile is chosen from anywhere in the array, which then
     * gets removed from the array directly.
     */
    ArrayList<Tile> tiles;

    /**
     * The Carcassonne starting tile, which is always tile D. When the starting tile
     * is drawn, this is set to null.
     */
    Tile startingTile;

    /**
     * Draws a tile from the deck at random. Once drawn, the tile is removed from the
     * deck entirely.
     *
     * @return The tile drawn from the deck.
     */
    public Tile drawTile() {
        return this.tiles.remove((int)(Math.random() * this.tiles.size()));
    }

    /**
     * Draws the starting tile from the deck. Once drawn, the tile is removed from the
     * deck entirely.
     *
     * @return The starting tile drawn from the deck.
     */
    public Tile drawStartingTile() {
        Tile ret = this.startingTile;
        this.startingTile = null;
        return ret;
    }

    /**
     * Queries whether or not there are any more tiles left in the deck.
     *
     * @return True if there are no more tiles in the deck, false otherwise.
     */
    public boolean isEmpty() {
        return this.tiles.size() == 0 && this.startingTile == null;
    }

    /**
     * Converts the deck to a string representation showing all tiles in the deck.
     *
     * @return The string representation of the deck and all its tiles.
     */
    @Override
    public String toString() {
        String str = "Deck {\n" +
                "    tiles = {";

        for (int i = 0; i < this.tiles.size(); i++) {
            str += Util.indent(this.tiles.get(i).toString()) + "\n";
            if (i != this.tiles.size() - 1) {
                str += ",\n";
            }
        }
        str += "    startingTile = " +
                Util.indent(this.startingTile.toString()) + "\n}";

        str += "}";
        return str;
    }

    /**
     * Creates a new deck with all the default tiles in it.
     */
    public Deck() {
        populateTiles();
    }

    /**
     * Creates a new deck that is a deep copy of another deck.
     *
     * @param other The deck to make a deep copy of.
     */
    public Deck(Deck other) {
        int size = other.tiles.size();
        this.tiles = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            this.tiles.set(i, new Tile(other.tiles.get(i)));
        }

        if (this.startingTile != null) {
            this.startingTile = new Tile(other.startingTile);
        }
        else {
            this.startingTile = null;
        }
    }

    /**
     * Adds multiple copies of a tile to the deck during populateTiles().
     *
     * @param id           The character id of the tile.
     * @param num          The number of times to add this tile.
     * @param farmSections The parts of the tile that comprise each farm section.
     * @param citySections The parts of the tile that comprise each city section.
     * @param roads        The road parts for each road exiting the tile.
     * @param hasPennant   Whether or not the tile has a pennant for the city.
     * @param hasCloister  Whether or not the tile has a cloister.
     */
    private void addTiles(char id, int num, int[][] farmSections,
                          int[][] citySections, int[] roads, boolean hasPennant,
                          boolean hasCloister) {
        for (int i = 0; i < num; i++) {
            this.tiles.add(new Tile(id, farmSections, citySections, roads,
                    hasPennant, hasCloister));
        }
    }


    /**
     * Adds multiple copies of a tile that has both pennant and non-pennant varieties
     * to the deck during populateTiles().
     *
     * @param normalId     The character id of the tile variant without a pennant.
     * @param normalNum    The number of times to add the tile variant without a pennant.
     * @param pennantId    The character id of the tile variant with a pennant.
     * @param pennantNum   The number of times to add the tile variant with a pennant.
     * @param farmSections The parts of the tile that comprise each farm section.
     * @param citySections The parts of the tile that comprise each city section.
     * @param roads        The road parts for each road exiting the tile.
     */
    private void addPennantTiles(char normalId, int normalNum, char pennantId,
                                 int pennantNum, int[][] farmSections, int[][] citySections,
                                 int[] roads) {
        addTiles(normalId, normalNum,
                farmSections, citySections, roads, false, false);
        addTiles(pennantId, pennantNum,
                farmSections, citySections, roads, true, false);
    }

    /**
     * During construction, populates the deck with the default set of 72 tiles
     * in Carcassonne.
     */
    private void populateTiles() {
        // Set a capacity of the number of tiles the deck will have. This is 71
        // instead of 72 because the starting tile is not stored in the ArrayList.
        this.tiles = new ArrayList<>(71);

        // These are helpful constants for tiles that have simple layouts that are
        // shared with many other tiles.
        final int[][] ALL_SECTIONS = new int[][]{{0, 1, 2, 3, 4, 5, 6, 7}};
        final int[][] NO_SECTIONS = new int[][]{{}};
        final int[] NO_ROADS = new int[]{};

        addTiles('a', 2,
                ALL_SECTIONS,
                NO_SECTIONS,
                new int[]{2},
                false,
                true
        );

        addTiles('b', 4,
                ALL_SECTIONS,
                NO_SECTIONS,
                NO_ROADS,
                false,
                true
        );

        addTiles('c', 1,
                NO_SECTIONS,
                ALL_SECTIONS,
                NO_ROADS,
                true,
                false
        );

        addTiles('d', 4,
                new int[][]{
                        {1, 4},
                        {0, 5, 6, 7}
                },
                new int[][]{{2, 3}},
                new int[]{0, 2},
                false,
                false
        );

        // The starting tile is special: it's always tile D, so we remove one of the
        // just created ones from the ArrayList and set it as the starting tile.
        this.startingTile = this.tiles.remove(this.tiles.size() - 1);

        addTiles('e', 5,
                new int[][]{{2, 3, 4, 5, 6, 7}},
                new int[][]{{0, 1}},
                NO_ROADS,
                false,
                false
        );

        addTiles('f', 2,
                new int[][]{
                        {0, 1},
                        {4, 5}
                },
                new int[][]{{2, 3, 6, 7}},
                NO_ROADS,
                true,
                false
        );

        addTiles('g', 1,
                new int[][]{
                        {2, 3},
                        {6, 7}
                },
                new int[][]{{0, 1, 4, 5}},
                NO_ROADS,
                false,
                false
        );

        addTiles('h', 3,
                new int[][]{{0, 1, 4, 5}},
                new int[][]{
                        {2, 3},
                        {6, 7}
                },
                NO_ROADS,
                false,
                false
        );

        addTiles('i', 2,
                new int[][]{{0, 1, 6, 7}},
                new int[][]{
                        {2, 3},
                        {4, 5},
                },
                NO_ROADS,
                false,
                false
        );

        addTiles('j', 3,
                new int[][]{
                        {2, 5, 6, 7},
                        {3, 4}
                },
                new int[][]{{0, 1}},
                new int[]{1, 2},
                false,
                false
        );

        addTiles('k', 3,
                new int[][]{
                        {1, 4, 5, 6},
                        {7, 0}
                },
                new int[][]{{2, 3}},
                new int[]{0, 3},
                false,
                false
        );

        addTiles('l', 3,
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

        addPennantTiles('m', 2, 'n', 3,
                new int[][]{{2, 3, 4, 5}},
                new int[][]{{6, 7, 0, 1}},
                NO_ROADS
        );

        addPennantTiles('o', 2, 'p', 3,
                new int[][]{
                        {2, 5},
                        {3, 4}
                },
                new int[][]{{6, 7, 0, 1}},
                new int[]{1, 2}
        );

        addPennantTiles('q', 1, 'r', 3,
                new int[][]{{4, 5}},
                new int[][]{{6, 7, 0, 1, 2, 3}},
                NO_ROADS
        );

        addPennantTiles('s', 2, 't', 1,
                new int[][]{
                        {4},
                        {5}
                },
                new int[][]{{6, 7, 0, 1, 2, 3}},
                new int[]{2}
        );

        addTiles('u', 8,
                new int[][]{
                        {1, 2, 3, 4},
                        {5, 6, 7, 0},
                },
                NO_SECTIONS,
                new int[]{0, 2},
                false,
                false
        );

        addTiles('v', 9,
                new int[][]{
                        {7, 0, 1, 2, 3, 4},
                        {5, 6}
                },
                NO_SECTIONS,
                new int[]{2, 3},
                false,
                false
        );

        addTiles('w', 4,
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

        addTiles('x', 1,
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

        // Basic check to ensure we have the right number of tiles.
        assert this.tiles.size() == 71;
    }
}
