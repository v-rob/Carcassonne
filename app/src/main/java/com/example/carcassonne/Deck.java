package com.example.carcassonne;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a deck of tiles. It contains three separate parts: First, it has a master
 * set of tiles, which allows specific tiles to be created at will with createTile().
 * Secondly, it has the actual list of tiles in the deck, which can be drawn from until
 * empty and have random rotations. Thirdly, it has the special starting tile D that
 * is drawn separately from the rest of the tiles in the deck.
 *
 * @author Vincent Robinson
 */
public class Deck {
    /**
     * The "master list" of tiles, which is the list from which all other tiles are
     * copied from with createTile(). This allows specific tiles to be created at will,
     * especially for testing purposes, without requiring TileImageProvider to be kept
     * around.
     */
    private HashMap<Character, Tile> masterTiles;

    /**
     * The array of tiles in the deck, not including the starting tile. When tiles
     * are drawn, a random tile is chosen from anywhere in the array, which then
     * gets removed from the array directly.
     */
    private ArrayList<Tile> tiles;

    /**
     * The Carcassonne starting tile, which is always tile D. When the starting tile
     * is drawn, this is set to null.
     */
    private Tile startingTile;

    /**
     * Draws a tile from the deck at random. Once drawn, the tile is removed from the
     * deck entirely.
     *
     * @param owner The index of the player that drew this tile
     * @return The tile drawn from the deck.
     */
    public Tile drawTile(int owner) {
        Tile tile = this.tiles.remove((int)(Math.random() * this.tiles.size()));
        tile.setOwner(owner);
        return tile;
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
     * Create an entirely new tile of the specified ID as a copy from the master list.
     * It comes with the tile's default rotation, not a random rotation.
     *
     * @param id The ID of the tile to create, from 'A' to 'X'.
     * @return The newly created tile.
     */
    public Tile createTile(char id) {
        return new Tile(this.masterTiles.get(id));
    }

    /**
     * Converts the deck to a string representation showing all tiles in the deck.
     *
     * @return The string representation of the deck and all its tiles.
     */
    @Override
    public String toString() {
        ToStringer toStr = new ToStringer("Deck");

        toStr.add("tiles", this.tiles);
        toStr.add("startingTile", this.startingTile);

        return toStr.toString();
    }

    /**
     * Create a new deck by populating the master set from the image provider and then
     * making copies of the tiles and adding them to the list of tiles.
     *
     * @param bitmapProvider The image provider containing all the tile images.
     */
    public Deck(BitmapProvider bitmapProvider) {
        this.masterTiles = new HashMap<>();
        this.tiles = new ArrayList<>();

        // Create the master list of tiles.
        for (char id = 'A'; id <= 'X'; id++) {
            this.masterTiles.put(id, new Tile(id, bitmapProvider));
        }

        // Copy all the master tiles as many times as the tile appears according to
        // the manual.
        addTiles('A', 2);
        addTiles('B', 4);
        addTiles('C', 1);
        addTiles('D', 3); // One less than the manual states since D is the starting tile.
        addTiles('E', 5);
        addTiles('F', 2);
        addTiles('G', 1);
        addTiles('H', 3);
        addTiles('I', 2);
        addTiles('J', 3);
        addTiles('K', 3);
        addTiles('L', 3);
        addTiles('M', 2);
        addTiles('N', 3);
        addTiles('O', 2);
        addTiles('P', 3);
        addTiles('Q', 1);
        addTiles('R', 3);
        addTiles('S', 2);
        addTiles('T', 1);
        addTiles('U', 8);
        addTiles('V', 9);
        addTiles('W', 4);
        addTiles('X', 1);

        // There must be 71 tiles, not including the starting tile.
        assert this.tiles.size() == 71;

        // The starting tile is always D, so create it separately.
        this.startingTile = new Tile('D', bitmapProvider);
    }

    /**
     * Creates a new deck that is a deep copy of another deck.
     *
     * @param other The deck to make a deep copy of.
     */
    public Deck(Deck other) {
        this.masterTiles = Util.deepCopyMap(other.masterTiles, HashMap::new, Tile::new);
        this.tiles = Util.deepCopyCol(other.tiles, ArrayList::new, Tile::new);
        this.startingTile = Util.copyOrNull(other.startingTile, Tile::new);
    }

    /**
     * Create a specified number of tiles of a specific ID from the master list and add
     * them to the list of tiles, all with random rotations.
     *
     * @param id  The ID of the tiles to create.
     * @param num The number of tiles to create.
     */
    private void addTiles(char id, int num) {
        for (int i = 0; i < num; i++) {
            Tile created = createTile(id);
            // TODO created.rotateRandomly();
            this.tiles.add(created);
        }
    }

}
