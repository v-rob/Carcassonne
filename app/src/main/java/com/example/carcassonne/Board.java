package com.example.carcassonne;

import java.util.HashMap;
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

    public Tile getAnyTile(int x, int y) {
        if (x == this.currentTileX && y == this.currentTileY) {
            return this.currentTile;
        }
        return getTile(x, y);
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

        // Check all possible surrounding positions for validity in clockwise order
        // starting from the top
        AdjacentValidation adjacent = new AdjacentValidation();

        isAdjacentValid(adjacent, 0, -1, 0, 1, 0);
        isAdjacentValid(adjacent, 1,  0, 2, 3, 1);
        isAdjacentValid(adjacent, 0,  1, 4, 5, 2);
        isAdjacentValid(adjacent, -1, 0, 6, 7, 3);

        // We can only place the tile if there is another tile next to this one and
        // if all farms, cities, and roads match up.
        return adjacent.found && adjacent.isValid;
    }

    public boolean isCurrentMeeplePlacementValid() {
        assert isCurrentTilePlacementValid();

        int type = this.currentTile.getMeepleType();

        HashSet<Tile> visited = new HashSet<>();
        HashMap<Integer, Integer> count = new HashMap<>();

        if (type == Tile.TYPE_NONE) {
            return true;
        }
        else if (type == Tile.TYPE_CLOISTER && this.currentTile.hasCloister()) {
            return true;
        }
        else if (type == Tile.TYPE_ROAD) {
            return countRoadMeeples(count, this.currentTileX, this.currentTileY, visited) == 1;
        }

        int total = 0;
        for (int part : this.currentTile.getMeepleSection()) {
            total += countSectionMeeples(count, type, this.currentTileX, this.currentTileY,
                    part, visited);
        }
        return total == 1;
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
                            Util.indent(tile.toString()) + "\n";
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

    private class AdjacentValidation {
        public boolean found = false;
        public boolean isValid = true;
    }

    private void isAdjacentValid(AdjacentValidation adjacent, int xOffset, int yOffset,
                                 int firstPart, int secondPart, int roadPart) {
        Tile tile = getTile(this.currentTileX + xOffset, this.currentTileY + yOffset);
        if (tile == null) {
            return;
        }

        adjacent.found = true;

        adjacent.isValid &= this.currentTile.getSectionType(firstPart) ==
                tile.getSectionType(Tile.flipPart(firstPart));
        adjacent.isValid &= this.currentTile.getSectionType(secondPart) ==
                tile.getSectionType(Tile.flipPart(secondPart));
        adjacent.isValid &= this.currentTile.hasRoad(roadPart) ==
                tile.hasRoad(Tile.flipRoadPart(roadPart));
    }

    private static int getOrDefault(HashMap<Integer, Integer> map, int key, int def) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return def;
    }

    private int countRoadMeeples(HashMap<Integer, Integer> count, int x, int y,
                                  HashSet<Tile> visited) {
        Tile tile = getAnyTile(x, y);

        // Don't count non-existent tile or tiles we've already searched through so
        // that we don't run into infinite recursion.
        if (tile == null || visited.contains(tile)) {
            return 0;
        }
        visited.add(tile);

        int total = 0;

        // Add the meeple from the current tile if it's a road meeple
        if (tile.getMeepleType() == Tile.TYPE_ROAD) {
            total++;

            int owner = tile.getOwner();
            count.put(owner, getOrDefault(count, owner, 0) + 1);
        }

        // Run this same function on all possible adjacent tiles
        for (int road_it : tile.getRoads()) {
            total += countRoadMeeples(count, x + Tile.roadPartXOffset(road_it),
                    y + Tile.roadPartYOffset(road_it), visited);
        }

        return total;
    }

    private int countSectionMeeples(HashMap<Integer, Integer> count, int type, int x,
                                    int y, int part, HashSet<Tile> visited) {
        // TODO: Take into account different sections on the same tile being connected
        // via other tiles.
        Tile tile = getAnyTile(x, y);

        if (tile == null || visited.contains(tile)) {
            return 0;
        }
        visited.add(tile);

        int total = 0;

        if (tile.getMeepleType() == type) {
            total++;

            int owner = tile.getOwner();
            count.put(owner, getOrDefault(count, owner, 0) + 1);
        }

        HashSet<Integer> section = tile.getSectionFromPart(part);
        for (int part_it : section) {
            total += countSectionMeeples(count, type, x + Tile.partXOffset(part_it),
                    y + Tile.partYOffset(part_it), Tile.flipPart(part_it), visited);
        }

        return total;
    }
}
