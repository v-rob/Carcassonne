package com.example.carcassonne;

public class Board {
    private Tile[][] tiles;

    private int currentTileX;
    private int currentTileY;

    public Board(Tile startingTile) {
        this.tiles = new Tile[3][3];

        setTile(1, 1, startingTile);
        commitTile();
    }

    public int getWidth() {
        return this.tiles[0].length;
    }

    public int getHeight() {
        return this.tiles.length;
    }

    public Tile getTile(int x, int y) {
        return this.tiles[y][x];
    }

    public void setTile(int x, int y, Tile tile) {
        this.tiles[y][x] = tile;
        this.currentTileX = x;
        this.currentTileY = y;
    }

    private void growSize(int srcX, int srcY, int destX, int destY) {
        int incX = Math.max(srcX, destX);
        int incY = Math.max(srcY, destY);

        Tile[][] dest = new Tile[getHeight() + incY][getWidth() + incX];

        for (int y = 0; y < this.tiles.length; y++) {
            for (int x = 0; x < this.tiles[y].length; x++) {
                dest[y + destY][x + destX] = this.tiles[y + srcY][x + srcX];
            }
        }

        this.tiles = dest;
    }

    public void commitTile() {
        // If the tile is at the edge of the array, increase the array size by one so
        // we have space on the edges for future tiles.
        if (this.currentTileX == 0) {
            growSize(1, 0, 0, 0);
        } else if (this.currentTileX == getWidth() - 1) {
            growSize(0, 0, 1, 0);
        }

        if (this.currentTileY == 0) {
            growSize(0, 1, 0, 0);
        } else if (this.currentTileY == getHeight() - 1) {
            growSize(0, 0, 0, 1 );
        }

        this.currentTileX = -1;
        this.currentTileY = -1;
    }

    public boolean isTileValid() {
        // TODO
        return false;
    }
}
