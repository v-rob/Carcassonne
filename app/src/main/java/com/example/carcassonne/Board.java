package com.example.carcassonne;

import java.util.HashSet;

/**
 * Represents the "board" of tiles, i.e. all the tiles in play, including the special
 * current tile that is not fully committed to the board, if there is one. Any positions
 * not containing a tile are null.
 *
 * The board always has a border of one empty tile on all sides of the board to allow
 * placement of the current tile there. This border will be reflected in getWidth() and
 * getHeight(). The board grows in each direction as necessary when tiles are added inside
 * this border.
 *
 * All logic for tile and placement and scoring takes place in the Analysis class and
 * its subclasses in this file, which get accessed through methods in Board.
 *
 * @author Vincent Robinson
 */
public class Board {
    /**
     * The array of tiles in the board, including the empty border around the board. If
     * there is no tile at some position, that tile is null. The tile currently being
     * placed is not stored in this array, but rather* stored as a separate instance
     * variable.
     */
    private Tile[][] tiles;

    /**
     * The tile currently being placed, or null if none. The current tile should never
     * overlap other tiles.
     */
    private Tile currentTile;
    /**
     * The X position of the current tile in the tiles array. If the current tile has not
     * yet been placed down or if there is no current tile, it contains -1.
     */
    private int currentTileX;
    /**
     * The Y position of the current tile in the tiles array. If the current tile has not
     * yet been placed down or if there is no current tile, it contains -1.
     */
    private int currentTileY;

    /**
     * Queries the width of the board, including the empty border.
     *
     * @return The width of the board in tiles.
     */
    public int getWidth() {
        return this.tiles[0].length;
    }

    /**
     * Queries the height of the board, including the empty border.
     *
     * @return The height of the board in tiles.
     */
    public int getHeight() {
        return this.tiles.length;
    }

    /**
     * Gets a tile on the board with bounds checking, including the current tile.
     *
     * @param x The X position of the tile to get.
     * @param y The Y position of the tile to get.
     * @return The tile if there is one, or null if there is no tile or the specified
     *         position is out of bounds.
     */
    public Tile getTile(int x, int y) {
        if (x == this.currentTileX && y == this.currentTileY) {
            return this.currentTile;
        }
        return getConfirmedTile(x, y);
    }

    /**
     * Gets a tile on the board with bounds checking, NOT including the current tile.
     *
     * @param x The X position of the tile to get.
     * @param y The Y position of the tile to get.
     * @return The tile if there is one, or null if there is no tile or the specified
     *         position is out of bounds.
     */
    public Tile getConfirmedTile(int x, int y) {
        if (outOfBounds(x, y)) {
            return null;
        }
        return this.tiles[y][x];
    }

    /**
     * Returns the tile currently being placed, or null if there is no such tile.
     *
     * @return The tile currently being placed.
     */
    public Tile getCurrentTile() {
        return this.currentTile;
    }

    /**
     * Returns the X position of the tile currently being placed, or -1 if there is
     * no such tile or the tile has not been placed down yet.
     *
     * @return The X position of the tile being placed, or -1 if no position.
     */
    public int getCurrentTileX() {
        return this.currentTileX;
    }

    /**
     * Returns the Y position of the tile currently being placed, or -1 if there is
     * no such tile or the tile has not been placed down yet.
     *
     * @return The Y position of the tile being placed, or -1 if no position.
     */
    public int getCurrentTileY() {
        return this.currentTileY;
    }

    /**
     * Sets the tile currently being placed to a new tile. The tile will be given no
     * position, i.e. an X and Y position of -1.
     *
     * @param tile The tile to set as the current tile.
     */
    public void setCurrentTile(Tile tile) {
        this.currentTile = tile;
        resetCurrentTilePosition();
    }

    /**
     * Sets the tile currently being placed to a new position. It is an error if the
     * position is already occupied.
     *
     * @param x The X position to give the tile.
     * @param y The Y position to give the tile.
     */
    public void setCurrentTilePosition(int x, int y) {
        // Ensure this position is not already taken so we don't run into bugs later.
        assert getConfirmedTile(x, y) == null;

        this.currentTileX = x;
        this.currentTileY = y;
    }

    /**
     * Resets the X and Y position of the current tile to -1.
     */
    public void resetCurrentTilePosition() {
        this.currentTileX = this.currentTileY = -1;
    }

    /**
     * Confirms the current tile placement, moving the current tile into the board
     * array and resizing it if necessary. The current tile is then set to null and
     * the X and Y positions set to -1.
     *
     * It is an error if the current tile placement or meeple placement on the tile
     * are invalid. They can be checked with isCurrentTilePlacementValid() and
     * isCurrentMeeplePlacementValid().
     */
    public void confirmCurrentTile() {
        // Ensure the tile is valid so we don't run into bugs later.
        assert isCurrentPlacementValid();

        // Insert the current tile into the array and reset the current tile
        this.tiles[this.currentTileY][this.currentTileX] = this.currentTile;
        this.currentTile = null;
        resetCurrentTilePosition();

        // Booleans for checking if the tile was placed in the empty border
        boolean incLeft = this.currentTileX == 0;
        boolean incTop  = this.currentTileY == 0;

        boolean incX = incLeft || this.currentTileX == getWidth() - 1;
        boolean incY = incTop  || this.currentTileX == getWidth() - 1;

        // Resize the tile array if necessary
        if (incX || incY) {
            Tile[][] copy = new Tile[getHeight() + (incY ? 1 : 0)]
                    [getWidth() + (incX ? 1 : 0)];

            for (int y = 0; y < getHeight(); y++) {
                for (int x = 0; x < getWidth(); x++) {
                    // If we inserted to the top or left, we need to add an offset in
                    // the destination array.
                    copy[y + (incTop ? 1 : 0)][x + (incLeft ? 1 : 0)] = this.tiles[y][x];
                }
            }

            // Replace the original tile array with the copy.
            this.tiles = copy;
        }
    }

    /**
     * Queries whether the position of the current tile is valid. This is subject to
     * three requirements:
     * - The tile is in bounds for the board.
     * - The tile has at least one adjacent tile.
     * - All adjacent tiles match up with the current tile.
     *
     * @return True if the current tile placement is valid, false otherwise.
     */
    public boolean isCurrentTilePlacementValid() {
        // Out-of-bounds tiles are never valid.
        if (outOfBounds(this.currentTileX, this.currentTileY)) {
            return false;
        }

        // Check all possible surrounding positions for validity in clockwise order
        // starting from the top
        Board.AdjacentValidation adjacent = new Board.AdjacentValidation();

        // Refer to the documentation for Tile for the meaning of these magical
        // constants.
        isAdjacentValid(adjacent, 0, -1, 0, 1, 0);
        isAdjacentValid(adjacent, 1,  0, 2, 3, 1);
        isAdjacentValid(adjacent, 0,  1, 4, 5, 2);
        isAdjacentValid(adjacent, -1, 0, 6, 7, 3);

        // We can only place the tile if there is another tile next to this one and
        // if all farms, cities, and roads match up.
        return adjacent.found && adjacent.isValid;
    }

    /**
     * Queries whether the meeple placement on the current tile is valid. If there is
     * no meeple, it is always valid. It is an error if the current tile placement is
     * invalid.
     *
     * @return True if the meeple placement is valid, false otherwise.
     */
    public boolean isCurrentMeeplePlacementValid() {
        // Ensure the tile placement is valid so we don't run into weird bugs in
        // the meeple placement code.
        assert isCurrentTilePlacementValid();

        if (!this.currentTile.hasMeeple()) {
            return true;
        }

        int type = this.currentTile.getMeepleSection().getType();

        if (type == Tile.TYPE_NONE || type == Tile.TYPE_CLOISTER) {
            // If there's no meeple or it's a monk, the placement is always valid.
            // It should be impossible for a monk to ever be placed on a tile without
            // a cloister.
            return true;
        }
        else if (type == Tile.TYPE_ROAD) {
            // If there's only one road meeple anywhere along this road, it must be
            // the one on this tile.
            return 1 == countRoadMeeples(this.currentTileX, this.currentTileY,
                    new HashSet<>());
        }

        // This must be a farm or city meeple: check all parts of the section it's in
        // for other meeples.

        // Make one shared visited set across all calls to ensure everything is counted
        // exactly once.
        HashSet<Section> visited = new HashSet<>();

        int total = 0;
        for (int part : this.currentTile.getMeepleSection().getParts()) {
            total += countSectionMeeples(type, this.currentTileX, this.currentTileY,
                    part, visited);
        }

        // If there's only one city/farm meeple in any of the connected sections, it
        // must be the one on this tile.
        return total == 1;
    }

    /**
     * Convenience function to check if both tile and meeple placements are valid.
     *
     * @return True if everything is valid, false otherwise.
     */
    public boolean isCurrentPlacementValid() {
        return isCurrentTilePlacementValid() && isCurrentMeeplePlacementValid();
    }

    /**
     * Converts the deck to a string representation showing all instance variables
     * and the positions of all tiles on the board.
     *
     * @return The string representation of the board and all its tiles.
     */
    @Override
    public String toString() {
        ToStringer toStr = new ToStringer("Board");

        toStr.add("currentTile", this.currentTile);
        toStr.add("currentTileX", this.currentTileX);
        toStr.add("currentTileY", this.currentTileY);

        toStr.add("getWidth()", getWidth());
        toStr.add("getHeight()", getHeight());

        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                // Print the coordinates of each non-null tile on the board.
                Tile tile = this.tiles[y][x];
                if (tile != null) {
                    toStr.add("tiles[" + y + "][" + x + "]", tile);
                }
            }
        }

        return toStr.toString();
    }

    /**
     * Creates a new board with space for a single tile and the empty border.
     *
     * @param startingTile The tile to place in the middle of the board. It should
     *                     be retrieved with Deck.drawStartingTile().
     */
    public Board(Tile startingTile) {
        this.tiles = new Tile[3][3];
        this.tiles[1][1] = startingTile;

        this.currentTile = null;
        resetCurrentTilePosition();
    }

    /**
     * Creates a new board that is a deep copy of another board.
     *
     * @param other The board to make a deep copy of.
     */
    public Board(Board other) {
        this.tiles = Util.deepCopyNested(other.tiles, Tile::new);

        this.currentTile = Util.copyOrNull(other.currentTile, Tile::new);
        this.currentTileX = other.currentTileX;
        this.currentTileY = other.currentTileY;
    }

    /**
     * Queries whether the specified position is out of bounds for the board array.
     *
     * @param x The X position to query boundedness of.
     * @param y The Y position to query boundedness of.
     * @return True if the position is out of bounds, false if in bounds.
     */
    private boolean outOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= getWidth() || y >= getHeight();
    }

    /**
     * Helper class for isAdjacentValid(). It is necessary because isAdjacentValid()
     * needs to share two pieces of information with other calls to that function:
     * is there another tile anywhere, and are the adjacent tiles valid?
     */
    private static class AdjacentValidation {
        /**
         * True if an adjacent tile was found. Defaults to false until proven that
         * there is one by a call to isAdjacentValid().
         */
        public boolean found = false;

        /**
         * True if this tile is valid when placed next to all adjacent tiles. Defaults
         * to true until proven by isAdjacentValid() that at least one adjacent tile
         * does not match up with this one.
         */
        public boolean isValid = true;
    }

    /**
     * Checks if there is a tile adjacent to the current tile and if the borders of the
     * tile match up. It is a helper method for isCurrentTilePlacementValid().
     *
     * @param adjacent   The object for storing the results of the function across
     *                   multiple calls. An object should be created and passed to every
     *                   call of this function, and the results checked afterwards.
     * @param xOffset    The X offset from the current tile to the adjacent tile.
     * @param yOffset    The Y offset from the current tile to the adjacent tile.
     * @param firstPart  One tile part on the current tile to validate with the adjacent
     *                   tile's tile part on the opposite side.
     * @param secondPart The other tile part on the current tile to validate with the
     *                   adjacent tile's tile part on the opposite side.
     * @param roadPart   The road part on the current tile to validate with the adjacent
     *                   tile's road part on the opposite side.
     */
    private void isAdjacentValid(Board.AdjacentValidation adjacent, int xOffset, int yOffset,
                                 int firstPart, int secondPart, int roadPart) {
        Tile tile = getConfirmedTile(this.currentTileX + xOffset,
                this.currentTileY + yOffset);
        if (tile == null) {
            // There's no tile: nothing has changed.
            return;
        }

        // If the tile exists, an adjacent tile has been found
        adjacent.found = true;

        // Check whether the types of sections match up, i.e. farm to farm and
        // city to city.
        adjacent.isValid &= this.currentTile.getSection(firstPart).getType() ==
                tile.getSection(Tile.flipPart(firstPart)).getType();
        adjacent.isValid &= this.currentTile.getSection(secondPart).getType() ==
                tile.getSection(Tile.flipPart(secondPart)).getType();

        // Check whether the roads match up.
        adjacent.isValid &= (this.currentTile.getRoadSection(roadPart) == null) ==
                (tile.getRoadSection(Tile.flipRoadPart(roadPart)) == null);
    }

    /**
     * Recursive function to count all meeples on any roads connected to the tile
     * at the specified position. It is useful to tell whether road meeple placement
     * is valid or not.
     *
     * @param x       The X position of the tile to check for this recursive call.
     * @param y       The Y position of the tile to check for this recursive call.
     * @param visited A set of all tiles that have already been visited. This is
     *                necessary to ensure tiles don't get counted multiple times in
     *                an infinite recursion loop.
     * @return The number of meeples anywhere on the road.
     */
    private int countRoadMeeples(int x, int y, HashSet<Tile> visited) {
        Tile tile = getTile(x, y);

        // Ignore non-existent tiles and don't count tiles we've already searched
        // through so that we don't run into infinite recursion.
        if (tile == null || visited.contains(tile)) {
            return 0;
        }
        visited.add(tile);

        int total = 0;

        // Add the meeple from the current tile if it's a road meeple
        if (tile.getMeepleSection().getType() == Tile.TYPE_ROAD) {
            total++;
        }

        // Run this same function on all adjacent tiles connected to this tile
        // via roads.
        for (Section section : tile.getSections()) {
            if (section.getType() != Tile.TYPE_ROAD) {
                continue;
            }

            for (int other_road : section.getParts()) {
                total += countRoadMeeples(x + Tile.roadPartXOffset(other_road),
                        y + Tile.roadPartYOffset(other_road), visited);
            }
        }

        return total;
    }

    /**
     * Recursive function to count all meeples on any sections (city or farm) connected
     * to the specified section on this tile. It is useful to tell whether city or farm
     * meeple placement is valid.
     *
     * @param type    The type of the section, either city or farm.
     * @param x       The X position of the tile to check for this recursive call.
     * @param y       The Y position of the tile to check for this recursive call.
     * @param part    The part contained in the section to search.
     * @param visited A set of all tile sections that have already been visited. This
     *                is necessary to ensure sections don't get counted multiple times
     *                in an infinite recursion loop.
     * @return The number of meeples in the current section or any sections connected to
     *         it.
     */
    private int countSectionMeeples(int type, int x, int y, int part,
                                    HashSet<Section> visited) {
        Tile tile = getTile(x, y);
        if (tile == null) {
            // Ignore non-existent tiles
            return 0;
        }

        Section section = tile.getSection(part);

        // Don't count tiles we've already searched through so that we don't run
        // into infinite recursion.
        if (visited.contains(section)) {
            return 0;
        }
        visited.add(section);

        int total = 0;

        // Add the meeple from the current tile if it's in our section.
        if (tile.getMeepleSection() == section) {
            total++;
        }

        // Run this same function on all adjacent tiles connected to this section.
        for (Section other_section : tile.getSections()) {
            if (section.isFarmOrCity()) {
                continue;
            }

            for (int other_part : other_section.getParts()) {
                total += countSectionMeeples(type, x + Tile.partXOffset(other_part),
                        y + Tile.partYOffset(other_part), Tile.flipPart(other_part),
                        visited);
            }
        }

        return total;
    }
}
