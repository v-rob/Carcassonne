package com.example.carcassonne;

import java.util.HashSet;

/**
 * Represents a single Carcassonne tile. Carcassonne's main complexity lies in
 * how things connect: for instance, farmland connects to farmland on different
 * tiles, but sometimes doesn't connect to farmland on the same tile due to a city
 * or road in between.
 *
 * The way Tile objects represent this is via "sections". All farmland that is
 * connected on one side of a road or city is put in its own section, and
 * likewise for cities.
 *
 * Sections are grouped together by the smallest unit of measure, a "part" of
 * the tile. Observe the following diagram and the eighths numbered 0-7: these
 * are the parts of every tile.
 *
 *               0
 *       +---------------+
 *       |\   0  |  1   /|
 *       |  \    |    /  |
 *       |  7 \  |  / 2  |
 *       |      \|/      |
 *     3 |-------+-------| 1
 *       |      /|\      |
 *       |  6 /  |  \ 3  |
 *       |  /    |    \  |
 *       |/   5  |  4   \|
 *       +---------------+
 *               2
 *
 * Any tile can be split into these eight parts, and these parts can then be
 * placed into sections. Finally, note the outer numbers 0-3 in the diagram:
 * these indicate whether there is a road in that direction.
 *
 * An example is likely the best way to explain this. Consider tile D:
 *
 *               0
 *       +---------------+
 *       |    0  |  1   /|
 *       |       |    /. |
 *       |  7    |  / 2 .|
 *       |       |  |. . |
 *     3 |       |  | . .| 1
 *       |       |  |. . |
 *       |  6    |  \ 3 .|
 *       |       |    \. |
 *       |    5  |  4   \|
 *       +---------------+
 *               2
 *
 * There is a city on the right and a vertical road through the middle. Note
 * that the part and road numbers have been left in the diagram. There are three
 * sections:
 *
 * - 2, 3:       These parts comprise the only city section.
 * - 1, 4:       This is one of the sections of farmland. It does not connect
 *               to the rest of the farmland because there is a road in between.
 * - 5, 6, 7, 8: This is the other section of farmland.
 *
 * Secondly, this tile will include the road numbers 0 and 2 because roads exit
 * in those two directions.
 *
 * Sections will vary from tile to tile: some may have as many as four farmland
 * sections, and others may have zero city or farmland sections. However, every
 * part must always be part of some section.
 *
 * Sections are stored as nested arrays of integers: the outermost array is the
 * array of sections, while the inner array is the array of part numbers. For
 * the previous example, this is the array structure for the farmland:
 *
 *     int[][] farmSections = {
 *         {1, 4},
 *         {5, 6, 7, 8}
 *     };
 *
 * And the city section:
 *
 *     int[][] citySections = {
 *         {2, 3}
 *     };
 *
 * Roads are simply stored as an array of road parts. So, for tile D, this is:
 *
 *     int[] roads = {0, 2};
 *
 * A final note on sections: rotating a tile causes all the part and road numbers
 * to be rotated with it. In other words, a right rotation would cause tile D's
 * city to become 4 and 5 instead of the normal 2 and 3.
 *
 * Meeple placement on tiles is represented as two integers: the type of the meeple
 * (which is one of the `MEEPLE_<type>` constants) and the section number, if placed
 * on something that is divided into sections. For instance, a meeple placed on the
 * second farm section has a `meepleType` of `MEEPLE_FARMER` and a `meepleSection`
 * of 1. Finally, there is a field for determining which player placed the tile (and
 * therefore the color of the meeple).
 *
 * Tiles have two additional boolean fields: hasPennant states whether the city
 * in the tile has a pennant. Tiles with two cities never have pennants, so this
 * this does not have to be accounted for. Secondly, hasCloister states whether
 * the tile is a cloister or not.
 *
 * @author Vincent Robinson
 */
public class Tile {
    /**
     * The ID of the tile, which is a letter between 'a' and 'x' that corresponds to
     * the "tile_<id>.png" file of this tile.
     */
    private char id;

    /**
     * The parts of the tile that comprise each section of each farm in this tile. See
     * the main comment for Tile for more information.
     */
    private HashSet<Integer>[] farmSections;
    /**
     * The parts of the tile that comprise each section of each cities in this tile. See
     * the main comment for Tile for more information.
     */
    private HashSet<Integer>[] citySections;

    /**
     * The road parts that indicate which edge of the tile has a road in that direction.
     * See the main comment for Tile for more information.
     */
    private HashSet<Integer> roads;

    /**
     * Indicates whether the city on this tile has a pennant or not. Always false for
     * tiles with zero or two cities.
     */
    private boolean hasPennant;
    /**
     * Indicates whether this tile has a cloister on it. No tiles have both cities and
     * cloisters.
     */
    private boolean hasCloister;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_FARM = 1;
    public static final int TYPE_CITY = 2;
    public static final int TYPE_ROAD = 3;
    public static final int TYPE_CLOISTER = 4;

    private int meepleType;

    private int meepleSection;

    private int owner;

    /**
     * Returns the part number on the opposite side of the tile. For instance, the
     * opposite for 2 is 7.
     *
     * @param part The part number to find the opposite of.
     * @return The opposite of the provided part number.
     */
    public static int flipPart(int part) {
        // This is an interesting property that subtracting five by the vertical
        // numbers and nine by the horizontal numbers give the opposite part.
        switch (part) {
            case 0:
            case 1:
            case 4:
            case 5:
                return 5 - part;
        }
        return 9 - part;
    }

    public static int flipRoadPart(int part) {
        if (part == 1 || part == 3) {
            return 4 - part;
        }
        return 2 - part;
    }

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

    public static int roadPartXOffset(int part) {
        if (part == 1) {
            return 1;
        }
        else if (part == 3) {
            return -1;
        }
        return 0;
    }

    public static int roadPartYOffset(int part) {
        if (part == 0) {
            return -1;
        }
        else if (part == 2) {
            return 1;
        }
        return 0;
    }

    public int getSectionType(int part) {
        if (getGenericSectionFromPart(this.citySections, part) != null) {
            return TYPE_CITY;
        }
        return TYPE_FARM;
    }

    public boolean hasRoad(int part) {
        return this.roads.contains(part);
    }

    public HashSet<Integer> getSectionFromPart(int part) {
        HashSet<Integer> section = getGenericSectionFromPart(this.citySections, part);
        if (section != null) {
            return section;
        }
        return getGenericSectionFromPart(this.farmSections, part);
    }

    public HashSet<Integer> getRoads() {
        return this.roads;
    }

    public boolean hasPennant() {
        return this.hasPennant;
    }
    
    public boolean hasCloister() {
        return this.hasCloister;
    }

    public int getMeepleType() {
        return this.meepleType;
    }

    public HashSet<Integer> getMeepleSection() {
        if (this.meepleType == TYPE_CITY) {
            return this.citySections[this.meepleSection];
        }
        else if (this.meepleType == TYPE_FARM) {
            return this.farmSections[this.meepleSection];
        }
        return null;
    }

    public int getOwner() {
        return this.owner;
    }

    /**
     * Rotates the tile 90 degrees clockwise. All sections and roads are rotated
     * with it.
     */
    public void rotate() {
        rotateSections(this.farmSections);
        rotateSections(this.citySections);

        this.roads = rotateSet(this.roads, 1, 4);
    }

    /**
     * Converts the tile to a string representation showing all instance variables.
     *
     * @return The string representation of the tile.
     */
    @Override
    public String toString() {
        return "Tile {\n" +
                "    id = " + this.id + "\n" +
                "    farmSections = " + sectionsToString(this.farmSections) + "\n" +
                "    citySections = " + sectionsToString(this.citySections) + "\n" +
                "    roads = " + setToString(this.roads) + "\n" +
                "    hasPennant = " + this.hasPennant + "\n" +
                "    hasCloister = " + this.hasCloister + "\n}";
    }

    /**
     * Creates a new Tile. Generally, only Deck needs to create Tiles, as everything
     * else will get its tiles from Deck. Deep copies are made of all array parameters.
     *
     * @param id           The character id of the tile.
     * @param farmSections The parts of the tile that comprise each farm section.
     * @param citySections The parts of the tile that comprise each city section.
     * @param roads        The road parts for each road exiting the tile.
     * @param hasPennant   Whether or not the tile has a pennant for the city.
     * @param hasCloister   Whether or not the tile has a cloister.
     */
    public Tile(char id, int[][] farmSections, int[][] citySections,
                int[] roads, boolean hasPennant, boolean hasCloister) {
        this.id = id;

        this.farmSections = sectionsFromIntArray(farmSections);
        this.citySections = sectionsFromIntArray(citySections);

        this.roads = setFromIntArray(roads);

        // Give the tile a random rotation since tiles will be in no particular
        // rotation when drawing a tile in real Carcassonne.
        int by = (int)(Math.random() * 4);
        for (int i = 0; i < by; i++) {
            rotate();
        }

        this.hasPennant = hasPennant;
        this.hasCloister = hasCloister;
    }

    /**
     * Performs a deep copy of the tile and all its instance variables.
     *
     * @param other The tile to make a copy of.
     */
    public Tile(Tile other) {
        this.id = other.id;

        this.farmSections = copySections(other.farmSections);
        this.citySections = copySections(other.citySections);

        this.roads = new HashSet<>(other.roads);

        this.hasPennant = other.hasPennant;
        this.hasCloister = other.hasCloister;
    }

    private static HashSet<Integer> getGenericSectionFromPart(
            HashSet<Integer>[] sections, int part) {
        for (int i = 0; i < sections.length; i++) {
            HashSet<Integer> section = sections[i];
            if (sections[i].contains(part)) {
                return section;
            }
        }
        return null;
    }

    private static void rotateSections(HashSet<Integer>[] sections) {
        for (int i = 0; i < sections.length; i++) {
            sections[i] = rotateSet(sections[i], 2, 8);
        }
    }

    private static HashSet<Integer> rotateSet(HashSet<Integer> set, int add, int mod) {
        HashSet<Integer> rotated = new HashSet<>();
        for (int part : set) {
            rotated.add((part + add) % mod);
        }
        return rotated;
    }

    private static HashSet<Integer> setFromIntArray(int[] array) {
        HashSet<Integer> set = new HashSet<>();
        for (int i = 0; i < array.length; i++) {
            set.add(array[i]);
        }
        return set;
    }

    private static HashSet<Integer>[] sectionsFromIntArray(int[][] array) {
        HashSet<Integer>[] sections = new HashSet[array.length];
        for (int i = 0; i < array.length; i++) {
            sections[i] = setFromIntArray(array[i]);
        }
        return sections;
    }

    private static HashSet<Integer>[] copySections(HashSet<Integer>[] sections) {
        HashSet<Integer>[] copy = new HashSet[sections.length];
        for (int i = 0; i < sections.length; i++) {
            copy[i] = new HashSet<>(sections[i]);
        }
        return copy;
    }

    public static String setToString(HashSet<Integer> array) {
        String str = "{";

        for (int part : array) {
            str += part + " ";
        }

        return str.substring(0, str.length() - 1) + "}";
    }

    public static String sectionsToString(HashSet<Integer>[] array) {
        String str = "{\n";

        for (int i = 0; i < array.length; i++) {
            str += "    " + setToString(array[i]) + "\n";
        }

        return str + "}";
    }
};
