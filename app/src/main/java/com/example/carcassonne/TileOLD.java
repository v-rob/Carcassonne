package com.example.carcassonne;

import java.util.HashSet;

// OLD: Only exists so the unfinished Analysis classes still build. Will be removed later.
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
 * (which is one of the `TYPE_` constants) and the section number, if placed on
 * something that is divided into sections. For instance, a meeple placed on the
 * second farm section has a `meepleType` of `MEEPLE_FARMER` and a `meepleSection`
 * of 1. Finally, there is a field for determining which player placed the tile
 * (and therefore the color of the meeple).
 *
 * Tiles have two additional boolean fields: hasPennant states whether the city
 * in the tile has a pennant. Tiles with two cities never have pennants, so this
 * this does not have to be accounted for. Secondly, hasCloister states whether
 * the tile is a cloister or not.
 *
 * Tiles are used in HashSets; however, equals() and hashCode() are deliberately
 * not implemented. This is because identical tiles are distinct: there can be two
 * tile D's with the same rotation and meeples on the same map, but they are
 * distinct for every purpose. Care must therefore be taken to not mix Tiles and
 * their deep copies.
 *
 * All externally-facing functions that return sets make a copy of the set so
 * callers can't inadvertently make changed to the original.
 *
 * @author Vincent Robinson
 */
public class TileOLD {
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
     * The parts of the tile that comprise each section of each city in this tile. See
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

    // Constants used for the type of meeple on the board as well as getting the
    // section type of a tile part:
    /**
     * Indicates that there is no meeple on the board.
     */
    public static final int TYPE_NONE = 0;
    /**
     * Used for two things:
     * - `meepleType`: Indicates that the meeple is a farmer and placed on one of
     *   the farm sections.
     * - getSectionType(): Indicates that the section is a farm section.
     */
    public static final int TYPE_FARM = 1;
    /**
     * Used for two things:
     * - `meepleType`: Indicates that the meeple is a knight and placed on one of
     *   the city sections.
     * - getSectionType(): Indicates that the section is a city section.
     */
    public static final int TYPE_CITY = 2;
    /**
     * Indicates that the meeple is a thief and placed on a road on this tile.
     */
    public static final int TYPE_ROAD = 3;
    /**
     * Indicates that the meeple is a monk and placed on the monastery on this tile.
     */
    public static final int TYPE_CLOISTER = 4;

    /**
     * The type of the meeple on the tile. One of the `TYPE_` constants. If `TYPE_NONE`,
     * then there is no meeple anywhere on the tile.
     */
    private int meepleType;

    /**
     * If `meepleType` is one of `TYPE_FARM` or `TYPE_CITY`, this is the section index
     * that the meeple is in. The value is -1 otherwise.
     */
    private int meepleSection;

    /**
     * The player index of the person who placed the tile. If there is no owner, contains
     * -1. This is important for determining the owner of the meeple on the tile.
     */
    private int owner;

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

    /**
     * Gets the section type for the specified part, which is either `TYPE_FARM` or
     * `TYPE_CITY`.
     *
     * @param part The part to get the section type of.
     * @return Either `TYPE_FARM` or `TYPE_CITY`, depending on the part and tile.
     */
    public int getSectionType(int part) {
        if (getGenericSectionFromPart(this.citySections, part) != null) {
            return TYPE_CITY;
        }
        return TYPE_FARM;
    }

    /**
     * Returns whether there is a road leading down the specified road part.
     *
     * @param part The road part to query the existence of a road for.
     * @return True if there is a road in that direction, false otherwise.
     */
    public boolean hasRoad(int part) {
        return this.roads.contains(part);
    }

    /**
     * Returns the set of parts in a section given a single part number in that
     * section. It works for both city and farm sections.
     *
     * @param part The part number to get the section for.
     * @return The entire set of parts in the part's section.
     */
    public HashSet<Integer> getSectionFromPart(int part) {
        HashSet<Integer> section = getGenericSectionFromPart(this.farmSections, part);
        if (section == null) {
            // We didn't find a farm, so find the city section that must then exist.
            section = getGenericSectionFromPart(this.citySections, part);
        }
        return new HashSet<>(section);
    }

    /**
     * Gets the set of all road parts that correspond to actual roads in this tile.
     *
     * @return The set of all roads.
     */
    public HashSet<Integer> getRoads() {
        return new HashSet<>(this.roads);
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

    /**
     * Queries whether this tile contains a cloister. Tiles will never have both
     * cloisters and cities.
     *
     * @return True if there is a cloister, false otherwise.
     */
    public boolean hasCloister() {
        return this.hasCloister;
    }

    /**
     * Returns the type of the meeple on this tile, which is one of the `TILE_`
     * constants. If `TILE_NONE`, there is no meeple on the tile.
     *
     * @return The type of the meeple on the tile.
     */
    public int getMeepleType() {
        return this.meepleType;
    }

    /**
     * Gets the set of parts in the section that the meeple is in. If there is no
     * meeple or the meeple is not in a city or farm, returns null.
     *
     * @return The set of parts in the meeple's section, or null if not applicable.
     */
    public HashSet<Integer> getMeepleSection() {
        if (this.meepleType == TYPE_CITY) {
            return new HashSet<>(this.citySections[this.meepleSection]);
        }
        else if (this.meepleType == TYPE_FARM) {
            return new HashSet<>(this.farmSections[this.meepleSection]);
        }
        return null;
    }

    /**
     * Removes the meeple from this tile, if there is one.
     */
    public void removeMeeple() {
        this.meepleType = TYPE_NONE;
        this.meepleSection = -1;
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
     * else will get its tiles from Deck. Arrays are used in the constructor while
     * sets are used Tile itself for ease of construction.
     *
     * By default, each tile will have a random rotation, no meeple, and a player
     * index of -1.
     *
     * @param id           The character id of the tile.
     * @param farmSections The parts of the tile that comprise each farm section.
     * @param citySections The parts of the tile that comprise each city section.
     * @param roads        The road parts for each road exiting the tile.
     * @param hasPennant   Whether or not the tile has a pennant for the city.
     * @param hasCloister  Whether or not the tile has a cloister.
     */
    public TileOLD(char id, int[][] farmSections, int[][] citySections,
                   int[] roads, boolean hasPennant, boolean hasCloister) {
        this.id = id;

        // Copy all arrays into sets.
        this.farmSections = sectionsFromIntArray(farmSections);
        this.citySections = sectionsFromIntArray(citySections);

        this.roads = setFromIntArray(roads);

        // Give the tile a random rotation since tiles will be in no particular
        // rotation when drawing a tile from the deck in real Carcassonne.
        int by = (int)(Math.random() * 4);
        for (int i = 0; i < by; i++) {
            rotate();
        }

        this.hasPennant = hasPennant;
        this.hasCloister = hasCloister;

        // Tiles start out with no meeple.
        this.meepleType = TYPE_NONE;
        this.meepleSection = -1;
        this.owner = -1;
    }

    /**
     * Creates a new tile that is a deep copy of the tile and all its instance
     * variables.
     *
     * @param other The tile to make a copy of.
     */
    public TileOLD(TileOLD other) {
        this.id = other.id;

        this.farmSections = copySections(other.farmSections);
        this.citySections = copySections(other.citySections);

        this.roads = new HashSet<>(other.roads);

        this.hasPennant = other.hasPennant;
        this.hasCloister = other.hasCloister;
    }

    /**
     * Searches through an array of sections and returns the section that contains
     * the specified part number, or null if no such part number exists in any of
     * the sections.
     *
     * @param sections The array of sections to search through.
     * @param part     The part number found in one of the sections.
     * @return The set of part in the section if found, or null otherwise.
     */
    private static HashSet<Integer> getGenericSectionFromPart(
            HashSet<Integer>[] sections, int part) {
        for (int i = 0; i < sections.length; i++) {
            if (sections[i].contains(part)) {
                return sections[i];
            }
        }
        return null;
    }

    /**
     * Rotates an array of sections 90 degrees clockwise in place.
     *
     * @param sections The sections to rotate.
     */
    private static void rotateSections(HashSet<Integer>[] sections) {
        for (int i = 0; i < sections.length; i++) {
            sections[i] = rotateSet(sections[i], 2, 8);
        }
    }

    /**
     * Rotates a set of parts (either road or normal parts) 90 degrees clockwise by
     * increasing each number by a fixed amount and then moduloing by the total number
     * of parts to ensure all parts wrap around.
     *
     * @param set The set of parts to rotate.
     * @param add The amount to increase each number by. For normal parts, this should
     *            be two because there are two parts per side. For road parts, this
     *            should be one.
     * @param mod The total number of parts.
     * @return The rotated set.
     */
    private static HashSet<Integer> rotateSet(HashSet<Integer> set, int add, int mod) {
        // We have to create a new set and return it because we can't change set
        // elements in place: they aren't positional.
        HashSet<Integer> rotated = new HashSet<>();
        for (int part : set) {
            rotated.add((part + add) % mod);
        }
        return rotated;
    }

    /**
     * Creates a HashSet of integers out of an array of integers.
     *
     * @param array The array of integers to create the set from.
     * @return The set created from the array.
     */
    private static HashSet<Integer> setFromIntArray(int[] array) {
        HashSet<Integer> set = new HashSet<>();
        for (int i = 0; i < array.length; i++) {
            set.add(array[i]);
        }
        return set;
    }

    /**
     * Creates an array of HashSets of integers out of a double array of integers.
     *
     * @param array The double array of integers to create the array of sets from.
     * @return The array of sets created from the double array.
     */
    private static HashSet<Integer>[] sectionsFromIntArray(int[][] array) {
        HashSet<Integer>[] sections = new HashSet[array.length];
        for (int i = 0; i < array.length; i++) {
            sections[i] = setFromIntArray(array[i]);
        }
        return sections;
    }

    /**
     * Makes a deep copy of an array of HashSets of integers.
     *
     * @param sections The array of sets to make a deep copy of.
     * @return The deep copy of the array of sets.
     */
    private static HashSet<Integer>[] copySections(HashSet<Integer>[] sections) {
        HashSet<Integer>[] copy = new HashSet[sections.length];
        for (int i = 0; i < sections.length; i++) {
            copy[i] = new HashSet<>(sections[i]);
        }
        return copy;
    }

    /**
     * Converts a set of integers to a string representation containing each integer.
     *
     * @param array The set to convert to a string.
     * @return The string representation of the set.
     */
    public static String setToString(HashSet<Integer> array) {
        String str = "{";

        for (int part : array) {
            str += part + " ";
        }

        return str.substring(0, str.length() - 1) + "}";
    }

    /**
     * Converts an array of sets of integers to a string representation containing
     * every set and every integer in each set.
     *
     * @param array The array of sets to convert to a string.
     * @return The string representation of the array of sets.
     */
    public static String sectionsToString(HashSet<Integer>[] array) {
        String str = "{\n";

        for (int i = 0; i < array.length; i++) {
            str += "    " + setToString(array[i]) + "\n";
        }

        return str + "}";
    }
};
