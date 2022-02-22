package com.example.carcassonne;

/**
 * Represents the "board" of tiles, i.e. all the tiles in play, including the special
 * current tile that is not fully committed to the board, if there is one. Any positions
 * not containing a tile are null. The board grows in each direction as necessary when
 * tiles are added at the edge.
 *
 * @author Vincent Robinson
 */
public class Board {
    private Tile[][] tiles;

    private Tile currentTile;
    private int currentTileX;
    private int currentTileY;

    public int getWidth() {
        return this.tiles[0].length;
    }

    public int getHeight() {
        return this.tiles.length;
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
            return null;
        }
        return this.tiles[y][x];
    }

    public Tile getCurrentTile() {
        return this.currentTile;
    }

    public int getCurrentTileX() {
        return this.currentTileX;
    }

    public int getCurrentTileY() {
        return this.currentTileY;
    }

    public void setCurrentTile(Tile tile) {
        setCurrentTile(-1, -1, tile);
    }

    public void setCurrentTile(int x, int y, Tile tile) {
        this.currentTile = tile;
        this.currentTileX = x;
        this.currentTileY = y;
    }

    public void commitCurrentTile() {
        assert isCurrentTileValid();

        this.tiles[this.currentTileY][this.currentTileX] = this.currentTile;

        // If the tile is at the edge of the array, increase the array size by one so
        // we have space on the edges for future tiles.
        if (this.currentTileX == 0) {
            growSize(1, 0, 0, 0);
        }
        else if (this.currentTileX == getWidth() - 1) {
            growSize(0, 0, 1, 0);
        }

        if (this.currentTileY == 0) {
            growSize(0, 1, 0, 0);
        }
        else if (this.currentTileY == getHeight() - 1) {
            growSize(0, 0, 0, 1 );
        }

        this.currentTile = null;
        this.currentTileX = this.currentTileY = -1;
    }

    public boolean isCurrentTileValid() {
        // Out-of-bounds tiles are never valid.
        if (this.currentTileX < 0 || this.currentTileY < 0 ||
                this.currentTileX >= getWidth() || this.currentTileY >= getHeight()) {
            return false;
        }

        // If the current tile is occupied, this is invalid.
        if (getTile(this.currentTileX, this.currentTileY) != null) {
            return false;
        }

        // TODO: Detect when tiles don't match with adjacent tiles.
        return true;
    }

    public Board(Tile startingTile) {
        this.tiles = new Tile[3][3];

        setCurrentTile(1, 1, startingTile);
        commitCurrentTile();
    }

    public Board(Board other) {
        this.tiles = new Tile[other.getHeight()][other.getWidth()];

        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                this.tiles[y][x] = new Tile(other.tiles[y][x]);
            }
        }

        this.currentTile = new Tile(other.currentTile);
        this.currentTileX = other.currentTileX;
        this.currentTileY = other.currentTileY;
    }

    private void growSize(int srcX, int srcY, int destX, int destY) {
        int incX = Math.max(srcX, destX);
        int incY = Math.max(srcY, destY);

        Tile[][] dest = new Tile[getHeight() + incY][getWidth() + incX];

        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                dest[y + destY][x + destX] = this.tiles[y + srcY][x + srcX];
            }
        }

        this.tiles = dest;
    }
}
