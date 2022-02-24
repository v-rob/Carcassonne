package com.example.carcassonne;

import java.util.HashSet;

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

    public void confirmCurrentTile() {
        assert isCurrentTilePlacementValid() && isCurrentMeeplePlacementValid();

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

    public boolean isCurrentTilePlacementValid() {
        // Out-of-bounds tiles are never valid.
        if (this.currentTileX < 0 || this.currentTileY < 0 ||
                this.currentTileX >= getWidth() || this.currentTileY >= getHeight()) {
            return false;
        }

        // If the current tile is occupied, this is invalid.
        if (getTile(this.currentTileX, this.currentTileY) != null) {
            return false;
        }

        boolean foundAdjacent = false;
        boolean adjacentIsValid = true;

        // TODO: Split into helper methods
        // Left
        Tile adjacent = getTile(this.currentTileX - 1, this.currentTileY);
        if (adjacent != null) {
            foundAdjacent = true;
            adjacentIsValid &= isAdjacentValid(adjacent, 6, 7, 3);
        }

        // Right
        adjacent = getTile(this.currentTileX + 1, this.currentTileY);
        if (adjacent != null) {
            foundAdjacent = true;
            adjacentIsValid &= isAdjacentValid(adjacent, 2, 3, 1);
        }

        // Above
        adjacent = getTile(this.currentTileX, this.currentTileY - 1);
        if (adjacent != null) {
            foundAdjacent = true;
            adjacentIsValid &= isAdjacentValid(adjacent, 0, 1, 0);
        }

        // Below
        adjacent = getTile(this.currentTileX, this.currentTileY + 1);
        if (adjacent != null) {
            foundAdjacent = true;
            adjacentIsValid &= isAdjacentValid(adjacent, 4, 5, 2);
        }

        return foundAdjacent && adjacentIsValid;
    }

    public boolean isCurrentMeeplePlacementValid() {
        assert isCurrentTilePlacementValid();

        int type = this.currentTile.getMeepleType();

        // Set of all tiles (not including the current tile) that have already
        // been visited by hasRoadMeeple() or hasSectionMeeple(). It is unused for
        // cloisters.
        HashSet<Tile> visited = new HashSet<>();

        if (type == Tile.TYPE_CLOISTER && this.currentTile.hasCloister()) {
            return true;
        } else if (type == Tile.TYPE_ROAD) {
            return checkAdjacentHasRoadMeeples(this.currentTileX, this.currentTileY,
                    this.currentTile, visited);
        }
        // return checkAdjacentHasSectionMeeples(this.currentTileX, this.currentTileY,
        //         this.currentTile, visited);
        return false;
    }

    @Override
    public String toString() {
        String str = "Board {\n" +
                "    currentTile = " + this.currentTile + "\n" +
                "    currentTileX = " + this.currentTileX + "\n" +
                "    currentTileY = " + this.currentTileY + "\n" +
                "    getWidth() = " + getWidth() + "\n" +
                "    getHeight() = " + getHeight() + "\n";

        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                Tile tile = getTile(x, y);
                if (tile != null) {
                    str += "    tiles[" + y + "][" + x + "] = " +
                            Util.indent(4, tile.toString()) + "\n";
                }
            }
        }

        str += "}";
        return str;
    }

    public Board(Tile startingTile) {
        this.tiles = new Tile[3][3];

        setCurrentTile(1, 1, startingTile);
        confirmCurrentTile();
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

    private boolean isAdjacentValid(Tile adjacent, int firstPart, int secondPart,
                                    int roadPart) {
        return (this.currentTile.getSectionType(firstPart) ==
                adjacent.getSectionType(Tile.flipPart(firstPart))) &&
                (this.currentTile.getSectionType(secondPart) ==
                        adjacent.getSectionType(Tile.flipPart(secondPart))) &&
                (this.currentTile.hasRoad(roadPart) ==
                        adjacent.hasRoad(Tile.flipRoadPart(roadPart)));
    }

    private boolean checkAdjacentHasRoadMeeples(int x, int y, Tile tile,
            HashSet<Tile> visited) {
        if (tile == null) {
            tile = getTile(x, y);
        }

        boolean foundMeeple = false;

        for (int road : tile.getRoads()) {
            switch (road) {
                case 0:
                    foundMeeple |= hasRoadMeeple(x, y - 1, visited);
                    break;
                case 1:
                    foundMeeple |= hasRoadMeeple(x + 1, y, visited);
                    break;
                case 2:
                    foundMeeple |= hasRoadMeeple(x, y + 1, visited);
                    break;
                case 3:
                    foundMeeple |= hasRoadMeeple(x - 1, y, visited);
                    break;
            }
        }

        return foundMeeple;
    }

    private boolean hasRoadMeeple(int x, int y, HashSet<Tile> visited) {
        Tile tile = getTile(x, y);
        if (tile == null || visited.contains(tile)) {
            return false;
        }

        visited.add(tile);
        return tile.getMeepleType() == Tile.TYPE_ROAD ||
                checkAdjacentHasRoadMeeples(x, y, null, visited);
    }

    private boolean checkAdjacentHasSectionMeeples(int x, int y, int[] parts,
            HashSet<Tile> visited) {
        return false;
    }

    private boolean hasSectionMeeple(int x, int y, int[] parts, HashSet<Tile> visited) {
        return false;
    }
}
