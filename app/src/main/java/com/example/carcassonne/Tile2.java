package com.example.carcassonne;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Tile2 {
    public static final int SIZE = 292;

    private int[][] map;
    private HashMap<Integer, Section> sections;
    private boolean hasPennant;

    private int meepleSection;
    private int owner;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_FARM = 1;
    public static final int TYPE_CITY = 2;
    public static final int TYPE_ROAD = 3;
    public static final int TYPE_CLOISTER = 4;

    private static final int NO_MEEPLE = 0xFFFFFFFF;

    private static final int NO_CONN_COLOR = 0xFF000000;
    private static final int PENNANT_COLOR = 0xFFFF0000;

    /*
     * External Citation
     * Date: 17 March 2022
     * Problem: Wanted a way to create a HashSet from a list of constants.
     * Resource:
     *     https://docs.oracle.com/javase/7/docs/api/java/util/Arrays.html
     * Solution: Used Arrays.asList() and called the HashSet copy constructor on it.
     */
    public static final HashSet<Integer> FARM_COLORS = new HashSet<>(Arrays.asList(
            0xFF00FF00, 0xFF00BF00, 0xFF007F00, 0xFF003F00));
    public static final HashSet<Integer> CITY_COLORS = new HashSet<>(Arrays.asList(
            0xFFFF0000, 0xFFBF0000));
    public static final HashSet<Integer> ROAD_COLORS = new HashSet<>(Arrays.asList(
            0xFF0000FF, 0xFF0000BF, 0xFF00007F, 0xFF00003F));
    public static final HashSet<Integer> CLOISTER_COLORS = new HashSet<>(Arrays.asList(
            0xFFFF00FF));

    /**
     * Returns the part number on the opposite side of the tile. For instance, the
     * opposite for 2 is 7.
     *
     * @param part The part number to find the opposite of.
     * @return The opposite of the provided part number.
     */
    public static int flipPart(int part) {
        // Section numbers have an interesting property: subtracting five by the
        // vertical numbers and nine by the horizontal numbers give the part on
        // the opposite side.
        switch (part) {
            case 0:
            case 1:
            case 4:
            case 5:
                return 5 - part;
        }
        return 9 - part;
    }

    /**
     * Returns the road part number on the opposite side of the tile. For instance,
     * the opposite for 0 is 2.
     *
     * @param part The road part number to find the opposite of.
     * @return The opposite of the provided road part number.
     */
    public static int flipRoadPart(int part) {
        // Similarly to flipPart(), some subtraction can give the road part number on
        // the opposite side based on the axis.
        if (part == 1 || part == 3) {
            return 4 - part;
        }
        return 2 - part;
    }

    /**
     * Given a part number, this function returns an offset of either -1 or 1 for the
     * tile next to the specified part number in the horizontal direction. Parts on
     * the vertical side of the tile return 0.
     *
     * @param part The part number to find a horizontal offset for.
     * @return One of -1, 0, or 1 as the horizontal offset.
     */
    public static int partXOffset(int part) {
        switch (part) {
            case 2:
            case 3:
                return 1;
            case 6:
            case 7:
                return -1;
        }
        return 0;
    }

    /**
     * See partXOffset(), but for the vertical direction.
     *
     * @param part The part number to find a vertical offset for.
     * @return One of -1, 0, or 1 as the vertical offset.
     */
    public static int partYOffset(int part) {
        switch (part) {
            case 0:
            case 1:
                return -1;
            case 4:
            case 5:
                return 1;
        }
        return 0;
    }

    /**
     * See partXOffset(), but works on road parts.
     *
     * @param part The road part number to find a horizontal offset for.
     * @return One of -1, 0, or 1 as the horizontal offset.
     */
    public static int roadPartXOffset(int part) {
        if (part == 1) {
            return 1;
        }
        else if (part == 3) {
            return -1;
        }
        return 0;
    }

    /**
     * See roadPartXOffset(), but for the vertical direction.
     *
     * @param part The road part number to find a vertical offset for.
     * @return One of -1, 0, or 1 as the vertical offset.
     */
    public static int roadPartYOffset(int part) {
        if (part == 0) {
            return -1;
        }
        else if (part == 2) {
            return 1;
        }
        return 0;
    }

    public HashMap<Integer, Section> getSections() {
        return this.sections;
    }

    public Section getSectionFromPart(int part) {
        for (Section section : this.sections.values()) {
            if ((section.getType() == TYPE_CITY || section.getType() == TYPE_FARM)
                    && section.getParts().contains(part)) {
                return section;
            }
        }

        // There should always be a section for each part
        assert false;
        return null;
    }

    public Section getSectionFromRoadPart(int part) {
        for (Section section : this.sections.values()) {
            if (section.getType() == TYPE_ROAD && section.getParts().contains(part)) {
                return section;
            }
        }
        return null;
    }

    public void setMeeple(int x, int y) {
        this.meepleSection = this.map[y][x];
    }

    public void removeMeeple() {
        this.meepleSection = NO_MEEPLE;
    }

    public Section getMeepleSection() {
        return this.sections.get(this.meepleSection);
    }

    /**
     * Queries the index of the player that placed this tile, and therefore the owner
     * of any meeples on the tile as well. If there is no owner (i.e. it was freshly
     * drawn from the deck), it returns -1.
     *
     * @return The index of the player that placed this tile, or -1 if there is no
     *         owner yet.
     */
    public int getOwner() {
        return this.owner;
    }

    /**
     * Sets the player index of the player that placed this tile and therefore the
     * owner of any meeples on the tile as well.
     *
     * @param owner The index of the player that placed this tile.
     */
    public void setOwner(int owner) {
        this.owner = owner;
    }

    public void rotateBy(int by) {
        for (int i = 0; i < by; i++) {
            rotate();
        }
    }

    public void rotate() {
        int[][] rotatedMap = new int[SIZE][SIZE];
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                rotatedMap[x][SIZE - y] = this.map[y][x];
            }
        }
        this.map = rotatedMap;

        for (Section section : this.sections.values()) {
            section.rotate();
        }
    }

    /**
     * Queries whether the city in this tile has a pennant. Tiles with zero or two
     * cities will always return false.
     *
     * @return True if there is a pennant, false otherwise.
     */
    public boolean hasPennant() {
        return this.hasPennant;
    }

    @Override
    public String toString() {
        ToStringer toStr = new ToStringer("Tile");

        toStr.add("map", this.map);
        toStr.add("sections", this.sections);
        toStr.add("hasPennant", this.hasPennant);
        toStr.add("meepleSection", this.meepleSection);
        toStr.add("owner", this.owner);

        return toStr.toString();
    }

    private static class PartPos {
        public boolean isRoad;
        public int x;
        public int y;
        public int part;

        public PartPos(boolean isRoad, int part, int x, int y) {
            this.isRoad = isRoad;
            this.x = x;
            this.y = y;
            this.part = part;
        }
    }

    private static final PartPos[] PART_POSITIONS = {
            // Farm/city parts
            new PartPos(false, 0, SIZE / 4,     0),
            new PartPos(false, 1, SIZE * 3 / 4, 0),
            new PartPos(false, 2, SIZE - 1,     SIZE / 4),
            new PartPos(false, 3, SIZE - 1,     SIZE * 3 / 4),
            new PartPos(false, 4, SIZE / 4,     SIZE - 1),
            new PartPos(false, 5, SIZE * 3 / 4, SIZE - 1),
            new PartPos(false, 6, 0,             SIZE / 4),
            new PartPos(false, 7, 0,             SIZE * 3 / 4),

            // Road parts
            new PartPos(true, 0, SIZE / 2, 0),
            new PartPos(true, 1, SIZE - 1, SIZE / 2),
            new PartPos(true, 2, SIZE / 2, SIZE - 1),
            new PartPos(true, 3, 0,         SIZE / 2),
    };

    public Tile2(int[][] map, int[][] connImage) {
        this.map = Util.deepCopyArray(map, Util::copyArray);
        this.sections = new HashMap<>();
        this.hasPennant = false;

        this.meepleSection = NO_MEEPLE;
        this.owner = -1;

        for (int y = 1; y < SIZE - 1; y++) {
            for (int x = 1; x < SIZE - 1; x++) {
                int color = connImage[y][x];
                if (color != NO_MEEPLE) {
                    this.sections.put(color, new Section(color, x, y));
                }
            }
        }

        for (PartPos partPos : PART_POSITIONS) {
            int color = connImage[partPos.y][partPos.x];
            if (color != NO_CONN_COLOR) {
                // Ensure we have the proper colors for the proper section type.
                if (partPos.isRoad) {
                    assert ROAD_COLORS.contains(color);
                } else {
                    assert FARM_COLORS.contains(color) || CITY_COLORS.contains(color);
                }

                Section section = this.sections.get(color);

                // There must be a section at this point; otherwise, the connImage
                // is incorrect.
                assert section != null;

                section.addPart(partPos.part);
            } else {
                // Only roads may not have a connection; everything else must have one.
                assert partPos.isRoad;
            }
        }

        int specialColor = connImage[0][0];
        if (specialColor == PENNANT_COLOR) {
            this.hasPennant = true;
        } else {
            // It must be black if there is no special.
            assert specialColor == NO_CONN_COLOR;
        }
    }

    public Tile2(Tile2 other) {
        this.map = Util.deepCopyArray(other.map, Util::copyArray);

        this.sections = Util.deepCopyMap(other.sections, HashMap::new, Section::new);

        this.meepleSection = other.meepleSection;
        this.hasPennant = other.hasPennant;
    }
}
