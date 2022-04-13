package com.example.carcassonne;

import java.util.ArrayList;

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
 * @author Sophie Arcangel
 * @author DJ Backus
 * @author Alex Martinez-Lopez
 * @author Vincent Robinson
 * @author Cheyanne Yim
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
     * Queries whether the specified position is out of bounds for the board array.
     *
     * @param x The X position to query boundedness of.
     * @param y The Y position to query boundedness of.
     * @return True if the position is out of bounds, false if in bounds.
     */
    public boolean isOutOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= getWidth() || y >= getHeight();
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
        if (this.currentTile != null &&
                x == this.currentTile.getX() && y == this.currentTile.getY()) {
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
        if (isOutOfBounds(x, y)) {
            return null;
        }
        return this.tiles[y][x];
    }

    /**
     * Manually places a tile directly on the board. It does not go through validity
     * checking, but just places it verbatim. Generally useful for internal functions
     * and unit testing rather than a normal external interface.
     *
     * @param x    The X position to place the tile at.
     * @param y    The Y position to place the tile at.
     * @param tile The tile to place at that position.
     */
    public void setTileDirectly(int x, int y, Tile tile) {
        tile.setPosition(x, y);
        this.tiles[y][x] = tile;
    }

    /**
     * Checks if the specified position has an adjacent tile in any of the four
     * cardinal directions. There does not need to be a tile at the specified position.
     *
     * @param x The X position to check for adjacent tiles around.
     * @param y The Y position to check for adjacent tiles around.
     * @return True if there is an adjacent tile, false otherwise.
     */
    public boolean hasAdjacentTile(int x, int y) {
        return getTile(x, y - 1) != null ||
                getTile(x + 1, y) != null ||
                getTile(x, y + 1) != null ||
                getTile(x - 1, y) != null;
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
     * Sets the tile currently being placed to a new tile. The tile will be given no
     * position, i.e. an X and Y position of -1.
     *
     * @param tile The tile to set as the current tile.
     */
    public void setCurrentTile(Tile tile) {
        this.currentTile = tile;
    }

    /**
     * Confirms the current tile placement, moving the current tile into the board
     * array and resizing it if necessary. The current tile is then set to null and
     * the X and Y positions set to -1.
     *
     * It is an error if the current tile placement or meeple placement on the tile
     * are invalid. They can be checked with isCurrentPlacementValid().
     */
    public void confirmCurrentTile() {
        // Ensure the tile is valid so we don't run into bugs later.
        assert isCurrentPlacementValid();
        
        int currentX = this.currentTile.getX();
        int currentY = this.currentTile.getY();

        // Insert the current tile into the array.
        this.tiles[currentY][currentX] = this.currentTile;

        // Booleans for checking if the tile was placed in the empty border
        boolean incLeft = currentX == 0;
        boolean incTop  = currentY == 0;

        boolean incX = incLeft || currentX == getWidth() - 1;
        boolean incY = incTop  || currentY == getHeight() - 1;

        // Resize the tile array if necessary
        if (incX || incY) {
            Tile[][] old = this.tiles;
            this.tiles = new Tile[getHeight() + (incY ? 1 : 0)]
                    [getWidth() + (incX ? 1 : 0)];

            for (int y = 0; y < old.length; y++) {
                for (int x = 0; x < old[y].length; x++) {
                    Tile tile = old[y][x];
                    if (tile == null) {
                        continue;
                    }

                    // If we inserted to the top or left, we need to add an offset in
                    // the destination array.
                    setTileDirectly(
                            tile.getX() + (incLeft ? 1 : 0),
                            tile.getY() + (incTop ? 1 : 0),
                            tile
                    );
                }
            }
        }

        // Reset the current tile to null and no position.
        this.currentTile = null;
    }

    /**
     * Queries whether the position of the current tile is valid. This is subject to
     * three requirements:
     * - The tile is in bounds for the board and does not overlap with an existing tile.
     * - The tile has at least one adjacent tile.
     * - All adjacent tiles match up with the current tile.
     *
     * @return True if the current tile placement is valid, false otherwise.
     */
    public boolean isCurrentTilePlacementValid() {
        int currentX = this.currentTile.getX();
        int currentY = this.currentTile.getY();
        
        // Out-of-bounds tiles or tiles that are already occupied are never valid.
        if (isOutOfBounds(currentX, currentY) ||
                getConfirmedTile(currentX, currentY) != null) {
            return false;
        }

        // Check for adjacent tiles and ensure they are all valid. Refer to the
        // documentation for Tile for the meaning of the magical part constants.
        return hasAdjacentTile(currentX, currentY) &&
                isAdjacentValid(0, -1, 0, 1, 0) &&
                isAdjacentValid(1,  0, 2, 3, 1) &&
                isAdjacentValid(0,  1, 4, 5, 2) &&
                isAdjacentValid(-1, 0, 6, 7, 3);
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

        // It's always valid if there's no meeple.
        Section meepleSection = this.currentTile.getMeepleSection();
        if (meepleSection == null) {
            return true;
        }

        // Otherwise, offload the work to the meeple analysis classes.
        return MeepleAnalysis.create(this, meepleSection).isMeepleValid();
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
     * Represents a single position and rotation of the current tile on the board.
     */
    public static class TilePlacement {
        /** The X position of this placement. */
        public int x;
        /** The Y position of this placement. */
        public int y;

        /** The tile rotation of this placement. */
        public int rotation;

        /**
         * The section on the tile to place the meeple at for this placement. Only
         * meaningful for getValidMeeplePlacements(); it is null for
         * getValidTilePlacements().
         */
        public Section meepleSection;

        /**
         * Constructor; just fills in the data values of the same name.
         *
         * @param x        The X position of this placement.
         * @param y        The Y position of this placement.
         * @param rotation The tile rotation of this placement.
         */
        public TilePlacement(int x, int y, int rotation) {
            this.x = x;
            this.y = y;
            this.rotation = rotation;
            this.meepleSection = null;
        }

        /**
         * Create a new tile placement that is a copy of another one.
         *
         * @param other The tile placement to make a copy of.
         */
        public TilePlacement(TilePlacement other) {
            this.x = other.x;
            this.y = other.y;
            this.rotation = other.rotation;
            this.meepleSection = other.meepleSection;
        }
    }

    /**
     * Searches through every position on the board and every rotation of the current
     * tile and creates an array of all valid positions and rotations that the current
     * tile can be placed in. It ignores meeples on the current tile; if there is
     * already a meeple on the current tile, it may return invalid results.
     *
     * @return An array of all valid placements of the current tile. Each tile will
     *         have a meepleSection of null. The array will never be empty.
     */
    public ArrayList<TilePlacement> getValidTilePlacements() {
        // Backup the original position of the current tile since it will be changed.
        // Rotation automatically goes through a full 360 degrees each inner loop,
        // so no need to back it up.
        int origX = this.currentTile.getX();
        int origY = this.currentTile.getY();

        ArrayList<TilePlacement> placements = new ArrayList<>();

        // Loop through every position and rotation and check the tile placement.
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                for (int rot = 0; rot < 4; rot++) {
                    // Set the current tile to these parameters.
                    this.currentTile.setPosition(x, y);
                    this.currentTile.rotate();

                    // If the placement is valid, add it to the array of valid placements.
                    if (isCurrentTilePlacementValid()) {
                        placements.add(new TilePlacement(x, y,
                                this.currentTile.getRotation()));
                    }
                }
            }
        }

        // Restore the current tile's position.
        this.currentTile.setPosition(origX, origY);

        return placements;
    }

    /**
     * Searches through every valid tile placement (gotten with getValidTilePlacements())
     * and creates an array of all the valid meeple placements on those tiles.
     *
     * @return An array of all valid meeple placements of the current tile. It may be
     *         empty if there are no valid meeple placements.
     */
    public ArrayList<TilePlacement> getValidMeeplePlacements() {
        // Backup all attributes about the current tile that will be changed in
        // the loop.
        int origX = this.currentTile.getX();
        int origY = this.currentTile.getY();
        int origRotation = this.currentTile.getRotation();
        Section origMeepleSection = this.currentTile.getMeepleSection();

        // Loop over all the valid tile placements and find all the sections that
        // can have meeples placed on them.
        ArrayList<TilePlacement> tilePlacements = getValidTilePlacements();
        ArrayList<TilePlacement> meeplePlacements = new ArrayList<>();

        for (int i = 0; i < tilePlacements.size(); i++) {
            TilePlacement tilePlacement = tilePlacements.get(i);

            // Set the current tile to the parameters for this placement.
            this.currentTile.setPosition(tilePlacement.x, tilePlacement.y);
            this.currentTile.setRotation(tilePlacement.rotation);

            // Loop over its sections and set the meeple to each one in turn.
            for (Section section : this.currentTile.getSections()) {
                this.currentTile.setMeepleSection(section);

                // If this meeple placement is valid, copy it, add the section, and
                // add it to the list of valid meeple placements.
                if (isCurrentMeeplePlacementValid()) {
                    TilePlacement copy = new TilePlacement(tilePlacement);
                    copy.meepleSection = section;

                    meeplePlacements.add(copy);
                }
            }
        }

        // Restore the current tile's attributes that were changed.
        this.currentTile.setPosition(origX, origY);
        this.currentTile.setRotation(origRotation);
        this.currentTile.setMeepleSection(origMeepleSection);

        return meeplePlacements;
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

        toStr.add("getWidth()", getWidth());
        toStr.add("getHeight()", getHeight());

        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                // Add the string of each non-null tile on the board.
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

        // Place the starting tile on the board. It's always valid, so we can set
        // it directly.
        setTileDirectly(1, 1, startingTile);

        this.currentTile = null;
    }

    /**
     * Creates a new board that is a deep copy of another board.
     *
     * @param other The board to make a deep copy of.
     */
    public Board(Board other) {
        this.tiles = Util.deepCopyNested(other.tiles, Tile::new);

        this.currentTile = Util.copyOrNull(other.currentTile, Tile::new);
    }

    /**
     * Checks if the borders of the current tile and the adjacent tile at the specified
     * offset match up. If there is no tile at that adjacent position, returns true
     * since blank tiles always "match". This is a helper method for
     * isCurrentTilePlacementValid().
     *
     * @param xOffset    The X offset from the current tile to the adjacent tile.
     * @param yOffset    The Y offset from the current tile to the adjacent tile.
     * @param firstPart  One tile part on the current tile to validate with the adjacent
     *                   tile's tile part on the opposite side.
     * @param secondPart The other tile part on the current tile to validate with the
     *                   adjacent tile's tile part on the opposite side.
     * @param roadPart   The road part on the current tile to validate with the adjacent
     *                   tile's road part on the opposite side.
     */
    private boolean isAdjacentValid(int xOffset, int yOffset, int firstPart, int secondPart,
                                    int roadPart) {
        Tile tile = getConfirmedTile(this.currentTile.getX() + xOffset,
                this.currentTile.getY() + yOffset);
        if (tile == null) {
            // There's no tile, so it's automatically valid in that direction.
            return true;
        }

        // Check whether the types of sections match up, i.e. farm to farm and
        // city to city, and then check for roads matching up.
        return this.currentTile.getSection(firstPart).getType() ==
                        tile.getSection(Tile.flipPart(firstPart)).getType() &&
                this.currentTile.getSection(secondPart).getType() ==
                        tile.getSection(Tile.flipPart(secondPart)).getType() &&
                this.currentTile.hasRoad(roadPart) ==
                        tile.hasRoad(Tile.flipRoadPart(roadPart));
    }
}
